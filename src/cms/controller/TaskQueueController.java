package cms.controller;

import cms.engine.connection.crawlserver.ConnectionManager;
import cms.model.Task;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.bson.Document;
import org.bson.types.ObjectId;



public class TaskQueueController {

    @FXML
    public TableView taskTableView;

    private ObservableList<ObservableList> data;

    @FXML
    public void initialize() {

    }


    public void buildData(){
        data = FXCollections.observableArrayList();
        ConnectionManager connectionManager = new ConnectionManager();
        MongoCollection taskCollection = connectionManager.UbuntuDB("dataselect_crawler").getCollection("tasks");

        try{
            //ObservableList<String> column = FXCollections.observableArrayList();
            /*TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>();
            ArrayList<String> columnNames = new ArrayList<>();

            columnNames.add("_id");
            columnNames.add("site");
            columnNames.add("source");
            columnNames.add("siteGroup");
            columnNames.add("pageType");
            columnNames.add("status");
            columnNames.add("crawlerId");
            columnNames.add("dataJSON");
            columnNames.add("dateCreated");

            for(int i = 0; i < columnNames.size(); i++) {
                TableColumn<String, Task> column = new TableColumn<>(columnNames.get(i));
                column.setCellValueFactory(new PropertyValueFactory<>(columnNames.get(i)));
                taskTableView.getColumns().add(column);
            }*/

            FindIterable<Document> iterable = taskCollection.find();
            try (MongoCursor<Document> cursor = iterable.iterator()) {
                while(cursor.hasNext()) {
                    Document doc = cursor.next();

                    Task task = new Task(
                            new ObjectId(doc.getString("_id")),
                            doc.getInteger("siteGroup"),
                            doc.getString("source"),
                            doc.getString("site"),
                            doc.getString("pageType"),
                            doc.getString("status"),
                            doc.getString("crawlerId"),
                            doc.getString("dataJSON"),
                            doc.getDate("dateCreated"));

                    taskTableView.getItems().add(task);
                }
            } catch (Exception exc) {
                //exc.printStackTrace();
            }

            taskTableView.getItems().add(data);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
}
