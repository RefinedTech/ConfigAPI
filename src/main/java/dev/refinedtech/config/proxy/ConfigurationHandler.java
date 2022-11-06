package dev.refinedtech.config.proxy;

import dev.refinedtech.config.Configurations;
import dev.refinedtech.config.io.ConfigurationIOHandler;
import dev.refinedtech.config.processing.classes.ProcessedClass;
import dev.refinedtech.config.processing.methods.ProcessedMethod;
import dev.refinedtech.config.processing.methods.info.MethodInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class ConfigurationHandler implements InvocationHandler {

    private final ProcessedClass processedClass;
    private final ConfigurationIOHandler ioHandler;

    private final HashMap<Class<?>, Object> childProxies = new HashMap<>();

    public ConfigurationHandler(ProcessedClass processedClass, ConfigurationIOHandler ioHandler) {
        this.processedClass = processedClass;
        this.ioHandler = ioHandler;

        this.ioHandler.init(processedClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ProcessedClass currentClass = this.processedClass.getProcessedClass(method);
        ProcessedMethod processedMethod = currentClass.getProcessedMethod(method);

        if (processedMethod == null) {
            return this.invokeDefault(proxy, method, args, currentClass.getType());
        }



        MethodInfo info = processedMethod.info();

        Object toReturn = switch (info.getType()) {
            case GETTER -> {

                Class<?> returnType = method.getReturnType();
                Object child = this.childProxies.get(returnType);
                if (child != null) {
                    yield child;
                }

                if (returnType.getEnclosingClass() == currentClass.getType()) {
                    Object obj = Configurations.manuallyCreate(
                        returnType,
                        this
                    );

                    this.childProxies.put(returnType, obj);
                    yield obj;
                }

                Object value = this.ioHandler.get(info.getPathNames(), info.getName());
                if (value != null) {
                    yield value;
                }

                yield null;
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

                this.ioHandler.set(info.getPathNames(), info.getName(), args[0]);

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
