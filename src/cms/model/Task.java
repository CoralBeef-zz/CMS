package cms.model;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import java.util.Date;

public class Task {

    private ObjectId id;
    private Integer siteGroup;
    private String source;
    private String site;
    private String pageType;
    private String status;
    private String crawlerId;
    private String dataJSON;
    private Date dateCreated;

    private Task() {}
    public Task(ObjectId id, Integer siteGroup, String source, String site, String pageType, String dataJSON) {
        this.id = id;
        this.siteGroup = siteGroup;
        this.source = source;
        this.site = site;
        this.pageType = pageType;
        this.crawlerId = "";
        this.dateCreated = Instant.now().toDate();
        this.dataJSON = dataJSON;
    }

    public Task(ObjectId id, Integer siteGroup, String source, String site, String pageType, String status, String crawlerId, String dataJSON, Date dateCreated) {
        this.id = id;
        this.siteGroup = siteGroup;
        this.source = source;
        this.site = site;
        this.pageType = pageType;
        this.status = status;
        this.crawlerId = crawlerId;
        this.dataJSON = dataJSON;
        this.dateCreated = dateCreated;
    }

    public ObjectId getId() {
        return id;
    }

    public Integer getSiteGroup() {
        return siteGroup;
    }

    public void setSiteGroup(Integer siteGroup) {
        this.siteGroup = siteGroup;
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

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(String crawlerId) {
        this.crawlerId = crawlerId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getData() {
        return dataJSON;
    }

    public void setData(String dataJSON) {
        this.dataJSON = dataJSON;
    }
}
