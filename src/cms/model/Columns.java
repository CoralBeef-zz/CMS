package cms.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    public String val;
    Columns(String val) {
        this.val = val;
    }

    public static String whatJobCategory(String toFind) {
        String category_name = "その他";
        for(String category_path : getCategory1Paths("columns/JOB_SITES_CATEGORY")) {
            HashSet<String> category = loadResourceFileIntoString(category_path, "columns/JOB_SITES_CATEGORY/");
            if(category.contains(toFind)) {
                category_name = category_path;
                break;
            }
        }
        return category_name;
    }

    public static String whatRestaurantCategory(String toFind) {
        String category_name = "その他";
        for(String category_path : getCategory1Paths("columns/RESTAURANT_SITES_CATEGORY")) {
            HashSet<String> category = loadResourceFileIntoString(category_path, "columns/RESTAURANT_SITES_CATEGORY/");
            //System.out.println("Checking in "+category_path+" to find "+toFind+" :: "+category+" ?? "+category.contains(toFind));
            if(category.contains(toFind)) {
                category_name = category_path;
                break;
            }
        }
        return category_name;
    }

    private static ArrayList<String> getCategory1Paths(String folder) {
        ArrayList<String> categories = new ArrayList<>();
        try {
            for (File f : getResourceFolderFiles(folder))
                categories.add(f.getName().replaceFirst("[.][^.]+$", ""));
        } catch(Exception ioexc) {
            System.out.println("Category Resource Not Found! "+ioexc.toString());
        }
        return categories;
    }

    private static File[] getResourceFolderFiles (String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = loader.getResource(folder).getPath();
        return new File(path).listFiles();
    }

    private static HashSet<String> loadResourceFileIntoString(String path, String folder) {
        HashSet<String> data = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Columns.class.getClassLoader().getResourceAsStream(folder +path+".txt")))) {
            String line;
            while ((line = br.readLine()) != null)
                data.add(line);

        } catch(IOException exc) {}
        return data;
    }


}
