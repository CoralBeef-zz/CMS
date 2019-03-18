package main;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import connection.ConnectionManager;
import connection.models.Information;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class ParserMain {

    public ParserMain() {
        try {
            MongoCollection<Information> collection_to_get = ConnectionManager.getLocalDB().getCollection("information", Information.class);
            MongoCollection<Information> collection_to_upload = ConnectionManager.getAtlasDB().getCollection("information", Information.class);

            FindIterable<Information> info_list = collection_to_get.find();


            int data_counter = 0;
            for (Information info : info_list) {
                String mailAddress = (String) info.getData().get("mailAddress");
                String homepage = (String) info.getData().get("homepage");

                //DISABLED FOR NOW
                /*
                Set<String> xx = new HashSet();

                if(mailAddress.equals("") && !homepage.equals("")) {
                    //run Scanner here
                    Scanner pageScanner = new Scanner();
                    pageScanner.scanDomain(homepage, 0);

                    for (String links : pageScanner.getList()) {
                        xx.add(links);
                        //System.out.println("Link: "+links);
                    }

                    System.out.println(mailAddress + " / " + homepage);
                    break;

                }

                for(String xs : xx) System.out.println(xs);*/


                System.out.println("UPLOADING TO SERVER: " + info.getData());
                collection_to_upload.findOneAndReplace(combine(
                        eq("source", info.getSource()),
                        eq("site", info.getSite())
                        ),
                        info,
                        new FindOneAndReplaceOptions().upsert(true));

                data_counter++;
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
