package in.emoji.emosyn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Bitmap.CompressFormat.JPEG;

/**
 * Created by navdeep on 7/21/2017.
 */

public class Imagers {

    private static boolean delete;

    private static final String FileAuthority = "in.emoji.fileprovider";

    private static final String TAG = "in.emoji.EmoSyn.Imagers";

    /**
     * create a temp file for image storage
     *
     * @param context The application context
     * @return temp image File
     * @throws IOException
     */
    @NonNull
    static File createTemp(Context context){

        File f =null;
        final Context thisCon=context;

        String timestamp = new SimpleDateFormat("ddmmyyyy_hhmmss").format(new Date());

    String name = "temp" + "_" + timestamp;
        try {
            f = File.createTempFile(name, ".jpg", thisCon.getExternalCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (f.exists()) {

            Log.e(TAG, "File created at" + f.getAbsolutePath());


        } else
            Log.e(TAG, "File created");




       // boolean deito = false;
        return f;


    }

    /**
     * delets the temp or any image file
     *
     * @param context
     * @param imagePath
     * @return boolean
     */

    static boolean deleteFile(Context context, String imagePath) {
        File currentImage = new File(imagePath);

        if (currentImage.exists()) {

                    delete = currentImage.delete();


            if (!delete) {
                Toast.makeText(context, "File couldn't be deleted", Toast.LENGTH_SHORT).show();

            } else Log.i(TAG, "File deleted from cache");

        }
        return delete;

    }

    /**
     * Fires the share intent
     *
     * @param context
     * @param imagePath
     * @return boolean
     * @deprecated  mereged in MainActivity for better performance
     */
    static void shareit(Context context, String imagePath) {

        File imageFile = new File(imagePath);
        Intent shareIN = new Intent(Intent.ACTION_SEND);
        Log.i(TAG, "Share intent for file created ");
        boolean result = false;

        shareIN.setType("image/*");
        Uri ursuri = FileProvider.getUriForFile(context, FileAuthority, imageFile);
        shareIN.putExtra(Intent.EXTRA_STREAM, ursuri);
        context.startActivity(shareIN);


    }


    /**
     * To reduce memory overhead Image Re-sampler
     *
     * @param context   Application context
     * @param imagePath path to image file
     * @return resampled image file Bitmap
     */

    static Bitmap iCompress(Context context, final String imagePath) throws FileNotFoundException {
final String temp_path=imagePath;
        DisplayMetrics DM = new DisplayMetrics();
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        w.getDefaultDisplay().getMetrics(DM);
        // Dimens of display
   final     int reqHeight = DM.heightPixels;
     final   int reqWidhth = DM.widthPixels;

    final    BitmapFactory.Options BMO = new BitmapFactory.Options();
        BMO.inJustDecodeBounds = true;
new Thread(new Runnable() {
    @Override
    public void run() {
        BitmapFactory.decodeFile(temp_path, BMO);
    }
}).start();


        Log.i(TAG, "Bitmap decoded");
        //Dimens of image
     final   int imgHeight = BMO.outHeight;
       final int imgWidth = BMO.outWidth;

        // sacling factor

      int  scale_fac = Math.min(imgHeight / reqHeight, imgWidth / reqWidhth);



//  resize decode and return
        BMO.inJustDecodeBounds = false;
        BMO.inSampleSize = scale_fac;
        Log.i(TAG, "Returning scaled bitmap");

                Bitmap b=BitmapFactory.decodeFile(imagePath);

        return b;
    }

    /**
     * Saves the final image returned
     *
     * @param context application context
     * @param bitmap  image file to be saved
     * @return string   path to the final created file
     * @throws IOException
     */
    static String saveImage(Context context, Bitmap bitmap) {

        Boolean success = true;

        String filepath = " ";

        String filename = new SimpleDateFormat("ddmmyyy_hhmmss", Locale.getDefault()).format(new Date()) + "_" + "emosyn" + ".jpg";

        File newf=null;
        if (isExternalStoageReadable() || isExternalStorageWritable()) {

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/emosyn");
            Log.i(TAG, "Directory created at " + file.getAbsolutePath());
            if (!file.exists()) {
                success = file.mkdirs();

            }

            if (success) {
                newf = new File(file, filename);
                Log.i(TAG, " Pakki File created at " + newf.getAbsolutePath());
                filepath = newf.getAbsolutePath();
                OutputStream ou = null;
                try {

                    ou = new FileOutputStream(newf);
                    bitmap.compress(JPEG,100,ou);

                    ou.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (!newf.exists()) {
                    Log.e(TAG, "File didnt got saved");

                } else

                    addToGallery(context, filepath);
                Toast.makeText(context, "File Added to gallery", Toast.LENGTH_SHORT).show();
            }

        } else
            Log.e(TAG, "write error --check external media mounted or not");

        return filepath;
    }


    /**
     * this method fires an intent to add file to gallery
     *
     * @param context   application context
     * @param imagePath path to the image file
     */
    private static void addToGallery(Context context, String imagePath) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri n = Uri.fromFile(new File(imagePath));
        mediaScanIntent.setData(n);

        context.sendBroadcast(mediaScanIntent);


    }

    /**
     * Helper methods to check mounted storage state
     */

    private static boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }


    private static boolean isExternalStoageReadable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            return true;
        }

        return false;
    }



 //   class MainAsync extends AsyncTask<Void,Void,Void>{



    //    @Override
  //      protected Void doInBackground(Void... voids) {


        //    return null;
   //     }
  //  }

}
