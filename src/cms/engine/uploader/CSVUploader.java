package cms.engine.uploader;

import cms.engine.connection.crawlserver.ConnectionManager;
import cms.model.Information;
import com.mongodb.client.MongoCollection;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CSVUploader extends javafx.concurrent.Task{

    private File file;

    private String collectionName;
    private Integer categoryIndex;
    private HashMap<String, String> columns;

    public CSVUploader(File file) {
        this.file = file;
    }

    public CSVUploader(File file, String collectionName, Integer categoryIndex, HashMap<String, String> columns) {
            this.file = file;
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

        FileInputStream fis = new FileInputStream(this.file);
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

    @SuppressWarnings("Duplicates")
    public void upload() throws IOException{
        if (this.file != null) {
            FileInputStream fis = new FileInputStream(this.file);
            InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
            Reader reader = new BufferedReader(isr);

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                ConnectionManager collection_to_upload_manager = new ConnectionManager();
                MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                        .getCollection(this.collectionName);


                Information nextRow = new Information("CSVData_"+(new ObjectId().toString()), this.collectionName, this.categoryIndex);
                Map<String, Object> nextRowData = new HashMap<>();
                this.columns.forEach((key, storeIn) -> nextRowData.put(storeIn, record.get(key)));
                nextRow.setData(nextRowData);

                nextRow.insertThisToCollection(collection_to_upload);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void upload(String collectionName, Integer categoryIndex, HashMap<String, String> columns) throws IOException{
        if (this.file != null) {
            FileInputStream fis = new FileInputStream(this.file);
            InputStreamReader isr = new InputStreamReader(fis, "Shift-JIS");
            Reader reader = new BufferedReader(isr);

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                ConnectionManager collection_to_upload_manager = new ConnectionManager();
                MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                        .getCollection(collectionName);


                Information nextRow = new Information("CSVData_"+(new ObjectId().toString()), collectionName, categoryIndex);
                Map<String, Object> nextRowData = new HashMap<>();
                columns.forEach((key, storeIn) -> nextRowData.put(storeIn, record.get(key)));
                nextRow.setData(nextRowData);

                nextRow.insertThisToCollection(collection_to_upload);
            }
        }
    }


}
