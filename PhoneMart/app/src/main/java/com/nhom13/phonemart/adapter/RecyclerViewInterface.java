package com.nhom13.phonemart.adapter;

import android.net.Uri;

public interface RecyclerViewInterface {

    //default là từ khóa để giúp cho method đó ko cần phải override lại (ko bị dư thừa)
    default void onItemClick(int position, String source){};

    default void onImageClick(Uri imageUri){};


    //default void onCartItemChange(CartItem cartItem){};
}
