package cms.engine;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import cms.engine.connection.ConnectionManager;
import cms.model.Information;
import org.bson.Document;
import java.util.Map;

public class Uploader {
    public Uploader() {
        ConnectionManager collection_to_get_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_get = collection_to_get_manager.AWSDB("MasterDB")
                .getCollection("information");

        MongoCursor<Document> info_list = collection_to_get.find().noCursorTimeout(true).iterator();

        int data_counter = 0;
        while(info_list.hasNext()) {
            Document download_info = info_list.next();

            System.out.println("Data number "+data_counter+": "+download_info.get("source"));

            ConnectionManager collection_to_upload_manager = new ConnectionManager();
            MongoCollection<Document> collection_to_upload = collection_to_upload_manager.AWSDB("MasterDB")
                    .getCollection("information");

            String source = (String) download_info.get("source");
            String site = (String) download_info.get("site");
            //Integer siteGroup = Integer.parseInt((String)download_info.get("siteGroup"));
            Integer siteGroup = (Integer)download_info.get("siteGroup");

            Information upload_info = new Information(source,site,siteGroup);
            ((Map<String, String>)download_info.get("data"))
                    .forEach((key, value) -> {
                        if(key.equals("mailAddress")) upload_info.addData( key, value.toLowerCase() );
                        else upload_info.addData(key, value);
                    } );

            System.out.println(data_counter+") UPLOADING DATA FROM SOURCE "+upload_info.getSource());
            upload_info.insertThisToCollection(collection_to_upload);

            data_counter++;
            collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
        }

        collection_to_get_manager.getConnectionBuilder().deactivateConnection();
        System.out.println("UPLOAD SUCCESSFUL! " + data_counter + " DATA UPLOADED! ");
        info_list.close();
    }
}
