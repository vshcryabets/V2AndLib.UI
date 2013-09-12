package com.v2soft.V2AndLib.demoapp.database;

import java.util.Date;

/**
 * Demo data item.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class DemoDataItem {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_FULLTEXT = "fulltext";
    public static final String FIELD_IMAGE = "image";
    public static final String FIELD_PUBLISH_DATE = "publishDate";
    public static final String FIELD_LINK = "link";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_ID = "_id";
    
    private String mTitle;
    private String mDescription;
    private String mFulltext;
    private String mImage;
    private Date mPubDate;
    private String mLink;
    private String mAuthor;
    private long mId;

    public DemoDataItem() {
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
    public String getFulltext() {
        return mFulltext;
    }
    public void setFulltext(String mFulltext) {
        this.mFulltext = mFulltext;
    }
    public String getImage() {
        return mImage;
    }
    public void setImage(String mImage) {
        this.mImage = mImage;
    }
    public Date getPubDate() {
        return mPubDate;
    }
    public void setPubDate(Date mPubDate) {
        this.mPubDate = mPubDate;
    }
    public String getLink() {
        return mLink;
    }
    public void setLink(String mLink) {
        this.mLink = mLink;
    }
    public long getId() {
        return mId;
    }
    public void setId(long mId) {
        this.mId = mId;
    }
    public String getAuthor() {
        return mAuthor;
    }
    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

}
