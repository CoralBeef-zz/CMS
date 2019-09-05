package cms.model;

import cms.engine.connection.crawlserver.ConnectionManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public enum Columns {

    //JOB SITE CATEGORIES
    COMPANYNAME("companyName"),
    BRANCHNAME("branchName"),
    ADDRESS("address"),
    ZIPCODE("zipcode"),
    PHONENUMBER("phoneNumber"),
    FAX("fax"),
    MAILADDRESS("mailAddress"),
    CATEGORY1("category1"),
    CATEGORY2("category2"),
    HOMEPAGE("homepage"),
    PREFECTURE("prefecture"),
    CITY("city"),
    WARD("ward"),
    TOWN("town"),
    DISTRICT("district"),
    BLOCK("block"),
    ESTABLISHEDYEAR("establishedYear"),
    CAPITAL("capital"),
    SPECMAILADDRESS("specMailAddress"),
    CONTACTPAGE("contactPage"),
    NEWGRADUATESITE("newGraduateSite"),

    //RESTAURANT SITE CATEGORIES
    STORENAME("storeName"),
    //ADDRESS("address"),
    //PREFECTURE("prefecture"),
    //CITY("city"),
    //WARD("ward"),
    //TOWN("town"),
    //DISTRICT("district"),
    //BLOCK("block"),
    //PHONENUMBER("phoneNumber"),
    //HOMEPAGE("homepage"),
    ACCESS("access"),
    STOREGENRE("storeGenre"),
    FOODGENRE("foodGenre"),
    BUSINESSHOURS("businessHours"),
    REGULARHOLIDAY("regularHoliday"),
    OPENINGDATE("openingDate"),
    STOREINTRODUCTION("storeIntroduction"),
    NUMBEROFSEATS("numberOfSeats"),
    PRIVATEROOM("privateRoom"),
    PARKINGLOT("parkingLot"),
    CREDITCARD("creditCard"),
    FREEWIFI("freeWifi"),
    SNS("sns"),
    //MAILADDRESS("mailAddress")

    ;

    public static ArrayList<String> colArrayToString(ArrayList<Columns> colArray) {
        ArrayList<String> out = new ArrayList<>();
        for(Columns col : colArray) out.add(col.val);
        return out;
    }

    public static Integer convertCategoryToIndex(String categoryName) {
        switch (categoryName) {
            case "Job Sites": return 1;
            case "Restaurant Sites": return 2;
            default: return 0;
        }
    }

    @SuppressWarnings("Duplicates")
    public static ArrayList<Columns> jobSiteColumnList() {
        ArrayList<Columns> columns = new ArrayList<>();

        columns.add(COMPANYNAME);
        columns.add(BRANCHNAME);
        columns.add(ADDRESS);
        columns.add(ZIPCODE);
        columns.add(PHONENUMBER);
        columns.add(FAX);
        columns.add(MAILADDRESS);
        columns.add(CATEGORY1);
        columns.add(CATEGORY2);
        columns.add(HOMEPAGE);
        columns.add(PREFECTURE);
        columns.add(CITY);
        columns.add(WARD);
        columns.add(TOWN);
        columns.add(DISTRICT);
        columns.add(BLOCK);
        columns.add(ESTABLISHEDYEAR);
        columns.add(CAPITAL);
        columns.add(SPECMAILADDRESS);
        columns.add(CONTACTPAGE);
        columns.add(NEWGRADUATESITE);

        return columns;
    }

    public static ArrayList<Document> getColumnsFromDatabase() {
        ConnectionManager collection_to_get_manager = new ConnectionManager();
        MongoCollection<Document> collection = collection_to_get_manager.AWSDB("DataSelectDB")
                .getCollection("columns");

        MongoCursor<Document> info_list = collection.find(Filters.eq("bigCategory", 1)).noCursorTimeout(true).iterator();
        ArrayList<Document> out = new ArrayList<>();

        while(info_list.hasNext()) out.add(info_list.next());

        return out;
    }

    @SuppressWarnings("Duplicates")
    public static ArrayList<Columns> restaurantSiteColumnList() {
        ArrayList<Columns> columns = new ArrayList<>();

        columns.add(STORENAME);
        columns.add(ADDRESS);
        columns.add(PREFECTURE);
        columns.add(CITY);
        columns.add(WARD);
        columns.add(TOWN);
        columns.add(DISTRICT);
        columns.add(BLOCK);
        columns.add(PHONENUMBER);
        columns.add(HOMEPAGE);
        columns.add(ACCESS);
        columns.add(STOREGENRE);
        columns.add(FOODGENRE);
        columns.add(BUSINESSHOURS);
        columns.add(REGULARHOLIDAY);
        columns.add(OPENINGDATE);

        columns.add(STOREINTRODUCTION);
        columns.add(NUMBEROFSEATS);
        columns.add(PRIVATEROOM);
        columns.add(PARKINGLOT);
        columns.add(CREDITCARD);
        columns.add(FREEWIFI);
        columns.add(SNS);
        columns.add(MAILADDRESS);

        return columns;
    }

    public static HashMap<String, ArrayList<Columns>> getAllColumnsOnServer() {
        HashMap<String, ArrayList<Columns>> cols = new HashMap<>();

        return cols;
    }

    public static HashMap<String, ArrayList<Columns>> getAllColumns() {
        HashMap<String, ArrayList<Columns>> out = new HashMap<>();
        out.put("Job Sites", jobSiteColumnList());
        out.put("Restaurant Sites", restaurantSiteColumnList());
        return out;
    }

    public String val;
    Columns(String val) {
        this.val = val;
    }
}
