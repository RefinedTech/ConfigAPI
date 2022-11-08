package dev.refinedtech.benchmark;

import dev.refinedtech.config.Configurations;
import dev.refinedtech.config.processing.classes.ProcessedClass;
import dev.refinedtech.config.io.ConfigurationIOHandler;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@State(Scope.Benchmark)
public class SpeedBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    public DatabaseConfig config;

    @Setup(Level.Invocation)
    public void setUp() {
        config = Configurations.createConfig(
            DatabaseConfig.class,
            new ConfigurationIOHandler(new File("./out.txt")) {
                private final HashMap<String, Object> objectHashMap = new HashMap<>();

                @Override
                public void init(ProcessedClass processedClass) {

                }

                @Override
                public Object get(List<String> pathNames, String name) {
                    return this.objectHashMap.get(String.join(".", name) + "." + name);
                }

                @Override
                public void set(List<String> pathNames, String name, Object arg) {
                    this.objectHashMap.put(String.join(".", name) + "." + name, arg);
                }

                @Override
                public void save(ProcessedClass processedClass, List<String> finalPath) {
                    System.out.println("Saving: " + String.join(".", processedClass.getPathNames()));
                }

                @Override
                public void load(ProcessedClass processedClass, List<String> finalPath) {

                }
            });
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkGetDatabase() {
        this.config.getDatabase();
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkSetPort() {
        this.config.setPort(100);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkGetChildAndElement() {
        this.config.getTest().getStuff();
    }

}
