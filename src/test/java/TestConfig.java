import dev.refinedtech.config.Configurations;
import dev.refinedtech.config.processing.classes.ProcessedClass;
import dev.refinedtech.config.io.ConfigurationIOHandler;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TestConfig {

    public static void main(String[] args) throws NoSuchMethodException {
//
//        Object proxy = Proxy.newProxyInstance(Thread.currentThread()
//                                                    .getContextClassLoader(), new Class[]{DatabaseConfig.class}, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                return null;
//            }
//        });
//
//        ClassProcessor classProcessor = new ClassProcessor(methodProcessor);
//        System.out.println(classProcessor.process(DatabaseConfig.class));
//
//        Method method = DatabaseConfig.class.getDeclaredMethod("getDatabaseType");

//        System.out.println(methodProcessor.processMethod(method));

//        TemporaryHandler temporaryHandler = new TemporaryHandler();
//
//        Object proxy = Proxy.newProxyInstance(
//            Thread.currentThread().getContextClassLoader(),
//            new Class[]{DatabaseConfig.class},
//            temporaryHandler
//        );
//
//        ClassProcessor classProcessor = new ClassProcessor(new MethodProcessorImpl(proxy));
//
//        ConfigurationHandler handler = new ConfigurationHandler(
//            classProcessor.process(DatabaseConfig.class),
//            new ConfigurationIOHandler(new File("./out.txt")) {
//                @Override
//                public void init(ProcessedClass processedClass) {
//
//                }
//
//                @Override
//                public Object get(List<String> pathNames, String name) {
//                    return null;
//                }
//
//                @Override
//                public void set(List<String> pathNames, String name, Object arg) {
//
//                }
//
//                @Override
//                public void save(ProcessedMethod processedMethod) {
//
//                }
//            }
//        );
//
//        temporaryHandler.setHandler(handler);

        DatabaseConfig config = Configurations.createConfig(
            DatabaseConfig.class,
            new ConfigurationIOHandler(new File("./out.txt")) {
                private final HashMap<String, Object> objectHashMap = new HashMap<>();

                @Override
                public void init(ProcessedClass processedClass) {
                    System.out.println(processedClass);
                    System.out.println("Initializing!");
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
                    System.out.println("Loading: " + String.join(".", processedClass.getPathNames()));
                }


            });

        DatabaseConfig.Test[] tests = config.getTests();
        tests[0].getStuff();
        tests[1].getStuff();

        DatabaseConfig.Test test = config.getTest();
        System.out.println(test.getStuff());
        System.out.println(test == config.getTest());
        double ms = test.save() / 1_000_000D;
        System.out.println(ms);
        test.load();
    }
}
