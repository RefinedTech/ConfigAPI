import dev.refinedtech.config.annotations.alias.GetterAlias;
import dev.refinedtech.config.annotations.alias.SetterAlias;
import dev.refinedtech.config.annotations.comment.Comment;
import dev.refinedtech.config.processing.MethodProcessor;
import dev.refinedtech.config.processing.info.MethodInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class TestConfig {

    public static void main(String[] args) throws NoSuchMethodException {

        Object proxy = Proxy.newProxyInstance(Thread.currentThread()
                                                    .getContextClassLoader(), new Class[]{DatabaseConfig.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

        MethodProcessor processor = new MethodProcessor(proxy) {
            @Override
            protected List<String> processComments(Comment[] comments, Method method) {
                String def = String.valueOf(invokeDefault(method));
                List<String> lines = new ArrayList<>();

                Class<?> returnType = method.getReturnType();
                String[] enumConstants = null;
                if (returnType.isEnum()) {

                    Object[] constants = returnType.getEnumConstants();
                    enumConstants = new String[constants.length];

                    for (int i = 0; i < constants.length; i++) {
                        enumConstants[i] = constants[i].toString();
                    }
                }


                for (Comment comment : comments) {
                    if (comment.valueJoiner().isBlank()) {
                        lines.add(comment.value().replace("${DEFAULT}", def));
                    }

                    if (comment.valueJoiner().isBlank()) {
                        continue;
                    }

                    if (enumConstants != null) {
                        String joiner = comment.valueJoiner();;
                        boolean isFormattable = joiner.contains("%s");

                        for (String enumConstant : enumConstants) {
                            if (isFormattable) {
                                lines.add(joiner.replace("%s", enumConstant));
                                continue;
                            }

                            lines.add(joiner + enumConstant);
                        }
                    }
                }

                return lines;
            }

            @Override
            protected MethodInfo getMethodInfo(Method method) {
                MethodInfo.MethodType type = MethodInfo.MethodType.getType(method.getName());

                if (method.isAnnotationPresent(GetterAlias.class)) {
                    if (type == MethodInfo.MethodType.SETTER) {
                        throw new IllegalStateException("Setter '%s' cannot have the annotation @GetterAlias."
                            .formatted(method.getName()));
                    }

                    String name = method.getAnnotation(GetterAlias.class).value();
                    return new MethodInfo(MethodInfo.MethodType.GETTER, name, method);
                }

                if (method.isAnnotationPresent(SetterAlias.class)) {
                    if (type == MethodInfo.MethodType.GETTER) {
                        throw new IllegalStateException("Getter '%s' cannot have the annotation @SetterAlias."
                            .formatted(method.getName()));
                    }

                    String name = method.getAnnotation(SetterAlias.class).value();
                    return new MethodInfo(MethodInfo.MethodType.SETTER, name, method);
                }

                return new MethodInfo(method);
            }
        };


        Method method = DatabaseConfig.class.getDeclaredMethod("getDatabaseType");

        System.out.println(processor.processMethod(method));
    }
}
