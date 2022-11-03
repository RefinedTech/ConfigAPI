package dev.refinedtech.config.annotations.comment;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(Comments.class)
public @interface Comment {

    String value() default "";
    String valueJoiner() default "";

}
