package com.zear16.common.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.*;

public class FlatFile {

    private static final Logger LOGGER = Logger.getLogger(FlatFile.class.getSimpleName());

    private Class mClazz;

    private Map<Field,Method> mFieldSetterMaps;

    private Map<Field,Method> mFieldGetterMaps;

    private Map<Integer,Integer> mOffsetLengthMaps;

    private FlatFileRecord.NewLine mNewLine = FlatFileRecord.NewLine.UNIX;

    private char mPadding = ' ';

    private FlatFileField.Align mAlign = FlatFileField.Align.LEFT;

    private int mRecordSize = -1;

    private char mLine[];

    public <T> FlatFile(Class<T> mClazz) throws Exception {
        this.mClazz = mClazz;
        Object obj = mClazz.newInstance();
        Field fields[] = mClazz.getDeclaredFields();
        mFieldSetterMaps = new HashMap<>();
        mFieldGetterMaps = new HashMap<>();
        mOffsetLengthMaps = new HashMap<>();

        FlatFileRecord flatFileRecord = mClazz.getAnnotation(FlatFileRecord.class);
        if (flatFileRecord != null) {
            mNewLine = flatFileRecord.newLine();
            mPadding = flatFileRecord.padding();
            mRecordSize = flatFileRecord.length();
            if (flatFileRecord.align() != FlatFileField.Align.NOT_SET) {
                mAlign = flatFileRecord.align();
            }
        }

        // Verify offset and length overlapped
        for (Field field : fields) {
            FlatFileField flatFileField = field.getAnnotation(FlatFileField.class);
            if (flatFileField != null) {
                if (flatFileField.offset() < 0) {
                    LOGGER.error("field [" + field.getName() + "] param [offset] must start with 0");
                    throw new InvalidParameterException("offset must start with 0");
                }
                if (flatFileField.length() <= 0) {
                    LOGGER.error("field [" + field.getName() + "] param [length] must more than 0");
                    throw new InvalidParameterException("length must more than 0");
                }

                if (flatFileRecord == null || flatFileRecord.length() <= 0) {
                    // Caller Not supply record length
                    if (flatFileField.offset() + flatFileField.length() > mRecordSize) {
                        mRecordSize = flatFileField.offset() + flatFileField.length();
                    }
                } else if (flatFileField.offset() + flatFileField.length() > flatFileRecord.length()) {
                    LOGGER.error("field [" + field.getName() + "] overflow");
                    throw new InvalidParameterException("offset or length overflow");
                }

                for (Map.Entry entry : mOffsetLengthMaps.entrySet()) {
                    int start = (Integer)entry.getKey();
                    int end = start + (Integer)entry.getValue();
                    for (int i = 0; i < flatFileField.length(); i++) {
                        if (flatFileField.offset() + i >= start && flatFileField.offset() + i < end) {
                            LOGGER.error(
                                    "field [" + field.getName() + "] offset and length has overlapped at "
                                            + (flatFileField.offset() + i));
                            throw new InvalidParameterException("offset and length has overlapped");
                        }
                    }
                }
                mOffsetLengthMaps.put(flatFileField.offset(), flatFileField.length());

                // Find Method
                Method method = findSetterMethod(
                        obj, field.getName(), flatFileField.setter(), field.getType());
                if (method == null) {
                    LOGGER.error("field [" + field.getName() + "] have no setter method");
                    throw new NoSuchMethodException();
                }
                mFieldSetterMaps.put(field, method);

                method = findGetterMethod(
                        obj, field.getName(), flatFileField.getter());
                if (method == null) {
                    LOGGER.error("field [" + field.getName() + "] have no getter method");
                    throw new NoSuchMethodException();
                }
                mFieldGetterMaps.put(field, method);
            }
        }

        mLine = new char[mRecordSize];
    }

    // TODO : Verify thread safe
    public <T> String toString(List<T> list) throws Exception {
        if (list != null) {
            StringBuilder buffer = new StringBuilder();
            for (T obj : list) {
                buffer.append(toString(obj)).append(mNewLine);
            }
            return buffer.toString();
        }
        return null;
    }

