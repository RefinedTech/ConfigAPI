package dev.refinedtech.config.io;

import dev.refinedtech.config.processing.classes.ProcessedClass;

import java.io.File;
import java.util.List;

public abstract class ConfigurationIOHandler {

    private final File file;

    protected ConfigurationIOHandler(File file) {
        this.file = file;
    }

    public abstract void init(ProcessedClass processedClass);

    public abstract Object get(List<String> pathNames, String name);

    public abstract void set(List<String> pathNames, String name, Object arg);

    public abstract void save(ProcessedClass processedClass, List<String> finalPath);
    public abstract void load(ProcessedClass processedClass, List<String> finalPath);

    protected File getFile() {
        return this.file;
    }
}
