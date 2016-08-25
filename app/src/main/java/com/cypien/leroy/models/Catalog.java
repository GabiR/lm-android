package com.cypien.leroy.models;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class Catalog {
    String title;
    String slug; //identificator
    String pdfURL; //link descarcare pdf
    String contextualURL; //apelare pt detalii catalog
    String websiteURL; //link catalog animat -> de deschis in webview in app
    String coverImageURL; //link imagine coperta;
                          // forma: /1613/130970/pages/f44eab2912eb90a5e127683240a2b6c8e9f6213b
                          // https://view.publitas.com + forma  + -at600.jpg

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public String getContextualURL() {
        return contextualURL;
    }

    public void setContextualURL(String contextualURL) {
        this.contextualURL = contextualURL;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }
}
