package in.asynchronous;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import in.emoji.emosyn.turner;


/**
 * Created by navdeep on 22/9/17.
 */

public class Asynctascker extends AsyncTask<Bitmap, Void,Void> {
    @Override
    protected Void doInBackground(Bitmap... bmp) {

        turner.canvas.drawBitmap(bmp[0],0,0,null);
        turner.canvas.drawBitmap(bmp[1],turner.emojiPositionX,turner.emojipositionY,null);
return null;
    }

//
//    @Override
//    protected void onPostExecute(Bitmap o) {
//
//    }
}
