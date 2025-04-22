package com.nhom13.phonemart.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.nhom13.phonemart.R;

public class DialogUtils {
    public static void ShowDialog(Context context, int layoutResId,String title, String message){

        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(layoutResId, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        TextView titleTv = dialogView.findViewById(R.id.errorTitleTv);
        TextView messageTv = dialogView.findViewById(R.id.errorDescTv);
        Button okBtn = dialogView.findViewById(R.id.errorDoneBtn);

        titleTv.setText(title);
        messageTv.setText(message);

        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        int width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 250, context.getResources().getDisplayMetrics());

        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
