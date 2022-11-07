package dev.refinedtech.config.proxy;

import dev.refinedtech.config.Configurations;
import dev.refinedtech.config.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ArrayProxy<T> {

    private final T[] objects;

    @SuppressWarnings("unchecked")
    protected ArrayProxy(Object obj, Class<T> type, List<String> path, ConfigurationHandler handler) {
        this.objects = (T[]) ReflectionUtils.arrayFromObject(obj);

        if (this.objects == null) {
            return;
        }

        for (int i = 0; i < this.objects.length; i++) {
            List<String> clone = new ArrayList<>(path);
            clone.add("element_" + i);

            this.objects[i] = Configurations.createProxy(
                type,
                new ArrayObjectHandler(clone, handler)
            );
        }
    }

    protected T[] getObjects() {
        return this.objects;
    }

    private record ArrayObjectHandler(List<String> path, ConfigurationHandler handler) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Accessing: " + this.path);
            return this.handler.invoke(proxy, method, args, this.path);
        }

    }
}
