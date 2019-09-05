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
import java.util.*;

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

    public void upload() throws IOException{
        System.out.println("Testing Upload!");
        Information nextRow = new Information("CSVData_"+(new ObjectId().toString()), this.collectionName, this.categoryIndex);
        Map<String, Object> nextRowData = new HashMap<>();

        ConnectionManager collection_to_upload_manager = new ConnectionManager();
        MongoCollection<Document> collection_to_upload = collection_to_upload_manager.UbuntuDB("dataselect")
                .getCollection(this.collectionName);

        System.out.println("Testing Upload! Point 2");
        for(File file : this.fileList) {

            System.out.println("Testing Upload! Point 3: "+file.getAbsolutePath());
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
            System.out.println("Sucessful Insert?!");
            nextRow.setData(nextRowData);
            //nextRow.insertThisToCollection(collection_to_upload);
        }

        collection_to_upload_manager.getConnectionBuilder().deactivateConnection();
    }

}
