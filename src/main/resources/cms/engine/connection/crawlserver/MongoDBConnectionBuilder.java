package main.resources.cms.engine.connection.crawlserver;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.SocketSettings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnectionBuilder {

    private MongoClient mongoClient = null;
    private CodecRegistry pojoCodecRegistry = null;

    public MongoClient getConnection(String username, String password, String host, int port, String authDatabase) {
        deactivateLogging();

        if(this.mongoClient != null) return this.mongoClient;
        else return this.build(username, password, host, port, authDatabase);
    }

    public MongoClient getConnection(String connectionString) {
        deactivateLogging();

        if(this.mongoClient != null) return this.mongoClient;
        else return this.build(connectionString);
    }

    public MongoDatabase getDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
    }

    public MongoClient rebuildConnection(String username, String password, String host, int port, String authDatabase) {
        deactivateLogging();
        deactivateConnection();
        return this.build(username, password, host, port, authDatabase);
    }

    public MongoClient rebuildConnection(String connectionString) {
        deactivateLogging();
        deactivateConnection();
        return this.build(connectionString);
    }

    public void deactivateConnection() {
        mongoClient.close();
        mongoClient = null;
    }

    private MongoClient build(String username, String password, String host, int port, String authDatabase) {
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

        return this.mongoClient;
    }

    private MongoClient build(String connectionString) {
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        return this.mongoClient;
    }

    private void deactivateLogging() {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
    }
}