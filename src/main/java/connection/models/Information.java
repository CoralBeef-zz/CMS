package connection.models;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Map;

public class Information {
    private ObjectId id;
    private Integer siteGroup;
    private String crawledDate;
    private String source;
    private String site;
    private Map<String, Object> data;
    private Date createdAt;
    private Date updatedAt;

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
}
