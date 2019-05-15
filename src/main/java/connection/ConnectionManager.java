package connection;

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
        //MongoDBConnectionBuilder atlasClusterConnectionBuilder = new MongoDBConnectionBuilder(atlasConnectionString);
        connectionBuilder.getConnection(ubuntuConnectionString);
        //return atlasClusterConnectionBuilder.getDatabase(database_name);
        return connectionBuilder.getDatabase(database_name);
    }

    //Default is master-db
    //WARNING: THIS IS NOT ADVISABLE TO USE
    //We use a separate program for uploading
    public MongoDatabase AtlasDB(String database_name) {
        final String atlasConnectionString = "mongodb+srv://dataselect:d4t4s3l3ct@listingcluster-7hi3m.mongodb.net/test?retryWrites=true";
        //MongoDBConnectionBuilder atlasClusterConnectionBuilder = new MongoDBConnectionBuilder(atlasConnectionString);
        connectionBuilder.getConnection(atlasConnectionString);
        //return atlasClusterConnectionBuilder.getDatabase(database_name);
        return connectionBuilder.getDatabase(database_name);
    }

    public MongoDBConnectionBuilder getConnectionBuilder() {
        return this.connectionBuilder;
    }
}
