package com.example.milindmahajan.spartandrive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.client2.DropboxAPI;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.utils.DateUtil;

import java.util.Comparator;

/**
 * Created by milind.mahajan on 11/28/15.
 */
public class DropboxItem implements Parcelable, Comparator<DropboxItem> {

    String name;
    String path;
    String shareLink;
    String isDir;
    String modified;
    String parentPath;
    String size;
    String extension;


    public DropboxItem() {

    }

    public DropboxItem(DropboxAPI.Entry entry) {

        this.setDir(entry.isDir);
        this.setName(entry.fileName());
        this.setPath(entry.path);
        this.setShareLink("");
        this.setModified(entry.modified);
        this.setParentPath(entry.parentPath());
        this.setSize(entry.size);
    }

    public void setName(String name) {

        this.name = name;

        try {

            if(!this.isDir()) {

                int dotIndex = name.lastIndexOf(".");
                String fileExt = name.substring(dotIndex+1, name.length());

                this.setExtension(fileExt);
            }
        } catch (Exception exc) {

            exc.printStackTrace();
        }
    }

    public String getName() {

        return this.name;
    }

    public void setExtension(String extension) {

        this.extension = extension;
    }

    public String getExtension() {

        return this.extension;
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

    public void setDir(boolean isDir) {

        this.isDir = String.valueOf(isDir);
    }

    public boolean isDir() {

        return Boolean.valueOf(this.isDir);
    }

    public void setModified(String modified) {

        this.modified = modified;
    }

    public String getModified() {

        return DateUtil.convertDate(this.modified);
    }

    public String getParentPath() {

        return this.parentPath;
    }

    public void setParentPath(String parentPath) {

        this.parentPath = parentPath;
    }

    public void setSize(String size) {

        this.size = size;
    }

    public String getSize() {

        return this.size;
    }

    public int getIcon() {

        try {

            if (!this.isDir()) {

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
        } catch (Exception exc) {

            return R.drawable.fol_icon;
        }

        return R.drawable.fol_icon;
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(isDir);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(shareLink);
        parcel.writeString(modified);
        parcel.writeString(parentPath);
        parcel.writeString(size);
        parcel.writeString(extension);
    }

    public static final Parcelable.Creator<DropboxItem> CREATOR = new Creator<DropboxItem>() {

        public DropboxItem createFromParcel(Parcel source) {

            DropboxItem dropboxItem = new DropboxItem();

            dropboxItem.setDir(Boolean.valueOf(source.readString()));
            dropboxItem.setName(source.readString());
            dropboxItem.setPath(source.readString());
            dropboxItem.setShareLink(source.readString());
            dropboxItem.setModified(source.readString());
            dropboxItem.setParentPath(source.readString());
            dropboxItem.setSize(source.readString());
            dropboxItem.setExtension(source.readString());

            return dropboxItem;
        }

        public DropboxItem[] newArray(int size) {

            return new DropboxItem[size];
        }
    };

    @Override
    public int compare(DropboxItem lhs, DropboxItem rhs) {

        return lhs.getName().compareTo(rhs.getName());
    }
}