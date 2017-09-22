package in.asynchronous;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import in.emoji.emosyn.turner;


/**
 * Created by navdeep on 22/9/17.
 */

public class Asynctascker extends AsyncTask<Bitmap,Void,Bitmap> {
    @Override
    protected  Bitmap doInBackground(Bitmap...bmp) {


        return Bitmap.createBitmap(bmp[0]);
    }

//
//    @Override
//    protected void onPostExecute(Bitmap o) {
//
//    }
}
