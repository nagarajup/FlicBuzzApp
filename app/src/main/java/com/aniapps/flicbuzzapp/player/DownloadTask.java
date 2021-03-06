package com.aniapps.flicbuzzapp.player;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.aniapps.flicbuzzapp.utils.FlickLoading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by PNR.
 */
public class DownloadTask {


    private static final String TAG = "Download Task";
    private Activity context;

    private String downloadUrl = "", downloadFileName = "";

    public DownloadTask(Activity context, String downloadUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        downloadFileName = downloadUrl.replace("https://www.flicbuzz.com/vendor_videos/original/vendor_3/", "");//Create file name by picking download file name from URL
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FlickLoading.getInstance().show(context);
            FlickLoading.tv_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                FlickLoading.getInstance().dismiss(context);
                FlickLoading.tv_progress.setVisibility(View.GONE);

              /*  playerView.showController();
                playerView.setControllerAutoShow(true);
                playerView.setUseController(true);*/
                if (outputFile != null) {
                    Uri uri = Uri.fromFile(outputFile);
                    Intent videoshare = new Intent(Intent.ACTION_SEND);
                    videoshare.putExtra(Intent.EXTRA_SUBJECT, "FlickBuzz App");
                    videoshare.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hi, I Sharing FlicBuzz - A Complete Entertainment App download link from Google Play! \nhttp://bit.ly/2vH9vub"
                    );
                    videoshare.setType("*/*");
                    videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                    context.startActivity(Intent.createChooser(videoshare, "Share to"));
                    ;
                } else {
                    Toast.makeText(context, "Downloading failed", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
               // Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                   // Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                       //     + " " + c.getResponseMessage());

                }

                //Get File if SD card is present
                if (isSDCardPresent()) {
                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + "FlickBuzz");
                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                   // Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                   // Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
               // Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }

    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
