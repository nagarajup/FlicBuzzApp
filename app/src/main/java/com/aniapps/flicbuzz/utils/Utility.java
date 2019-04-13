package com.aniapps.flicbuzz.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

public class Utility {
    public static boolean hasMobileNumber(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() == 10) {
            String pattern = "^[6-9][0-9]{9}$";
            return !text.matches(pattern);
        }
        return true;
    }


    public static boolean hasName(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() >= 4 && text.length() < 50) {
            return !text.matches("[a-zA-Z ]+");
        }
        return true;
    }

    public static boolean hasNameFin(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() > 0 && text.length() < 27) {
            return !text.matches("[a-zA-Z ]+");
        }
        return true;
    }

    public static boolean hasIFSC(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() == 11) {
            return !text.matches("^[^\\s]{4}\\d{7}$");
        }
        return true;
    }

    public static boolean hasAccNumber(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() >= 5 && text.length() < 20) {
            return !text.matches("^\\d{5,19}$");

        }
        return true;
    }

    public static boolean validatePassword(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() >= 5 && text.length() < 20) {
            return !text.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

        }
        return true;
    }

    public static boolean validateEmail(EditText editText) {
        String text = editText.getText().toString().trim();
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return !(text.length() != 0 && text.charAt(0) != '_' && text.matches(emailPattern) && text.length() > 5);
    }

    public static void alertDialog(Context context,String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }
    public static boolean isConnectingToInternet (Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
