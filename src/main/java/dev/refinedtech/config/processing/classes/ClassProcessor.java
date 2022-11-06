package dev.refinedtech.config.processing.classes;

import dev.refinedtech.config.annotations.Ignored;
import dev.refinedtech.config.processing.methods.MethodProcessor;
import dev.refinedtech.config.processing.methods.ProcessedMethod;
import dev.refinedtech.config.utils.ReflectionUtils;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ClassProcessor {

    private final MethodProcessor methodProcessor;

    public ClassProcessor(MethodProcessor methodProcessor) {
        this.methodProcessor = methodProcessor;
    }

    public ProcessedClass process(Class<?> clazz) {
        return new ProcessedClass(
            clazz,
            this.processMethods(clazz),
            this.fetchChildren(clazz),
            ReflectionUtils.findPath(clazz)
        );
    }

    private Map<Method, ProcessedMethod> processMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
            .filter(method -> !method.isAnnotationPresent(Ignored.class))
            .collect(Collectors.toMap(Function.identity(), this.methodProcessor::processMethod));

    }

    private Map<Class<?>, ProcessedClass> fetchChildren(Class<?> clazz) {
        return Arrays.stream(clazz.getClasses())
            .filter(Class::isInterface)
            .collect(Collectors.toMap(Function.identity(), this::process));
    }

}
