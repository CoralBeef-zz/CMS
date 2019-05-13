package connection;

import com.mongodb.client.MongoDatabase;

public class ConnectionManager {

    public static MongoDatabase LocalDB(String database_name) {
        MongoDBConnectionBuilder mongoDBConnectionBuilder = new MongoDBConnectionBuilder(
                "dataselect",
                "d4t4s3l3ct",
                "localhost",
                27017,
                "admin"
        );
        return mongoDBConnectionBuilder.getDatabase(database_name);
    }

    public static MongoDatabase UbuntuDB(String database_name) {
        /*final String atlasConnectionString = "mongodb://192.168.0.159:27017/admin?retryWrites=true";
        MongoDBConnectionBuilder atlasClusterConnectionBuilder = new MongoDBConnectionBuilder(atlasConnectionString);
        return atlasClusterConnectionBuilder.getDatabase(database_name);*/
        MongoDBConnectionBuilder mongoDBConnectionBuilder = new MongoDBConnectionBuilder(
                "dataselect",
                "d4t4s3l3ct",
                "192.168.0.159",
                27017,
                "admin"
        );
        return mongoDBConnectionBuilder.getDatabase(database_name);
    }

    public static MongoDatabase AtlasDB(String database_name) {
        final String atlasConnectionString = "mongodb+srv://dataselect:d4t4s3l3ct@listingcluster-7hi3m.mongodb.net/test?retryWrites=true";
        MongoDBConnectionBuilder atlasClusterConnectionBuilder = new MongoDBConnectionBuilder(atlasConnectionString);
        return atlasClusterConnectionBuilder.getDatabase(database_name);
    }
}
