package com.aniapps.flicbuzzapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static String sharedKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp2nqxrzrQ1TtlUZVkxlJ6EbwHLHJovAE8POca7aH/Thum9aLQpGOteHd5H5C6nd8cekVz8Q4AB0hqouPp1wWo67FhdZJtrxAMMH5NAAFcFBjsib/OztFe2smjLVLytHRaG1mNxg9wu8zEYkB4XyBlo/7URKTruR041RB00rTfsfDbqQ8IcwAgjnzXF3cz38wc5Pr59/QEXXZEy+XQt/RgGrm+x7xwbws2Uw1qoctGsFWRxWGQHF9g3s+6IvtFGfZK4r9h+aazDqFQDCwALwzwtDYF60vqx01aZJkMLFlLFsi6PzCdbJ4PYFi1ViYZsl2JtEkXCluo3CWDEmSbaxbuwIDAQAB";
    public static boolean purchaseFlag;
    public static final String tendaysubs = "10days";
    public static final String six_months = "sixmonths_sub";
    public static final String six_months_trail = "sixmonths_sub_trail";
    public static final String one_year = "oneyear_subs";
    public static final String one_year_trail = "oneyear_subs_trail";
    public static final String threemonths = "threemonthsub";
    public static final String threemonths_threedaytrail = "threemonths_threedaytrail";

    public static boolean hasMobileNumber(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text.length() == 10) {
            String pattern = "^[6-9][0-9]{9}$";
            return !text.matches(pattern);
        }
        return true;
    }

    public static long getMilliSeconds(String date_str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(date_str);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
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
            return !text.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

        }
        return true;
    }

    public static boolean validateEmail(EditText editText) {
        String text = editText.getText().toString().trim();
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return !(text.length() != 0 && text.charAt(0) != '_' && text.matches(emailPattern) && text.length() > 5);
    }

    public static void alertDialog(final Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        //builder.setNegativeButton("NO", null);
        builder.show();
    }

    public static boolean isConnectingToInternet(Context context) {
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
