package dev.refinedtech.config.utils;

import dev.refinedtech.config.annotations.NotSection;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {

    }

    public static List<Class<?>> findRootInterface(Member member) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> parent = member.getDeclaringClass();

        while (parent != null && parent.isInterface()) {
            if (!parent.isAnnotationPresent(NotSection.class)) {
                classes.add(parent);
            }
            parent = parent.getDeclaringClass();
        }

        return classes;
    }

}
