package io.github.jmillanacosta.retractable.classes;

public class RetractedArticle {
    public String id;
    public String pmcid;
    public String articleAbstract;
    public String articleFullText;
    public String url;
    public String retractionReason;
    public final static String USER_AGENT = "Mozilla/5.0";
    public final static String BASE_URL = "https://www.ebi.ac.uk/europepmc/webservices/rest/%s/fullTextXML";

    public void setPmcId(String pmcid) {
        this.pmcid = pmcid;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public void setArticleFullText(String articleFullText) {
        this.articleFullText = articleFullText;
    }

    public void setURL(String url){
        this.url = url;
    }
    public String getId() {
        return this.id;
    }
    public String getPmcid() {
        return this.pmcid;
    }

    public String getArticleAbstract() {
        return this.articleAbstract;
    }

    public String getArticleFullText() {
        return this.articleFullText;
    }

    public String getURL() {
        return this.url.toString();
    }
    public void setRetractionReason(){
        //TODO: check how they describe the reasons, come up with patterns or search strategy
        String reason = new String();
        this.retractionReason = reason;
    }
    public String getRetractionReason(){
        return this.retractionReason;
    }
}
