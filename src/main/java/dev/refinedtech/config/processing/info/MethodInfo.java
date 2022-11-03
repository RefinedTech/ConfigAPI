package dev.refinedtech.config.processing.info;

import dev.refinedtech.config.annotations.alias.Alias;
import dev.refinedtech.config.utils.ReflectionUtils;
import dev.refinedtech.config.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MethodInfo {

    public enum MethodType {
        GETTER("get"),
        SETTER("set");

        private final String name;

        MethodType(String name) {
            this.name = name.toLowerCase(Locale.ROOT);
        }

        public final String extractName(String name) {
            if (this.matches(name)) {
                return StringUtils.startingLowercase(name.substring(this.name.length()));
            }

            return name;
        }

        public final boolean matches(String name) {
            return name.toLowerCase(Locale.ROOT).startsWith(this.name);
        }

        public static MethodType getType(String name) {
            for (MethodType type : values()) {
                if (type.matches(name)) {
                    return type;
                }
            }

            return null;
        }
    }

    private final MethodType type;
    private final String name;
    private final List<String> pathNames;

    public MethodInfo(Method method) {
        String methodName = method.getName();

        this.type = MethodType.getType(methodName);

        if (this.type == null) {
            throw new IllegalStateException("Cannot create MethodType for '%s'".formatted(methodName));
        }

        this.name = this.type.extractName(methodName);


        this.pathNames = getPathNames(method);
    }

    public MethodInfo(MethodType type, String name, Method method) {
        this.type = type;
        this.name = name;

        this.pathNames = getPathNames(method);
    }

    public MethodType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getPathNames() {
        return pathNames;
    }

    private final List<String> getPathNames(Method method) {
        return ReflectionUtils.findRootInterface(method)
                              .stream()
                              .map(clazz -> {
                                  if (clazz.isAnnotationPresent(Alias.class)) {
                                      return clazz.getAnnotation(Alias.class).value();
                                  }

                                  return clazz.getSimpleName();
                              })
                              .map(StringUtils::startingLowercase)
                              .sorted(Collections.reverseOrder())
                              .toList();
    }
}
