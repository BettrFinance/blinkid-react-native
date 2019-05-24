package com.microblink.reactnative.overlays.serialization;

import java.lang.Override;
import android.os.Parcel;
import android.os.Parcelable;

public class MultipleScanSetting implements Parcelable{
  private String description;
  private String overlayImage;
  private String title;
  private String successImage;
  private String successMessage;

  // Constructor
  public MultipleScanSetting(String description, String overlayImage, String title, String successImage, String successMessage){
      this.description = description;
      this.overlayImage = overlayImage;
      this.title = title;
      this.successImage = successImage;
      this.successMessage = successMessage;
  }
  // Getter and setter methods

  // Parcelling part
  public MultipleScanSetting(Parcel in){
    this.description = in.readString();
    this.overlayImage = in.readString();
    this.title = in.readString();
    this.successImage = in.readString();
    this.successMessage = in.readString();
  }

  public int describeContents(){
    return 0;
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    @Override
    public MultipleScanSetting createFromParcel(Parcel in) {
      return new MultipleScanSetting(in);
    }

    @Override
    public MultipleScanSetting[] newArray(int size) {
      return new MultipleScanSetting[size];
    }
  };

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(description);
    parcel.writeString(overlayImage);
    parcel.writeString(title);
    parcel.writeString(successImage);
    parcel.writeString(successMessage);
  }

  public String getDescription() {
    return description;
  }

  public String getOverlayImage() {
    return overlayImage;
  }

  public String getTitle() {
    return title;
  }

  public String getSuccessImage() {
    return successImage;
  }

  public String getSuccessMessage() {
    return successMessage;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setOverlayImage(String overlayImage) {
    this.overlayImage = overlayImage;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSuccessImage(String successImage) {
    this.successImage = successImage;
  }

  public void setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
  }

  public String toString() {
    return "Title: " + title + ", Description: " + description + ", overlayImage: " + overlayImage + ", successImage: " + successImage;
  }
}