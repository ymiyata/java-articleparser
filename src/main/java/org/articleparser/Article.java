package org.articleparser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {

    private String title;
    private String link;
    private String snippet;
    private String html;
    private Date date;
    private String sharedBy;
    private String imageURL;
    private String videoURL;

    public Article() {
        title = "";
        link = "";
        snippet = "";
        html = "";
        date = null;
        sharedBy = "";
        imageURL = "";
        videoURL = "";
    }

    public Article(String _title, String _link, String _snippet, String _html,
                   Date _date, String _sharedBy) {
        title = _title;
        link = _link;
        snippet = _snippet;
        html = _html;
        date = _date;
        sharedBy = _sharedBy;
        imageURL = "";
        videoURL = "";
    }

    public Article(String _title, String _link, String _snippet, String _html,
                   Date _date, String _sharedBy, String _image, String _video) {
        title = _title;
        link = _link;
        snippet = _snippet;
        html = _html;
        date = _date;
        sharedBy = _sharedBy;
        imageURL = _image;
        videoURL = _video;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() { 
        return link;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getHTML() {
        return html;
    }

    public Date getDate() {
        return date;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setDate(Date _date) {
        date = _date;
    }

    public void setSharedBy(String _sharedBy) {
        sharedBy = _sharedBy;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String publishedDate = date != null ? sdf.format(date) : "";
        return publishedDate + "\n" + title + "\n" + snippet;
    }
}
