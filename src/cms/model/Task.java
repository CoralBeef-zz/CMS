package cms.model;

import org.bson.types.ObjectId;
import org.joda.time.Instant;
import java.util.Date;

public class Task {

    private ObjectId id;
    private Integer siteGroup;
    private String source;
    private String site;
    private String partitionString;
    private String pageType;
    private String status;
    private String crawlerId;
    private Date dateCreated;

    private Task() {}
    public Task(ObjectId id, Integer siteGroup, String source, String site, String partitionString, String pageType) {
        this.id = id;
        this.siteGroup = siteGroup;
        this.source = source;
        this.site = site;
        this.partitionString = partitionString;
        this.pageType = pageType;
        this.crawlerId = "";
        this.dateCreated = Instant.now().toDate();
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

    public String getPartitionString() {
        return partitionString;
    }

    public void setPartitionString(String partitionString) {
        this.partitionString = partitionString;
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



}
