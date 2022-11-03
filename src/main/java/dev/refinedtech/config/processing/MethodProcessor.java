package dev.refinedtech.config.processing;

import dev.refinedtech.config.annotations.comment.Comment;
import dev.refinedtech.config.annotations.comment.Comments;
import dev.refinedtech.config.processing.info.MethodInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public abstract class MethodProcessor {

    private final Object proxy;

    protected MethodProcessor(Object proxy) {
        this.proxy = proxy;
    }

    public final ProcessedMethod processMethod(Method method) {
        return new ProcessedMethod(
            this.getComments(method),
            this.getMethodInfo(method)
        );
    }

    private List<String> getComments(Method method) {
        if (method.isAnnotationPresent(Comments.class)) {
            return this.processComments(method.getAnnotation(Comments.class).value(), method);
        }

        if (method.isAnnotationPresent(Comment.class)) {
            return this.processComments(new Comment[] {method.getAnnotation(Comment.class)}, method);
        }

        return List.of();
    }

    protected abstract List<String> processComments(Comment[] comments, Method method);

    protected abstract MethodInfo getMethodInfo(Method method);

    protected final Object invokeDefault(Method method) {
        if (!method.isDefault()) return null;
        if (method.getReturnType() == Void.class) return null;

        try {
            return InvocationHandler.invokeDefault(proxy, method);
        } catch (Throwable exception) {
            throw new IllegalStateException(exception);
        }
    }

}
