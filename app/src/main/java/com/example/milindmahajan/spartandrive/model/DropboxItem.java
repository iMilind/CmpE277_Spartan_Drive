package com.example.milindmahajan.spartandrive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.client2.DropboxAPI;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.utils.DateUtil;

/**
 * Created by milind.mahajan on 11/28/15.
 */
public class DropboxItem implements Parcelable {

    String name;
    String path;
    String shareLink;
    boolean isFile;
    String modified;
    String description;


    public DropboxItem() {

    }

    public DropboxItem(DropboxAPI.Entry entry) {

        this.setName(entry.fileName());
        this.setPath(entry.path);
        this.setShareLink("");
        this.setFile(entry.isDir);
        this.setModified(entry.modified);
        this.setDescription("Description of the Dropbox item");
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public String getPath() {

        return this.path;
    }

    public void setShareLink(String shareLink) {

        this.shareLink = shareLink;
    }

    public String getShareLink() {

        return this.shareLink;
    }

    public void setFile(boolean isFile) {

        this.isFile = isFile;
    }

    public boolean isFile() {

        return this.isFile;
    }

    public void setModified(String modified) {

        this.modified = modified;
    }

    public String getModified() {

        return DateUtil.convertDate(this.modified);
    }


    public void setDescription(String description) {

        this.description = description;
    }

    public String getDescription() {

        return this.description;
    }

    public int getIcon() {

        int dotIndex = this.getPath().lastIndexOf(".");
        String fileExt = this.getPath().substring(dotIndex, this.getPath().length() - 1);

        if (fileExt.toLowerCase().contains("doc".toLowerCase())) {

            return R.drawable.doc_icon;
        } else if (fileExt.toLowerCase().contains("xls".toLowerCase())) {

            return R.drawable.xls_icon;
        } else if (fileExt.toLowerCase().contains("pdf".toLowerCase())) {

            return R.drawable.pdf_icon;
        } else if (fileExt.toLowerCase().contains("ppt".toLowerCase())) {

            return R.drawable.ppt_icon;
        } else {

            return R.drawable.def_icon;
        }
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(shareLink);
        parcel.writeString(modified);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<DropboxItem> CREATOR = new Creator<DropboxItem>() {

        public DropboxItem createFromParcel(Parcel source) {

            DropboxItem dropboxItem = new DropboxItem();

            dropboxItem.setName(source.readString());
            dropboxItem.setPath(source.readString());
            dropboxItem.setShareLink(source.readString());
            dropboxItem.setModified(source.readString());
            dropboxItem.setDescription(source.readString());

            return dropboxItem;
        }

        public DropboxItem[] newArray(int size) {

            return new DropboxItem[size];
        }
    };
}