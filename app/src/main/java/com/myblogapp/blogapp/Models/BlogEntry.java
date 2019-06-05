package com.myblogapp.blogapp.Models;

import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by piasatuito on 3/29/17.
 */

public class BlogEntry extends RealmObject implements Parcelable {

  @PrimaryKey
  private String id;
  private String title;
  private String date;
  private String imagePath;
  private String content;
  private User author;
  private boolean isActive;

  public BlogEntry(){
    // empty constructor
  }


  private BlogEntry(String id, String title, String date, String imagePath, String content, User author){
    this.id = id;
    this.title = title;
    this.date = date;
    this.imagePath = imagePath;
    this.content = content;
    this.author = author;

  }

  protected BlogEntry(Parcel in) {
    id = in.readString();
    title = in.readString();
    date = in.readString();
    imagePath = in.readString();
    content = in.readString();
    isActive = in.readByte() != 0;
    author = in.readParcelable(User.class.getClassLoader());
  }

  public static final Creator<BlogEntry> CREATOR = new Creator<BlogEntry>() {
    @Override public BlogEntry createFromParcel(Parcel in) {
      return new BlogEntry(in);
    }

    @Override public BlogEntry[] newArray(int size) {
      return new BlogEntry[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(id);
    parcel.writeString(title);
    parcel.writeString(date);
    parcel.writeString(imagePath);
    parcel.writeString(content);
    parcel.writeByte((byte) (isActive ? 1 : 0));
    parcel.writeParcelable(this.author, 0);
  }

  public String getID() {
    return id;
  }

  public void setID(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String name) {
    this.title = name;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