    public <T> String toString(T obj)
            throws InvalidParameterException, InvocationTargetException, IllegalAccessException {
        if (!mClazz.isInstance(obj)) {
            LOGGER.error(
                    "Object type [" + obj.getClass().getSimpleName()
                            + "] not applicable for FlatFile type [" + mClazz.getSimpleName() + "]");
            throw new InvalidParameterException("Invalid object type");
        }
        Arrays.fill(mLine, mPadding);
        for (Map.Entry<Field, Method> entry : mFieldGetterMaps.entrySet()) {
            Field field = entry.getKey();
            Method method = entry.getValue();
            FlatFileField flatFileField = field.getAnnotation(FlatFileField.class);
            Object value = method.invoke(obj);
            if (value instanceof String) {
                String sValue = (String) value;
                int offset = flatFileField.offset();
                int length = flatFileField.length();
                // If String longer than configuration we will trim it
                if (sValue.length() > length) {
                    sValue = sValue.substring(0, length);
                }
                char padding = mPadding;
                if (flatFileField.padding() != '\u0000') {
                    padding = flatFileField.padding();
                }
                FlatFileField.Align align = mAlign;
                if (flatFileField.align() != FlatFileField.Align.NOT_SET) {
                    align = flatFileField.align();
                }
                int c = 0;
                for (int i = 0; i < length; i++) {
                    if (sValue.length() < length &&
                            ((align == FlatFileField.Align.LEFT && i >= sValue.length()) ||
                                    (align == FlatFileField.Align.RIGHT && i < (length - sValue.length()))
                            )) {
                        // padding
                        mLine[i + offset] = padding;
                    } else {
                        mLine[i + offset] = sValue.charAt(c++);
                    }
                }
            } else {
                // TODO : Support More Data Type
            }
        }
        return new String(mLine);
    }

    public <T> T parseLine(String line) throws Exception {
        T result = (T) mClazz.newInstance();
        Iterator<Map.Entry<Field, Method>> iterator = mFieldSetterMaps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, Method> entry = iterator.next();
            Field field = entry.getKey();
            Method method = entry.getValue();
            FlatFileField flatFileField = field.getAnnotation(FlatFileField.class);
            if (!StrUtil.isNullOrEmpty(line) &&
                    line.length() >= flatFileField.offset() + flatFileField.length()) {
                String s = line.substring(
                        flatFileField.offset(),
                        flatFileField.offset() + flatFileField.length());
                method.invoke(result, s);
            }
            iterator.remove();
        }
        return result;
    }

    public <T> List<T> parse(String data) throws Exception {
        List<T> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(data);
        for (;;) {
            String line = tokenizer.getNextToken('\n');
            if (line == null) {
                break;
            }
            T obj = parseLine(line);
            list.add(obj);
        }
        return list;
    }

    public int getRecordSize() {
        return mRecordSize;
    }

    // TODO : Support getter with parameter
    // TODO : Support boolean type (is)

    private static synchronized Method findSetterMethod(
            Object obj,
            String property,
            String methodName,
            Class paramType) {
        return findMethod(obj, property, methodName, "set", paramType);
    }

    private static synchronized Method findGetterMethod(
            Object obj,
            String property,
            String methodName) {
        return findMethod(obj, property, methodName, "get", null);
    }

    private static synchronized Method findMethod(
            Object obj,
            String property,
            String methodName,
            String prefix,
            Class paramType) {
        Method m;
        Class<?> theClass = obj.getClass();
        if (StrUtil.isNullOrEmpty(methodName)) {
            methodName = String.format(prefix + "%C%s", property.charAt(0), property.substring(1));
        }
        if (paramType == null) {
            // Getter
            try {
                m = theClass.getMethod(methodName);
                return m;
            } catch (NoSuchMethodException ex) {

            }
        }
        // Setter
        while (paramType != null) {
            try {
                m = theClass.getMethod(methodName, paramType);
                return m;
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class clazz : paramType.getInterfaces()) {
                    try {
                        m = theClass.getMethod(methodName, clazz);
                        return m;
                    } catch (NoSuchMethodException ex1) {

                    }
                }
                paramType = paramType.getSuperclass();
            }
        }
        return null;
    }
}
