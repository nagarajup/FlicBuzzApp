package com.aniapps.flicbuzz.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.aniapps.flicbuzz.Base;
import com.aniapps.flicbuzz.R;

public class FlickLoading extends Base {
    public static Dialog dialog;
    private static FlickLoading mInstance;
    FlickProgress cpb;

    public static synchronized FlickLoading getInstance() {
        if (mInstance == null) {
            mInstance = new FlickLoading();
        }
        return mInstance;
    }

    public void show(Context context) {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.flickprogress);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        cpb = (FlickProgress) dialog.findViewById(R.id.progress);
        cpb.startAnimation();
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss(Activity act) {
        if (dialog != null && dialog.isShowing()) {
            if (!act.isFinishing()) {
                try {
                    dialog.dismiss();
                    cpb.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dialog = null;
            }
        }
    }
}