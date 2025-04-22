package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Set;

public class UserDto implements Parcelable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private ImageDto image;
    private CartDto cart;
    private Set<OrderDto> orders;

    public UserDto(Long id, String firstName, String lastName, String email, ImageDto image, CartDto cart, Set<OrderDto> orders) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
        this.cart = cart;
        this.orders = orders;
    }


    protected UserDto(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        image = in.readParcelable(ImageDto.class.getClassLoader());
        cart = in.readParcelable(CartDto.class.getClassLoader());
    }

    public static final Creator<UserDto> CREATOR = new Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    public Set<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderDto> orders) {
        this.orders = orders;
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
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeParcelable(image, i);
        parcel.writeParcelable(cart, i);
    }
}
