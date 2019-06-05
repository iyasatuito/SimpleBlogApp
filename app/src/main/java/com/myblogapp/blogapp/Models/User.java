package com.myblogapp.blogapp.Models;

import android.os.Parcel;
import android.os.Parcelable;
import com.myblogapp.blogapp.Utils.RealmController;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by piasatuito on 3/29/17.
 */

public class User extends RealmObject implements Parcelable {

  @PrimaryKey
  private String name;
  private String password;
  private RealmList<BlogEntry> blogEntries;
  private RealmList<User> blockUserList;

  public User(){
    //empty constructor
  }

  public User(String name, String password){
    this.name = name;
    this.password = password;
  }

  protected User(Parcel in) {
    name = in.readString();
    password = in.readString();
    blogEntries = new RealmList<>();
    blockUserList = new RealmList<>();
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override public User createFromParcel(Parcel in) {
      return new User(in);
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(name);
    parcel.writeString(password);
    parcel.writeTypedList(blogEntries);
    parcel.writeTypedList(blockUserList);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public RealmList<BlogEntry> getBlogEntries() {
    //return blogEntries;

    RealmList <BlogEntry> blogEntries = new RealmList<>();
    RealmResults<BlogEntry> blogEntryRealmResults = RealmController.getInstance().getRealm().where(BlogEntry.class).equalTo("author.name",name).findAll();

    blogEntries.addAll(blogEntryRealmResults.subList(0, blogEntryRealmResults.size()));
   return blogEntries;

  }

  public void setBlogEntries(RealmList<BlogEntry> blogEntries) {
    this.blogEntries = blogEntries;
  }

  public RealmList<User> getBlockUserList() {
    return blockUserList;
  }

  public void setBlockUserList(RealmList<User> blockUserList) {
    this.blockUserList = blockUserList;
  }
}
