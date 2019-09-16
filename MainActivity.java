package ch.hepia.iti.opencvnativeandroidstudio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;



import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Boolean.TRUE;
import static org.opencv.core.CvType.CV_8UC4;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_COMPLEX_SMALL;
import static org.opencv.imgproc.Imgproc.putText;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "OCVSample::Activity";
    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle
                    // after opencv initialization
                    System.loadLibrary("native-lib");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, _baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private class MyTask extends AsyncTask<Long, Void, Void> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
           System.out.println("Pre execute...........................>>><<<");
        }

        // This is run in a background thread
        @Override
        protected Void doInBackground(Long... param) {
            // get the string from params, which is an array
            Long xxx= param[0];
            System.out.println("back execute...........................>>><<<"+xxx);
            salt(xxx);
            // Do something that takes a long time, for example:
            System.out.println("back execute returned...........................>>><<<"+xxx);
         return null;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   video_thread();
                    //image();

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void video_thread()
    {
        Mat m=new Mat();
        new MyTask().execute(m.getNativeObjAddr());
        try {
            Thread.sleep(20000);
        } catch(InterruptedException e) {
            // Process exception
        }
        for (int fi=0; fi<1; fi++) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                int ia=10;
                public void run(){
                    ImageView iv = (ImageView) findViewById(R.id.imageView);
                    iv.invalidate();
                    Bitmap bm;
                    Mat m1=new Mat();
                    saltStill(m1.getNativeObjAddr(), ia);
                    if (ia%2==0) {
                        Imgproc.putText(m1, "hi there ;)", new Point(30, 80), FONT_HERSHEY_COMPLEX_SMALL, 2.2, new Scalar(200, 200, 200), 2);
                    }
                    bm = Bitmap.createBitmap(m1.cols(), m1.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(m1, bm);
                    System.out.println(".............>:"+ ia);
                    iv.setImageBitmap(bm);
                    ia++;
                    handler.postDelayed(this, 10);
                }
            }, 10);

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                // Process exception
            }
        }
    }

    public void image() {

        Mat m;
        m = new Mat();

        image(m.getNativeObjAddr());
        // putText(m, "hi there ;)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new Scalar(200,200,0),2);

        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, bm);

        // find the imageview and draw it!
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bm);

    }

    public void displaymat(Mat m) {
        Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, bm);

        // find the imageview and draw it!
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bm);

    }
    public native void image(long addr);
    public native void salt(long addr);
    public native void saltStill(long addr, int fi);


}
