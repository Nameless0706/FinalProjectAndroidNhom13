package com.nhom13.phonemart.dto;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ImageDto implements Parcelable {
	private Long id;
	private String name;
	private String downloadUrl;

	public ImageDto(Long id, String name, String downloadUrl) {
		this.id = id;
		this.name = name;
		this.downloadUrl = downloadUrl;
	}

	protected ImageDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		name = in.readString();
		downloadUrl = in.readString();
	}

	public static final Creator<ImageDto> CREATOR = new Creator<ImageDto>() {
		@Override
		public ImageDto createFromParcel(Parcel in) {
			return new ImageDto(in);
		}

		@Override
		public ImageDto[] newArray(int size) {
			return new ImageDto[size];
		}
	};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel parcel, int i) {
		if (id == null) {
			parcel.writeByte((byte) 0);
		} else {
			parcel.writeByte((byte) 1);
			parcel.writeLong(id);
		}
		parcel.writeString(name);
		parcel.writeString(downloadUrl);
	}
}
