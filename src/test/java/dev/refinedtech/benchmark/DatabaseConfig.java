package dev.refinedtech.benchmark;

import dev.refinedtech.config.annotations.Ignored;
import dev.refinedtech.config.annotations.alias.GetterAlias;
import dev.refinedtech.config.annotations.comment.Comment;

@Ignored
public interface DatabaseConfig {

    enum DatabaseType {
        MYSQL,
        REDIS,
        MONGODB
    }

    @Comment("How should the database be handled?")
    @Comment("Possible values: ")
    @Comment(valueJoiner = "- ")
    @Comment
    @Comment("Default value: ${DEFAULT}")
    @GetterAlias("type")
    default DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }

    @Ignored
    default DatabaseConnection getDatabase() {
        return new DatabaseConnection(getPort(), getUrl(), getUser(), getPass());
    }

    @Comment("The port to connect to")
    @Comment("Default value: ${DEFAULT}")
    default int getPort() {
        return 3316;
    }

    void setPort(int port);

    @Comment("The url to connect to")
    @Comment("Default value: ${DEFAULT}")
    default String getUrl() {
        return "localhost";
    }

    @Comment("The user to use")
    @Comment("Default value: ${DEFAULT}")
    default String getUser() {
        return "root";
    }

    @Comment("The password to use")
    @Comment("Default value: ${DEFAULT}")
    default String getPass() {
        return "";
    }

    void save();

    Test getTest();

    interface Test {
        long save();
        void load();

        default String getStuff() {
            return "LALALALLAA";
        }

        interface Test2 {
            void save();
        }
    }
}