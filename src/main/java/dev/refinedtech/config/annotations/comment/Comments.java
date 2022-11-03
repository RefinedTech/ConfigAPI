package dev.refinedtech.config.annotations.comment;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Comments {

    Comment[] value();

}
