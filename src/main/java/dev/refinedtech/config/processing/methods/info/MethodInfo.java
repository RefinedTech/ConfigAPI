package dev.refinedtech.config.processing.methods.info;

import dev.refinedtech.config.utils.ReflectionUtils;
import dev.refinedtech.config.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class MethodInfo {

    public enum MethodType {
        GETTER("get"),
        SETTER("set"),
        SAVE("save", true),
        LOAD("load", true);

        private final String name;
        private final boolean strict;

        MethodType(String name) {
            this.name = name.toLowerCase(Locale.ROOT);
            this.strict = false;
        }

        MethodType(String name, boolean strict) {
            this.name = name.toLowerCase(Locale.ROOT);
            this.strict = strict;
        }

        public final String extractName(String name) {
            if (this.strict) {
                return this.name;
            }

            if (this.matches(name)) {
                return StringUtils.startingLowercase(name.substring(this.name.length()));
            }

            return name;
        }

        public final boolean matches(String name) {
            if (this.strict) {
                return name.toLowerCase(Locale.ROOT).equals(this.name);
            }

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


        this.pathNames = ReflectionUtils.findPath(method.getDeclaringClass());
    }

    public MethodInfo(MethodType type, String name, Method method) {
        this.type = type;
        this.name = name;

        this.pathNames = ReflectionUtils.findPath(method.getDeclaringClass());
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
}
