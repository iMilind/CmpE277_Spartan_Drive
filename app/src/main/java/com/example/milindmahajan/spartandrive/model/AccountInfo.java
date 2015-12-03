package com.example.milindmahajan.spartandrive.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dropbox.client2.DropboxAPI;

/**
 * Created by deepakkole on 12/3/15.
 */
public class AccountInfo  implements Parcelable {

    String displayName;
    String email;
    String quota;
    String quotaNormal;
    String quotaShared;
    String freeSpace;

    public AccountInfo () {

    }

    public AccountInfo (DropboxAPI.Account account) {

        this.setDisplayName(account.displayName);
        this.setEmail(account.email);
        this.setQuota(String.valueOf(account.quota/1024/1024));
        this.setQuotaNormal(String.valueOf(account.quotaNormal/1024/1024));
        this.setQuotaShared(String.valueOf(account.quotaShared/1024/1024));
        this.setFreeSpace(String.valueOf((account.quota - (account.quotaNormal + account.quotaShared))/1024/1024));
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getQuotaNormal() {
        return quotaNormal;
    }

    public void setQuotaNormal(String quotaNormal) {
        this.quotaNormal = quotaNormal;
    }

    public String getQuotaShared() {
        return quotaShared;
    }

    public void setQuotaShared(String quotaShared) {
        this.quotaShared = quotaShared;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String getEmail() {

        return email;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(displayName);
        parcel.writeString(email);
        parcel.writeString(quota);
        parcel.writeString(quotaNormal);
        parcel.writeString(quotaShared);
        parcel.writeString(freeSpace);
    }

    public static final Parcelable.Creator<AccountInfo> CREATOR = new Creator<AccountInfo>() {

        public AccountInfo createFromParcel(Parcel source) {

            AccountInfo accountInfo = new AccountInfo();

            accountInfo.setDisplayName(source.readString());
            accountInfo.setEmail(source.readString());
            accountInfo.setQuota(source.readString());
            accountInfo.setQuotaNormal(source.readString());
            accountInfo.setQuotaShared(source.readString());
            accountInfo.setFreeSpace(source.readString());

            return accountInfo;
        }

        public AccountInfo[] newArray(int size) {

            return new AccountInfo[size];
        }
    };
}