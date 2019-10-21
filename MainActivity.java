package ch.hepia.iti.opencvnativeandroidstudio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Boolean.TRUE;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.StrictMath.abs;
import static org.opencv.core.CvType.CV_8UC4;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_COMPLEX_SMALL;
import static org.opencv.imgproc.Imgproc.putText;




public class MainActivity extends AppCompatActivity {
    int chunk2display=1;
    int dlFinished=0;
    Long chunk2loadFile=1L;

    private static final String TAG = "OCVSample::Activity";
    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle               // after opencv initialization
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
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
            Long addr= param[0];
            long x=param[1];
            int chunk2load=(int)x;
            System.out.println("backGround execute chunk...........................>>><<<"+ chunk2load);
            Long tsLong = System.currentTimeMillis();
            String ts1 = tsLong.toString();
            String videoPath=downloadFileHttp(chunk2load);
            Long tsLong2 = System.currentTimeMillis();
            String ts2 = tsLong2.toString();
            System.out.println("video path..................................."+videoPath);
            dlFinished=loadVideoFromDevice(addr, videoPath, chunk2load);
            Long tsLong3 = System.currentTimeMillis();
            String ts3 = tsLong3.toString();
            System.out.println("time to download................................>>>"+" "+ts1+" "+ts2+" "+ts3);
            System.out.println("backGround execute returned...........................>>><<<"+ chunk2load);
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
                   initCoREparameters();
                    System.out.println("CoRE param updated..........................................>>>>");
                    dlThread();
                    playThread();

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

    public void dlThread()
    {
        Mat m=new Mat();
        new MyTask().execute(m.getNativeObjAddr(),chunk2loadFile);//calling load video from device using videocapture in background
     }

    public void playThread()
    {
        while(dlFinished==0)
        {
            //System.out.println("Not downloaded yet");
        }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable(){

                int ia=0    ;
                Mat m1=new Mat();
                public void run(){
                    ImageView iv = (ImageView) findViewById(R.id.imageView);
                    iv.invalidate();
                    Bitmap bm;
                    System.out.println("displaying chunk ..........."+ chunk2display);
                    CoREoperationPerFrame(m1.getNativeObjAddr(), ia, chunk2display); // ia increaseas and one after another frame comses out
                    bm = Bitmap.createBitmap(m1.cols(), m1.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(m1, bm);
                    System.out.println("frame displayed from current chunk.............>:"+ ia);
                    iv.setImageBitmap(bm);

                    if (ia<119) {
                        handler.postDelayed(this, 2);
                    }
                    if (ia==119)
                    {
                       chunk2display=chunk2display+1;
                        ia=0;
                        System.out.println("chunk 2 display updated............"+ chunk2display+" ia "+ ia);
                        //System.exit((1));
                        playThread();
                    }
                    if (ia==90)
                    {
                        Mat m=new Mat();
                        chunk2loadFile=chunk2loadFile+1;
                        dlFinished=0;
                        new MyTask().execute(m.getNativeObjAddr(), chunk2loadFile);//calling load video from device using videocapture in background

                    }
                    ia++;
                }
            }, 2);

    }

    public String downloadFileHttp(int chunkN)
    {
        String fPath="";
        try
        {
            String sourceBaseAddr="http://128.10.120.226:80/4thSecVar/diving/";
            double cameraDirectionX=1;
            double cameraDirectionY=0.1;
            double cameraDirectionZ=1;
            String result="diving_"+getFileName2Req(sourceBaseAddr,chunkN, cameraDirectionX,cameraDirectionY, cameraDirectionZ);
            //URL url = new URL("http://128.10.120.226:80/video1.mp4");
            String name=sourceBaseAddr+result;
            URL url = new URL(name);
            String fname=result;
            System.out.println("requested file name................................>>>"+ name+ " "+fname);
            //System.out.println("path "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

            URLConnection ucon = url.openConnection();
            ucon.setReadTimeout(5000);
            ucon.setConnectTimeout(10000);
            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
            File file = new File("/storage/emulated/0/Download/" + fname);
           // File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fname);
             fPath=file.getPath();

            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            int len;
            while ((len = inStream.read(buff)) != -1)
            {
                outStream.write(buff, 0, len);
            }

            outStream.flush();
            outStream.close();
            inStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Cant save file.......>>>"+fPath);
            System.exit(1);
        }

        return fPath;
    }
//x,y and z needed to be unit vector
    public String getFileName2Req(String srcBaseAddr, int chunkN, double x, double y, double z)
    {
        double latt = 90.0f- asin(y)* 180.0f / PI;
        double absVec=x*x+y*y+z*z;
        x=x/absVec;
        y=y/absVec;
        z=z/absVec;

        double longg=0;
        if (x >= 0 && z >= 0)
        {    // use nested if
            longg = (atan(x / z))* 180.0f / 3.1416;
        }
        else if (x < 0 && z >= 0) {

            longg = 360.0f + (atan(x / z))* 180.0f / PI;

        }
        else if (x >= 0 && z < 0) {
            longg = (atan(x / z))* 180.0f / PI;
            longg = 180 + longg;
        }

        else if (x < 0 && z < 0) {
            longg = (atan(x / z))* 180.0f / PI;
            longg = 180.0f + longg;
        }

        else {
            //cout << "this not handled yet" << endl;
        }

        double pann = longg - 180;
        double tiltt = 90 - latt;
        int latAngle = 20;
        int temp = -90 - latAngle / 2;

        while ((int) tiltt >= temp) {
            temp = temp + latAngle;

        }
        int reqtilt = (2 * temp - latAngle) / 2;
        int panAngle = (int) abs((20) / cos(3.1416 * reqtilt / 180));   //argument in radian
        if (panAngle % 2 != 0) {
            panAngle += 1;
        }
        temp = -180 - panAngle / 2;
        while ((int) pann >= temp) {
            temp = temp + panAngle;
        }
        int reqpann = (2 * temp - panAngle) / 2;

       String result= chunkN + "_" + reqtilt + "_" + reqpann + ".mp4";

       return result;
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

    public native void image(long addr);
    public native void initCoREparameters();
    public native int loadVideoFromDevice(long addr, String videoPath, int chunkN);
    public native void CoREoperationPerFrame(long addr, int fi, int chunkN);
    }
