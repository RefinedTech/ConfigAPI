package dev.refinedtech.config.proxy;

import dev.refinedtech.config.Configurations;
import dev.refinedtech.config.io.ConfigurationIOHandler;
import dev.refinedtech.config.processing.classes.ProcessedClass;
import dev.refinedtech.config.processing.methods.ProcessedMethod;
import dev.refinedtech.config.processing.methods.info.MethodInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ConfigurationHandler implements InvocationHandler {

    private final ProcessedClass processedClass;
    private final ConfigurationIOHandler ioHandler;

    private final HashMap<Method, Object> childProxies = new HashMap<>();
    private final HashMap<Method, ArrayProxy<?>> childArrayProxies = new HashMap<>();

    public ConfigurationHandler(ProcessedClass processedClass, ConfigurationIOHandler ioHandler) {
        this.processedClass = processedClass;
        this.ioHandler = ioHandler;

        this.ioHandler.init(processedClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.invoke(proxy, method, args, null);
    }

    Object invoke(Object proxy, Method method, Object[] args, List<String> path) throws Throwable {
        ProcessedClass currentClass = this.processedClass.getProcessedClass(method);
        ProcessedMethod processedMethod = currentClass.getProcessedMethod(method);

        if (processedMethod == null) {
            return this.invokeDefault(proxy, method, args, currentClass.getType());
        }

        MethodInfo info = processedMethod.info();
        final List<String> finalPath = path == null ? info.getPathNames() : path;

        Object toReturn = switch (info.getType()) {
            case GETTER -> {

                Class<?> returnType = method.getReturnType();
                if (returnType.isArray() && returnType.getComponentType().getEnclosingClass() == currentClass.getType()) {
                    Object defaultObj = this.invokeDefault(proxy, method, args, currentClass.getType());
                    if (defaultObj != null) {
                        ArrayProxy<?> arrayProxy = this.childArrayProxies.computeIfAbsent(method, m -> {
                            List<String> proxyPath = new ArrayList<>();
                            proxyPath.add(info.getName());
                            proxyPath.addAll(finalPath);

                            return new ArrayProxy<>(
                                defaultObj,
                                returnType.getComponentType(),
                                proxyPath,
                                this
                            );
                        });

                        yield arrayProxy.getObjects();
                    }
                }

                Object child = this.childProxies.get(method);
                if (child != null) {
                    yield child;
                }

                if (returnType.getEnclosingClass() == currentClass.getType()) {
                    Object obj = Configurations.createProxy(
                        returnType,
                        this
                    );

                    this.childProxies.put(method, obj);
                    yield obj;
                }

                yield this.ioHandler.get(finalPath, info.getName());
            }
            case SETTER -> {
                if (args == null || args.length < 1) {
                    throw new IllegalStateException(
                        "Setter '%s' in '%s' requires at least one argument!".formatted(
                            method.getName(),
                            currentClass.getType().getCanonicalName()
                        )
                    );
                }

                this.ioHandler.set(finalPath, info.getName(), args[0]);

                yield null;
            }
            case SAVE -> {
                long start = System.nanoTime();
                this.ioHandler.save(currentClass);
                long end = System.nanoTime();

                if (method.getReturnType() == long.class
                    || method.getReturnType() == Long.class) {
                    yield end - start;
                }
                yield null;
            }
            case LOAD -> {
                long start = System.nanoTime();
                this.ioHandler.load(currentClass);
                long end = System.nanoTime();
                if (method.getReturnType() == long.class
                    || method.getReturnType() == Long.class) {
                    yield end - start;
                }
                yield null;
            }
        };

        if (toReturn == null) {
            return this.invokeDefault(proxy, method, args, currentClass.getType());
        }

        return toReturn;
    }

    private Object invokeDefault(Object proxy, Method method, Object[] args, Class<?> type) throws Throwable {
        if (method.getReturnType() == Void.class) {
            return null;
        }

        if (!method.isDefault()) {
            return null;
        }

        try {
            return InvocationHandler.invokeDefault(proxy, method, args);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException(
                "ConfigAPI cannot access the default method '%s' in '%s'. Try to make '%s' public."
                    .formatted(
                        method.getName(),
                        type.getCanonicalName(),
                        type.getCanonicalName()
                    )
            );
        }
    }
}
