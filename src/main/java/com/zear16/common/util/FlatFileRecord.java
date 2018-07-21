package com.zear16.common.util;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlatFileRecord {

    int length() default 0;

    char padding() default ' ';

    FlatFileField.Align align() default FlatFileField.Align.LEFT;

    NewLine newLine() default NewLine.UNIX;

    enum NewLine {
        DOS,
        UNIX
    }

}
