package main.resources.cms.engine.uploader;

import main.resources.cms.controller.v2controllers.UploaderControllers.CsvUploaderController;
import main.resources.cms.engine.connection.crawlserver.ConnectionManager;
import main.resources.cms.engine.helpers.FXWindowTools;
import main.resources.cms.model.Columns;
import main.resources.cms.model.Information;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.*;

import static main.resources.cms.engine.helpers.FXWindowTools.*;

public class CSVUploader extends javafx.concurrent.Task{

    private ArrayList<File> fileList = new ArrayList<>();
    private File fileForHeader;

    private String collectionName;
    private Integer categoryIndex;
    private HashMap<String, String> columns;

    public CSVUploader(File file) { this.fileForHeader = file; }

    public CSVUploader(String collectionName, Integer categoryIndex, HashMap<String, String> columns, ArrayList<File> files) {
            for(File file : files) this.fileList.add(file);
            this.collectionName = collectionName;
            this.categoryIndex = categoryIndex;
            this.columns = columns;
    }

    @Override
    protected Void call() {
        try {
            upload();
        } catch(IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getHeaders() throws IOException{
        ArrayList<String> headers = new ArrayList<>();

        FileInputStream fis = new FileInputStream(this.fileForHeader);
        InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
        Reader reader = new BufferedReader(isr);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
        CSVRecord headerList = (((CSVParser) records).getRecords().get(0));
        Iterator iterator = headerList.iterator();

        while(iterator.hasNext()) {
            String nextHeader = iterator.next().toString();
            headers.add(nextHeader);
        }

        return headers;
    }

    /*public static void upload(ObservableList<Node> boxes, String collectionName, Integer categoryIndex, File ... fileList) throws IOException{
        Map<String, Object> nextRowData = new HashMap<>();

        ConnectionManager collection_to_upload_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                .getCollection(collectionName);

        for(File file : fileList) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
                    Reader reader = new BufferedReader(isr);
                    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    Information nextRow = new Information("CSVData_" + (new ObjectId().toString()), collectionName, categoryIndex);

                    for (Node boxx : boxes) {
                        if (boxx instanceof CsvUploaderController.BuilderBox) {
                            CsvUploaderController.BuilderBox box = (CsvUploaderController.BuilderBox) boxx;
                            putWithFilters(nextRowData, box, csvRecord);
                        }
                    }

                    if (nextRowData.size() > 0) {
                        nextRow.setData(nextRowData);
                        System.out.println("Information: "+ new Gson().toJson(nextRow));
                        nextRow.insertThisToCollection(collection_to_upload);
                    }
                }
            }
        }



        collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
    }*/

    @SuppressWarnings("Duplicates") public static void upload(ObservableList<Node> boxes, String collectionName, Integer categoryIndex, File ... fileList) throws IOException{
        Map<String, Object> nextRowData = new HashMap<>();

        ConnectionManager collection_to_upload_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                .getCollection(collectionName);

        for(File file : fileList) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
                    Reader reader = new BufferedReader(isr);
                    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    Information nextRow = new Information("CSVData_" + (new ObjectId().toString()), collectionName, categoryIndex);

                    for (Node boxx : boxes) {
                        if (boxx instanceof CsvUploaderController.BuilderBox) {
                            CsvUploaderController.BuilderBox box = (CsvUploaderController.BuilderBox) boxx;
                            putWithFilters(nextRowData, box, csvRecord);
                        }
                    }

                    if (nextRowData.size() > 0) {
                        nextRow.setData(nextRowData);
                        System.out.println("Information: "+ new Gson().toJson(nextRow));
                        nextRow.insertThisToCollection(collection_to_upload);
                    }
                }
            }
        }



        collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
    }

    public static void putWithFilters(Map<String, Object> nextRowData, CsvUploaderController.BuilderBox box, CSVRecord csvRecord) {
        String targetColumn = box.columnName();

        if(targetColumn.equals(Columns.NEWGRADUATESITE.val)) {
            nextRowData.put(targetColumn, box.dataString());
        } else if(targetColumn.equals(Columns.CAPITAL.val)) {
            String targetData = csvRecord.get(box.dataString());

            if(targetData != null && !targetData.equals("")) {
                Long bigCapitalDigit = Long.parseLong(targetData.replaceAll(",", ""));

                nextRowData.put(targetColumn, bigCapitalDigit);
            }
        } else if(targetColumn.equals(Columns.ADDRESS.val)) {

            String targetData = csvRecord.get(box.dataString());

            nextRowData.put(targetColumn, targetData);
            Map<Columns, String> map = FXWindowTools.splittedAddress(targetData);

            map.forEach((key, value) -> nextRowData.put(key.val, value));
        }
        else {
            String targetData = csvRecord.get(box.dataString());
            nextRowData.put(targetColumn, targetData);
        }
    }

    public void upload() throws IOException{
        Information nextRow = new Information("CSVData_"+(new ObjectId().toString()), this.collectionName, this.categoryIndex);
        Map<String, Object> nextRowData = new HashMap<>();

        ConnectionManager collection_to_upload_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                .getCollection(this.collectionName);

        for(File file : this.fileList) {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
            Reader reader = new BufferedReader(isr);

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);


            for (CSVRecord record : records) {
                this.columns.forEach((key, storeIn) -> nextRowData.put(storeIn, record.get(key)));
            }

        }

        if(nextRowData.size() > 0) {
            nextRow.setData(nextRowData);
            nextRow.insertThisToCollection(collection_to_upload);
        }

        collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
    }

}
