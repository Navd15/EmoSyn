package in.emoji.emosyn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity {
    private FloatingActionButton click, delete;
    private static Boolean isAlreadySaved = false;
    private ImageButton save, share;
    private final int share_code = 1;
    private ImageView img;
    private static final int req_co = 124;
    private final int image_code = 12;
    private static final String TAG = "in.emoji.emosyn.Main";
    private static final String File_Provider_Authority = "in.emoji.fileprovider";
    private static String tempPath = " ";
    private Bitmap bmp;
    private LinearLayout bottomBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        delete = (FloatingActionButton) findViewById(R.id.delete);

        delete.setVisibility(View.GONE);

        click = (FloatingActionButton) findViewById(R.id.click);

        save = (ImageButton) findViewById(R.id.save);

        share = (ImageButton) findViewById(R.id.share);

        bottomBanner = (LinearLayout) findViewById(R.id.bottomBanner);

        img = (ImageView) findViewById(R.id.img);


    }



    public void click(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, req_co);


        } else
            capture();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case req_co:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    capture();
                } else
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    public void capture() {

        Intent launch_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (launch_cam.resolveActivity(getPackageManager()) != null) {
            //create a temp file
            File tempimg;

            tempimg = Imagers.createTemp(getApplicationContext());
            //
            if (tempimg != null) {
                tempPath = tempimg.getAbsolutePath();
                Uri temp = FileProvider.getUriForFile(this, File_Provider_Authority, tempimg);
                launch_cam.putExtra(MediaStore.EXTRA_OUTPUT, temp);
                startActivityForResult(launch_cam, image_code);

            }

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case share_code:
                // if(resultCode==RESULT_OK){
                //  Imagers.deleteFile(this,tempPath);

                //  }

            case image_code:
                if (resultCode == RESULT_OK) {
                    setupData();

                } else {
                    Imagers.deleteFile(this, tempPath);
                }

        }

    }
// Triggers after user selects the image from camera

    private void setupData() {

        try {
            //
            turner turn=new turner(this,Imagers.iCompress(getApplicationContext(), tempPath));

            if(turn.finder()!=null) {
                bmp = turn.finder();
                delete.setVisibility(View.VISIBLE);
                click.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);
                bottomBanner.setVisibility(View.VISIBLE);
                img.setImageBitmap(bmp);

            }
            else{
               //do nothing

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }

    public void deleteIt(View v) {
        img.setImageBitmap(null);
        img.setVisibility(View.GONE);
        click.setVisibility(View.VISIBLE);
        bottomBanner.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        Imagers.deleteFile(this, tempPath);
    }


    public void saveIt(View view) {
        Imagers.saveImage(this, bmp);

        Imagers.deleteFile(this, tempPath);

    }

    public void share(View v) {
        File n = new File(Imagers.saveImage(this, bmp));
        Uri urs = FileProvider.getUriForFile(this, File_Provider_Authority, n);
        Intent sh = new Intent(Intent.ACTION_SEND);
        sh.setType("image/*");
        sh.putExtra(Intent.EXTRA_STREAM, urs);
        startActivityForResult(sh, share_code);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  clearCacheDir();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCacheDir();
    }


    //Helper method to clear cache dir

    private static void clearCacheDir() {

        File n = new File("/storage/emulated/0/Android/data/in.emoji.emosyn/cache/");
        if (n.exists() && n.listFiles().length > 0) {
            File[] d = n.listFiles();
            for (int i = 0; i < d.length; i++) {
                d[i].delete();
            }

        }


    }



}


