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
}