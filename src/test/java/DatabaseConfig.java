import dev.refinedtech.config.annotations.NotSection;
import dev.refinedtech.config.annotations.alias.Alias;
import dev.refinedtech.config.annotations.alias.GetterAlias;
import dev.refinedtech.config.annotations.comment.Comment;

@Alias("database")
@NotSection
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

    default DatabaseConnection getDatabase() {
        return new DatabaseConnection(getPort(), getUrl(), getUser(), getPass());
    }

    @Comment("The port to connect to")
    @Comment("Default value: ${DEFAULT}")
    default int getPort() {
        return 3316;
    }

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
}