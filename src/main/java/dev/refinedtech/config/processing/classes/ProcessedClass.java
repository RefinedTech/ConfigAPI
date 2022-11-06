package dev.refinedtech.config.processing.classes;

import dev.refinedtech.config.processing.methods.ProcessedMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public final class ProcessedClass {

    private final Class<?> clazz;
    private final Map<Method, ProcessedMethod> methods;
    private final Map<Class<?>, ProcessedClass> children;
    private final List<String> pathNames;

    public ProcessedClass(Class<?> clazz,
                          Map<Method, ProcessedMethod> methods,
                          Map<Class<?>, ProcessedClass> children,
                          List<String> pathNames) {
        this.clazz = clazz;
        this.methods = methods;
        this.children = children;
        this.pathNames = pathNames;
    }

    public ProcessedClass getProcessedClass(Method method) {
        if (method.getDeclaringClass() == this.clazz) return this;

        return this.children.get(method.getDeclaringClass()).getProcessedClass(method);
    }

    public ProcessedMethod getProcessedMethod(Method method) {
        return this.methods.get(method);
    }

    public Map<Class<?>, ProcessedClass> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int offset) {
        StringBuilder sb = new StringBuilder("  ".repeat(offset));
        sb.append("Processed Class:\n\n");

        String basicOffset = "  ".repeat(offset + 1);
        sb.append(basicOffset).append("Class: ").append(this.clazz.getName()).append("\n\n");
        if (!this.methods.isEmpty()) {
            sb.append(basicOffset).append("Methods:\n");
            this.methods.forEach((method, processedMethod) -> sb
                .append(processedMethod.toString(offset + 2))
                .append('\n'));
        }

        if (!this.children.isEmpty()) {
            sb.append(basicOffset).append("Children:\n");
            this.children.forEach((cl, processedClass) -> sb
                .append(processedClass.toString(offset + 2))
                .append('\n'));
        }

        return sb.toString();
    }

    public Class<?> getType() {
        return this.clazz;
    }

    public List<String> getPathNames() {
        return pathNames;
    }
}
