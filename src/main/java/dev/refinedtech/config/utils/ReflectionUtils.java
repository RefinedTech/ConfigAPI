package dev.refinedtech.config.utils;

import dev.refinedtech.config.annotations.Ignored;
import dev.refinedtech.config.annotations.alias.Alias;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {

    }

    public static List<Class<?>> findRootInterface(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        if (!clazz.isInterface()) {
            return classes;
        }

        classes.add(clazz);
        Class<?> parent = clazz.getDeclaringClass();

        while (parent != null && parent.isInterface()) {
            if (!parent.isAnnotationPresent(Ignored.class)) {
                classes.add(parent);
            }
            parent = parent.getDeclaringClass();
        }

        return classes;
    }

    public static List<String> findPath(Class<?> targetClass) {
        List<String> path = ReflectionUtils.findRootInterface(targetClass)
                                           .stream()
                                           .filter(clazz -> !clazz.isAnnotationPresent(Ignored.class))
                                           .map(clazz -> {
                                               if (clazz.isAnnotationPresent(Alias.class)) {
                                                   return clazz.getAnnotation(Alias.class).value();
                                               }

                                               return clazz.getSimpleName();
                                           })
                                           .map(StringUtils::startingLowercase)
                                           .toList();
        if (path.isEmpty()) return path;

        List<String> res = new ArrayList<>(path);
        Collections.reverse(res);
        return res;
    }

}
