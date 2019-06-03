package cms.engine.connection;

import com.mongodb.client.MongoDatabase;

public class ConnectionManager {

    private MongoDBConnectionBuilder connectionBuilder = new MongoDBConnectionBuilder();

    public MongoDatabase LocalDB(String database_name) {
        connectionBuilder.getConnection(
                "dataselect",
                "d4t4s3l3ct",
                "localhost",
                27017,
                "admin"
        );
        return connectionBuilder.getDatabase(database_name);
    }

    public MongoDatabase UbuntuDB(String database_name) {
        final String ubuntuConnectionString = "mongodb://192.168.0.159:27017/admin?retryWrites=true";
        connectionBuilder.getConnection(ubuntuConnectionString);
        return connectionBuilder.getDatabase(database_name);
    }

    public MongoDatabase AtlasDB(String database_name) {
        final String atlasConnectionString = "mongodb+srv://dataselect:d4t4s3l3ct@listingcluster-7hi3m.mongodb.net/test?retryWrites=true";
        connectionBuilder.getConnection(atlasConnectionString);
        return connectionBuilder.getDatabase(database_name);
    }

    public MongoDatabase AWSDB(String database_name) {
        connectionBuilder.getConnection(
                "admin",
                "bit%40okut%40m%40594",
                "ec2-13-113-108-57.ap-northeast-1.compute.amazonaws.com",
                27017,
                "admin"
        );
        return connectionBuilder.getDatabase(database_name);
    }

    public MongoDBConnectionBuilder getConnectionBuilder() {
        return this.connectionBuilder;
    }
}
