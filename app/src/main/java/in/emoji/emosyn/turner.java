package in.emoji.emosyn;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by nav_161 on 7/23/2017.
 */


/**
 *
 *
 * */
public class turner {
    private static final String TAG = "in.emoji.emosyn.turner";
    private static double Threshold_EyesOpen = 0.51;
    private static double Threshold_smiling = 0.56;
    private static double scale_fac = 0.9f;


    /**
     * @param context
     * @param bmp
     */

    static Bitmap finder(Context context, Bitmap bmp) {

        FaceDetector fd = new FaceDetector.Builder(context).setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame fm = new Frame.Builder().setBitmap(bmp).build();


        SparseArray<Face> Sp = fd.detect(fm);

        int faceses = Sp.size();
        Bitmap final_ = bmp;

        if (faceses > 0) {

            boolean smiling = false;
            boolean left_eye_open = false;
            boolean right_eye_open = false;

            Bitmap selected_emo;
            Log.i(TAG, "Faces detected " + faceses);

            for (int i = 0; i < faceses; ++i) {

                right_eye_open = Sp.valueAt(i).getIsRightEyeOpenProbability() > Threshold_EyesOpen;

                left_eye_open = Sp.valueAt(i).getIsLeftEyeOpenProbability() > Threshold_EyesOpen;

                smiling = Sp.get(i).getIsSmilingProbability() > Threshold_smiling;

                //selectEmoji method returns the drawable id for the required emoji

                selected_emo = BitmapFactory.decodeResource(context.getResources(), selectEmoji(whicEmoji(smiling, left_eye_open, right_eye_open)));

                final_ = adder(final_, selected_emo, Sp.get(i));


            }


        } else {
            Toast.makeText(context, "No faces detected in the above image", Toast.LENGTH_SHORT).show();
        }

        fd.release();

        return final_;

    }

    //Helper enum

    private enum Emoji {
        SMILING,

        SMILING_RIGHT_EYE_OPEN,

        SMILING_LEFT_EYE_OPEN,

        SMILING_BOTH_EYES_CLOSED,

        FROWNING,

        FROWNING_RIGHT_EYE_OPEN,

        FROWNING_LEFT_EYE_OPEN,

        FROWNING_BOTH_EYES_CLOSED,
    }


    /**
     * @param f
    public static void prob_builder(Face f) {

    Log.i("LeftEyeOpenProbability", Float.toString(f.getIsLeftEyeOpenProbability()));
    Log.i("RightEyeOpenProbability", Float.toString(f.getIsRightEyeOpenProbability()));
    Log.i("IsSmilingProbability", Float.toString(f.getIsSmilingProbability()));

    }*/

    /**
     * Helper method returns the enum object on basis of probabilites
     *
     * @param s
     * @param l
     * @param r
     * @return enum
     */
    private static Emoji whicEmoji(Boolean s, Boolean l, Boolean r) {
        if (s) {
            if (l && r) {
                return Emoji.SMILING;

            }
            if (!l && r) {
                return Emoji.SMILING_RIGHT_EYE_OPEN;

            }
            if (l && !r) {
                return Emoji.SMILING_LEFT_EYE_OPEN;
            }


            if (!l && !r) {

                return Emoji.SMILING_BOTH_EYES_CLOSED;

            }


        } else
            if (l && r) {
            return Emoji.FROWNING;

        }
        if (!l && r) {
            return Emoji.FROWNING_RIGHT_EYE_OPEN;

        }

        if (l && !r) {

            return Emoji.FROWNING_LEFT_EYE_OPEN;
        }
        if (!l && !r) {
            return Emoji.FROWNING_BOTH_EYES_CLOSED;
        }

        return null;

    }


    /**
     * Helper method returns the 'id' of the drawable according to the emoji selected from 'whichEmoji' method
     * @param e
     * @return drawable id
     */
    private static int selectEmoji(Emoji e) {
        switch (e) {

            case SMILING:

                return R.drawable.smiling;

            case SMILING_RIGHT_EYE_OPEN:

                return R.drawable.rightwink;

            case SMILING_LEFT_EYE_OPEN:

                return R.drawable.leftwink;

            case SMILING_BOTH_EYES_CLOSED:

                return R.drawable.closed_smile;

            case FROWNING:

                return R.drawable.frown;

            case FROWNING_RIGHT_EYE_OPEN:

                return R.drawable.rightwinkfrown;

            case FROWNING_LEFT_EYE_OPEN:

                return R.drawable.leftwinkfrown;

            case FROWNING_BOTH_EYES_CLOSED:

                return R.drawable.closed_frown;

            default:

                return 0;
        }


    }

    /**
     * Adds the overlay to the selected bitmap
     *
     * @param bg_image
     * @param emoji_bg
     * @param face_slected
     * @return final bitmap
     */
    private static Bitmap adder(Bitmap bg_image, Bitmap emoji_bg, Face face_slected) {

        // Cloning the bitmap sent  by the  user
        Bitmap overlyed = Bitmap.createBitmap(bg_image.getWidth(), bg_image.getHeight(), bg_image.getConfig());

        //Scale the size of emoji to be drawn on
        int emo_height = (int) (face_slected.getHeight() * scale_fac);
        int emo_width = (int) (face_slected.getWidth() * scale_fac);

       //create a emoji with new dimens
        emoji_bg = Bitmap.createScaledBitmap(emoji_bg, emo_width, emo_height, false);

        // position to draw the emoji
        float emojiPositionX =
                (face_slected.getPosition().x + face_slected.getWidth() / 2) - emoji_bg.getWidth() / 2;

        float emojiPositionY =
                (face_slected.getPosition().y + face_slected.getHeight() / 2) - emoji_bg.getHeight() / 3;

        //new Canvas object to draw face on
        Canvas canu = new Canvas(overlyed);

        //we have to draw it again as Bitmap is imutable
        canu.drawBitmap(bg_image, 0, 0, null);

        // selcted emoji bg drawn on given positions
        canu.drawBitmap(emoji_bg, emojiPositionX, emojiPositionY, null);


        // returning the overlayed image
        return overlyed;

    }


}

