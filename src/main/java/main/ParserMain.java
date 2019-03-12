package main;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import connection.ConnectionManager;
import connection.models.Information;
import org.bson.Document;

public class ParserMain {

    public ParserMain() {
        MongoCollection<Information> collection_to_get = ConnectionManager.getLocalDB().getCollection("information", Information.class);
        MongoCollection<Information> collection_to_upload = ConnectionManager.getAtlasDB().getCollection("information", Information.class);

        FindIterable<Information> info_list = collection_to_get.find();


        int x = 0;
        for(Information info : info_list) {
            String mailAddress = (String)info.getData().get("mailAddress");
            String homepage = (String)info.getData().get("homepage");

            if(mailAddress.equals("") && !homepage.equals("")) {
                //run Scanner here

                System.out.println(mailAddress + " / " + homepage);
            }

            x++;
            if(x > 30) break;
        }

    }

    public static void main(String[] args) {

        new ParserMain();
    }
}
