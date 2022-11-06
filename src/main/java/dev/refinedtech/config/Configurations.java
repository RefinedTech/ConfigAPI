package dev.refinedtech.config;

import dev.refinedtech.config.implementation.processing.methods.MethodProcessorImpl;
import dev.refinedtech.config.processing.classes.ClassProcessor;
import dev.refinedtech.config.proxy.ConfigurationHandler;
import dev.refinedtech.config.io.ConfigurationIOHandler;
import dev.refinedtech.config.proxy.TemporaryHandler;

import java.lang.reflect.Proxy;

public final class Configurations {

    private Configurations() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T createConfig(Class<T> clazz, ConfigurationIOHandler ioHandler) {
        TemporaryHandler temporaryHandler = new TemporaryHandler();

        T proxy = (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[] { clazz },
            temporaryHandler
        );

        ClassProcessor classProcessor = new ClassProcessor(new MethodProcessorImpl(proxy));

        ConfigurationHandler handler = new ConfigurationHandler(
            classProcessor.process(clazz),
            ioHandler
        );

        temporaryHandler.setHandler(handler);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T manuallyCreate(Class<T> clazz, ConfigurationHandler handler) {
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[] { clazz },
            handler
        );
    }

}
