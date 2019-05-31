package model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.or;

public class Information {
    private ObjectId id;
    private Integer siteGroup;
    private String crawledDate;
    private String source;
    private String site;
    private Map<String, Object> data;
    private Date createdAt;
    private Date updatedAt;

    public Information(String source, String site, Integer siteGroup) {
        this.id = new ObjectId();
        this.siteGroup = siteGroup;
        this.source = source;
        this.site = site;
        this.crawledDate = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now());
        this.data = new HashMap<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getSiteGroup() { return this.siteGroup; }

    public void setSiteGroup(int groupNum) { this.siteGroup = groupNum; }

    public String getCrawledDate() { return this.crawledDate; }

    public void setCrawledDate(String crawledDate) { this.crawledDate = crawledDate; }

    public void addData(String key, Object value) {
        if (value instanceof String) value = String.valueOf(value).trim();
        this.data.put(key, value);
    }

    public void removeData(String key) {
        this.data.remove(key);
    }

    public void addDataList(HashMap<Columns, String> data_list) {
        data_list.forEach((column,value) -> addData(column.val, value));
    }

    public void insertThisToCollection(MongoCollection collection) {
        Document doc = new Document();

        //doc.put("_id", getId());
        doc.put("site", getSite());
        doc.put("siteGroup", getSiteGroup());
        doc.put("source", getSource());
        doc.put("crawledDate", getCrawledDate());
        doc.put("data", getData());

        ArrayList<Bson> dataToCheckForNotEqual = new ArrayList<>();
        getData().forEach((column, value) -> dataToCheckForNotEqual.add(ne(column, value)) );
        collection.findOneAndReplace(
                Filters.and(
                        eq("source", getSource()),
                        eq("site", getSite()),
                        or( dataToCheckForNotEqual )
                ),
                doc,
                new FindOneAndReplaceOptions().upsert(true)
        );
    }
}
