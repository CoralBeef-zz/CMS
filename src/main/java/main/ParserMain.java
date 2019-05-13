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
            MongoCollection<Document> collection_to_get = ConnectionManager.UbuntuDB("dataselect")
                    .getCollection("hitosara");
            MongoCollection<Document> collection_to_upload = ConnectionManager.LocalDB("dataselect")
                    .getCollection("information2");

            FindIterable<Document> info_list = collection_to_get.find();

            int data_counter = 0;
            for (Document download_info : info_list) {
                String source = (String) download_info.get("source");
                String site = (String) download_info.get("site");
                Integer siteGroup = Integer.parseInt((String)download_info.get("siteGroup"));

                Information upload_info = new Information(source,site,siteGroup);
                ((Map<String, String>)download_info.get("data"))
                        .forEach((key, value) -> upload_info.addData(key, value) );

                System.out.println("UPLOADING DATA FROM SOURCE "+upload_info.getSource());
                upload_info.insertThisToCollection(collection_to_upload);
            }
            System.out.println("UPLOAD SUCCESSFUL! " + data_counter + " DATA UPLOADED! ");
        } catch(Exception exc) {
            System.out.println("ERROR ENCOUNTERED! "+exc.toString());
        }
    }

    public static void main(String[] args) {

        new ParserMain();
    }
}
