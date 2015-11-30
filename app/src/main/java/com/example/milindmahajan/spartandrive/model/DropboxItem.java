package com.example.milindmahajan.spartandrive.model;

/**
 * Created by milind.mahajan on 11/28/15.
 */
public class DropboxItem {

    String id;
    String name;
    String path;
    String shareLink;
    boolean isFile;
    String created;
    String modified;


    public void setId (String id) {

        this.id = id;
    }

    public String getId () {

        return this.id;
    }

    public void setName (String name) {

        this.name = name;
    }

    public String getName () {

        return this.name;
    }

    public void setPath (String path) {

        this.path = path;
    }

    public String getPath () {

        return this.path;
    }

    public void setShareLink (String shareLink) {

        this.shareLink = shareLink;
    }

    public String getShareLink () {

        return this.shareLink;
    }

    public void setFile (boolean isFile) {

        this.isFile = isFile;
    }

    public boolean isFile () {

        return this.isFile;
    }

    public void setModified (String modified) {

        this.modified = modified;
    }

    public String getModified () {

        return this.modified;
    }

    public void setCreated (String created) {

        this.created = created;
    }

    public String getCreated () {

        return this.created;
    }
}