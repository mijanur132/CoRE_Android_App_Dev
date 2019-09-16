#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <vector>
#include <ostream>
#include <string_view>
#include <locale>
#include <android/log.h>
#include"ERI.h"
#include "image.h"
#include "path.h"
#include <vector>

using namespace std;
using namespace cv;

typedef unsigned char byte;
vector <Mat> vec;



extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_image(JNIEnv *env, jobject instance, jlong addr) {

    Mat* pMatGr=(Mat*)addr;
   // Mat convImage= imread("http://127.0.0.5:80/1.JPG");
    Mat convImage= imread("http://10.0.2.2:80/1.jpg");
    int a=convImage.empty();
    __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "Image loaded??....................................%d>> ", a);
    //testrotationxyframe(convImage);
    if (convImage.empty()) {
        *pMatGr = Mat::zeros(200, 400, CV_8UC3);
    }
    else{
        *pMatGr=convImage;
    }
}
}

extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_salt(JNIEnv *env, jobject instance, jlong addr) {

    Mat* pMatGr=(Mat*)addr;

     VideoCapture cap1("/storage/emulated/0/Download/roller_2000_1000.mp4");
    //VideoCapture cap1("http://10.0.2.2:80/video.mp4");
    if (!cap1.isOpened()) {
        exit(EXIT_FAILURE);
    }
    for (int fi = 0; fi < 250; fi++)
    {
        Mat frame;
        cap1 >> frame;
        if (frame.empty()) {
            frame = Mat::zeros(100+fi, 400+fi, CV_8UC3);
        } else {
        }
        __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "fi...............arr......................%d>> ", fi);
        vec.push_back(frame);
        frame.release();
    }
   }
}


extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_saltStill(JNIEnv *env, jobject instance, jlong addr, jint fi) {

    Mat* pMatGr=(Mat*)addr;
   // testrotationxyframe(convImage);
    __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "fi...............arr................>>>>>>>>>>>>..............%d>> ", fi);
    *pMatGr=vec[fi];

    return;
 }
}







