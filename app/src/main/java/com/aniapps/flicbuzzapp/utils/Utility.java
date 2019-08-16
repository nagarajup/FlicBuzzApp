package com.aniapps.flicbuzzapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.activities.LoginActivity;
import com.aniapps.flicbuzzapp.activities.SettingsActivity;
import com.aniapps.flicbuzzapp.activities.SignIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utility {

    public static String sharedKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp2nqxrzrQ1TtlUZVkxlJ6EbwHLHJovAE8POca7aH/Thum9aLQpGOteHd5H5C6nd8cekVz8Q4AB0hqouPp1wWo67FhdZJtrxAMMH5NAAFcFBjsib/OztFe2smjLVLytHRaG1mNxg9wu8zEYkB4XyBlo/7URKTruR041RB00rTfsfDbqQ8IcwAgjnzXF3cz38wc5Pr59/QEXXZEy+XQt/RgGrm+x7xwbws2Uw1qoctGsFWRxWGQHF9g3s+6IvtFGfZK4r9h+aazDqFQDCwALwzwtDYF60vqx01aZJkMLFlLFsi6PzCdbJ4PYFi1ViYZsl2JtEkXCluo3CWDEmSbaxbuwIDAQAB";
    public static boolean purchaseFlag;
    public static final String tendaysubs = "10days";
    public static final String six_months = "sixmonths_sub";
    public static final String six_months_trail = "sixmonths_sub_trail";
    public static final String one_year = "oneyear_subs";
    public static final String one_year_latest = "one_year_latest_plan";
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
        return text.length() >= 8;

      /*  if (text.length() >= 8 && text.length() < 20) {
        //    return !text.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
            return true;

        }
        return true;*/
    }

    public static boolean validateEmail(EditText editText) {
        String text = editText.getText().toString().trim();
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return !(text.length() != 0 && text.charAt(0) != '_' && text.matches(emailPattern) && text.length() > 5);
    }

    public static void alertDialog(final Context context, String title, String msg) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(context);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.alert_dialog);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_title);
        txt_alert_title.setText(title);
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_msg);
        LinearLayout main = (LinearLayout) alert_dialog.findViewById(R.id.main);
        Button txt_ok = (Button) alert_dialog.findViewById(R.id.ok);
        txt_ok.setVisibility(View.GONE);
        Button txt_cancel = (Button) alert_dialog.findViewById(R.id.cancel);
        main.setVisibility(View.VISIBLE);
        txt_alert_description.setText(msg);
        txt_cancel.setText("OK");
        txt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                alert_dialog.dismiss();
            }
        });

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();

            }
        });

        alert_dialog.show();
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


    public static void alertDialog(final Context context, String msg) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(context);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.alert_dialog);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_title);
        txt_alert_title.setText("NOTICE");
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.custom_alert_dialog_msg);
        LinearLayout main = (LinearLayout) alert_dialog.findViewById(R.id.main);
        Button txt_ok = (Button) alert_dialog.findViewById(R.id.ok);
        txt_ok.setVisibility(View.GONE);
        Button txt_cancel = (Button) alert_dialog.findViewById(R.id.cancel);
        main.setVisibility(View.VISIBLE);
        txt_alert_description.setText(msg);
        txt_cancel.setText("OK");
        txt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();
                if(!(context instanceof LoginActivity)) {
                    PrefManager.getIn().clearLogins();
                    //PrefManager.getIn().setLogin(false);
                    Intent intent = new Intent(context, SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        });

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dialog.dismiss();

            }
        });

        alert_dialog.show();
    }

}
