package connection;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnectionBuilder {

    private final MongoClient mongoClient;
    private final CodecRegistry pojoCodecRegistry;

    public MongoDBConnectionBuilder(String username, String password, String host, int port, String authDatabase) {
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(
                        String.format(
                                "mongodb://%s:%s@%s:%d/%s",
                                username,
                                password,
                                host,
                                port,
                                authDatabase
                        )
                ))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
    }

    public MongoDBConnectionBuilder(String connectionString) {
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
    }

    public MongoDatabase getDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
    }
}
