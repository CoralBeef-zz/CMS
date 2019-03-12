package connection;

import com.mongodb.client.MongoDatabase;

public class ConnectionManager {

    public static MongoDatabase getLocalDB() {
        MongoDBConnectionBuilder mongoDBConnectionBuilder = new MongoDBConnectionBuilder(
                "dataselect",
                "d4t4s3l3ct",
                "localhost",
                27017,
                "admin"
        );
        return mongoDBConnectionBuilder.getDatabase("dataselect");
    }

    public static MongoDatabase getAtlasDB() {
        final String atlasConnectionString = "mongodb+srv://dataselect:d4t4s3l3ct@listingcluster-7hi3m.mongodb.net/test?retryWrites=true";
        MongoDBConnectionBuilder atlasClusterConnectionBuilder = new MongoDBConnectionBuilder(atlasConnectionString);
        return atlasClusterConnectionBuilder.getDatabase("master-db");
    }
}
