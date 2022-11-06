package dev.refinedtech.config.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class TemporaryHandler implements InvocationHandler {

    private InvocationHandler handler;

    public void setHandler(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.handler != null) {
            return this.handler.invoke(proxy, method, args);
        }
        return null;
    }
}
