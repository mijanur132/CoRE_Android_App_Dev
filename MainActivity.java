package com.example.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.os.Environment.getExternalStorageState;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.StrictMath.abs;


public class MainActivity extends AppCompatActivity {
    volatile int chunk2display=1;
    volatile int totalChunk2display=1;
    volatile int yes2DL=0;
    volatile int yes2PL=0;
    volatile int dlFinished=0;
    volatile int totalDlChunk=0;
    volatile int totalPlChunk=0;
    long   chunk2loadFile=1L;
    volatile int pan=0;
    volatile  int dlChunkPan=0;
    volatile  int dlChunkPan1=0;
    volatile int lastChunkReqPan=0;
    volatile int lastChunkReqTilt=0;
    volatile int totalPan=0;
    volatile int totalTilt=0;
    volatile long startTotal=0;
    volatile float downX=0;
    volatile float downY=0;
    volatile int perVideoMx=30;
    volatile long lastTime=0;
    volatile String resultPart="30_roller.mkv0_";

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
        }

        // This is run in a background thread
        @Override
        protected Void doInBackground(Long... param) {
            Long addr= param[0];
            long x=param[1];
            long p=param[2];
           // int pan=(int)p;
            int chunk2load=(int)x;
            while(true)
            {
                if(yes2DL==1)
                {
                    String videoPath=downloadFileHttp(chunk2load, totalPan, totalTilt);
                    System.out.println("Path:"+videoPath);
                    int xx=pan;
                    dlFinished=loadVideoFromDevice(addr, videoPath, chunk2load);
                    System.out.println("dl finished.total DL:..................................................................."+chunk2load);
                    totalDlChunk=totalDlChunk+1;
                    yes2DL=0;
                    yes2PL=1;
                    chunk2load=chunk2load+1;
                   dlChunkPan1=dlChunkPan;
                }
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    helloworld();
                    load_image();
                    initCoREparameters();
                    System.out.println("CoRE param updated..........................................>>>>");
                    Toast.makeText(MainActivity.this, "Wait for video to start........", Toast.LENGTH_SHORT).show();
                    startTotal = System.currentTimeMillis();
                    yes2DL=1;
                    dlThread();
                    while(totalPlChunk>=totalDlChunk)//totalDlChunk<=totalPlChunk
                    {  }
                    playThread();


                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    public void dlThread()
    {

                Mat m = new Mat();
                long cameraPan = totalPan;
                new MyTask().execute(m.getNativeObjAddr(), chunk2loadFile, cameraPan);//calling load video from device using videocapture in background

    }

    public void playThread()
    {

        System.out.println("here..");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            int ia=0;
            Mat m1=new Mat();
            Long FirstStart = System.currentTimeMillis();

            int dlChunkPan1xx=0;
            int dlChunkTilt1xx=0;
            public void run()
                    {
                        int lastPan=0;

                        ImageView iv = (ImageView) findViewById(R.id.imageView);
                        iv.invalidate();
                        Bitmap bm;

                        iv.setOnTouchListener(new View.OnTouchListener(){
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                int add=5;
                                boolean mIsSwiping = false;
                                switch(event.getActionMasked()) {
                                    case MotionEvent.ACTION_DOWN: {
                                        downX = event.getX();
                                        downY=event.getY();
                                        break;
                                    }
                                    case MotionEvent.ACTION_UP:
                                        float deltaX = event.getX() - downX;
                                        float deltaY = event.getY() - downY;
                                        //Toast.makeText(MainActivity.this, "touch.................."+deltaX+" "+deltaY, Toast.LENGTH_SHORT).show();
                                        if (abs(deltaX) > 10)
                                        {
                                            int addX=(int)deltaX/40;
                                            totalPan=(totalPan+addX);
                                        }

                                        if (abs(deltaY) > 10)
                                        {
                                            int addY=(int)deltaY/100;
                                            totalTilt=(totalTilt+addY);
                                            if (totalTilt>80)
                                            {totalTilt=80;}
                                            if (totalTilt<-80)
                                            {totalTilt=-80;}
                                        }
                                        else {
                                        }
                                        return true;
                                }

                                return true;
                            }
                        });
                        Long current = System.currentTimeMillis();
                        long playTime=current-lastTime;
                        while(playTime<48)
                        {   current = System.currentTimeMillis();
                            playTime=current-lastTime;
                        }
                        lastTime=current;

                        long frameTime=current-FirstStart;



                        if(ia==0)
                            {
                                dlChunkPan1xx=lastChunkReqPan;
                                dlChunkTilt1xx=lastChunkReqTilt;
                            }
                        int cameraPan=totalPan-dlChunkPan1xx;
                        int cameraTilt=totalTilt-dlChunkTilt1xx;

                        CoREoperationPerFrame(m1.getNativeObjAddr(), ia, chunk2display, totalPan, totalTilt,dlChunkPan1xx, dlChunkTilt1xx ); // ia increaseas and one after another frame comses out
                        bm = Bitmap.createBitmap(m1.cols(), m1.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(m1, bm);

                        iv.setImageBitmap(bm);
                        handler.postDelayed(this, 1);
                        if (ia==119)
                        {
                            chunk2display=chunk2display+1;
                            totalChunk2display=totalChunk2display+1;
                            totalPlChunk=totalPlChunk+1;
                            while(yes2PL==0)
                            {
                            }
                            System.out.println("ended...............................................................................................................");
                            ia=-1;

                            if (totalChunk2display==perVideoMx)
                            {
                                Toast.makeText(MainActivity.this, "Thank you for Watching.", Toast.LENGTH_SHORT).show();
                                Long endTotal = System.currentTimeMillis();
                                Long totalPlay=endTotal-startTotal;
                                System.out.println("Total total Time:..................................................................:" +totalPlay);

                                String url = "https://forms.gle/sxgZQrdRyANFgYq59";
                                //String url = "https://forms.gle/cfUdaqSkifimhqbh6";
                                //String url = "https://forms.gle/dHwPywC1Zu5ZNr4e8";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                System.exit(0);
                            }

                        }

                        if (ia==60)
                        {
                            System.out.println("Chunk..............................................................................................................."+chunk2display);

//                            if (chunk2display==perVideoMx)
//                            {
//                                Toast.makeText(MainActivity.this, "Video Should Change.", Toast.LENGTH_SHORT).show();
//                                resultPart="30_rhino.webm0_";
//                            }
//                            if (chunk2display==perVideoMx*2)
//                            {
//                                Toast.makeText(MainActivity.this, "Thank you for Watching.", Toast.LENGTH_SHORT).show();
//                                resultPart="30_roller.mkv0_";
//                            }

                            yes2DL=1;
                            yes2PL=0;
                        }
                        ia++;



                    }
                }, 2);

    }


    public String downloadFileHttp(int chunkN, int pan, int tilt)
    {
        String fPath="";
        String name="";
        String fname="";
        try
        {
            String sourceBaseAddr="http://192.168.43.179:80/3vid2crf3trace/android/2min_30/";
           // String sourceBaseAddr="http://10.0.2.2:80/3vid2crf3trace/android/2min_30/";
            String result=resultPart+getFileName2Req(sourceBaseAddr,chunkN, totalPan, totalTilt);
            name=sourceBaseAddr+result;
            URL url = new URL(name);
            fname=result;
            System.out.println("Requested file name................................>>>"+ name);

            URLConnection ucon = url.openConnection();
            ucon.setReadTimeout(1000);
            ucon.setConnectTimeout(1000);
            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 500);
            File file = new File("/storage/emulated/0/Download/" + fname);
            fPath=file.getPath();
            System.out.println("File name................................>>>"+ fPath);
          // fPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+ fname;
            //fPath="/storage/emulated/0/Download/diving_1_10_40.mp4";

            if (!file.exists())
            {
                file.createNewFile();
                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[500 * 1024];

                int len;
                while ((len = inStream.read(buff)) != -1) {
                    outStream.write(buff, 0, len);
                }

                outStream.flush();
                outStream.close();
                inStream.close();
            }

            System.out.println("File saved.......>>>"+fPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String fileB = "/storage/emulated/0/Download/" + fname;
            System.out.println("Cant save file.......>>>"+fPath);
            return  fileB;
        }

        return fPath;
    }

    public String getFileName2Req(String srcBaseAddr, int chunkN, int pan, int tilt)
    {

        int panTemp=-200;
        int panAngle=20;
        while ((int) pan >= panTemp) {
            panTemp = panTemp + panAngle;
        }
        int reqpann = panTemp-panAngle;
        lastChunkReqPan=reqpann;
        reqpann=reqpann%360;

        if (reqpann>180)
        {
            reqpann=reqpann-360;
        }
        if (reqpann<-180)
        {
            reqpann=360+reqpann;
        }


        int tempTilt=-80;
        while(tilt>=tempTilt)
        {
            tempTilt=tempTilt+panAngle;
        }
        int reqTilt=tempTilt-panAngle;
        if (reqTilt>60)
        {
            reqTilt=60;
        }
        if (reqTilt<-60)
        {
            reqTilt=-60;
        }
        lastChunkReqTilt=reqTilt;
        int chunknn=chunkN%perVideoMx;
        if(chunknn==0)
        {
            chunknn=chunknn+1;
        }
        if ((chunknn==5 || chunknn ==7) && (reqTilt==0 && reqpann==0)){
            String result= chunknn + "_" + "-20" + "_" + "20" + ".avi";
            System.out.println(" Changed...................>>>");
            return result;
        }
        else{
            String result= chunknn + "_" + reqTilt + "_" + reqpann + ".avi";
            return result;
        }


    }

    public void load_image()
    {
        String path=Environment.getExternalStorageDirectory()+"/Download/11.jpg";
        String path1=Environment.getExternalStorageDirectory().getPath();

        File imgFile = new File(path);
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView imageView=(ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(myBitmap);
        }
    }

    public void helloworld() {
        // make a mat and draw something
        Mat m = Mat.zeros(100,400, CvType.CV_8UC3);

        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, bm);

        // find the imageview and draw it!
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bm);
    }

    public native void initCoREparameters();
    public native int loadVideoFromDevice(long addr, String videoPath, int chunkN);
    public native void CoREoperationPerFrame(long addr, int fi, int chunkN, int cameraPan, int cameratilt, int baseAnglePan, int baseAngleTilt);
}
