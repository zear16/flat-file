package com.zear16.common.util;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlatFileField {

    int offset() default -1;

    int length() default -1;

    String setter() default "";

    String getter() default "";

    Align align() default Align.NOT_SET;

    char padding() default '\u0000';

    enum Align {
        LEFT,
        RIGHT,
        NOT_SET
    }
}
