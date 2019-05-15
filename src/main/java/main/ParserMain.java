package main;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import connection.ConnectionManager;
import connection.models.Information;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Updates.combine;

public class ParserMain {

    public ParserMain() {
        try {
            ConnectionManager collection_to_get_manager = new ConnectionManager();
            MongoCollection<Document> collection_to_get = collection_to_get_manager.UbuntuDB("dataselect")
                    .getCollection("hitosara");

            FindIterable<Document> info_list = collection_to_get.find();

            int data_counter = 0;
            for (Document download_info : info_list) {
                ConnectionManager collection_to_upload_manager = new ConnectionManager();
                MongoCollection<Document> collection_to_upload = collection_to_upload_manager.AtlasDB("master-db")
                        .getCollection("information");

                String source = (String) download_info.get("source");
                String site = (String) download_info.get("site");
                Integer siteGroup = Integer.parseInt((String)download_info.get("siteGroup"));

                Information upload_info = new Information(source,site,siteGroup);
                ((Map<String, String>)download_info.get("data"))
                        .forEach((key, value) -> upload_info.addData(key, value) );

                System.out.println("UPLOADING DATA FROM SOURCE "+upload_info.getSource());
                upload_info.insertThisToCollection(collection_to_upload);

                collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
            }

            collection_to_get_manager.getConnectionBuilder().deactivateConnection();
            System.out.println("UPLOAD SUCCESSFUL! " + data_counter + " DATA UPLOADED! ");
        } catch(Exception exc) {
            System.out.println("ERROR ENCOUNTERED! "+exc.toString());
        }
    }

    public static void main(String[] args) {

        new ParserMain();
    }
}
