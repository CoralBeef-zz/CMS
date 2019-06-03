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
}
