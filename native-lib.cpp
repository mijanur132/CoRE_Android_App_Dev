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
vector<vector <Mat>> loadedFrameVec;
int framloaded;



extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_image(JNIEnv *env, jobject instance, jlong addr) {

    Mat* pMatGr=(Mat*)addr;
    Mat convImage= imread("http://10.0.2.2:80/1.jpg");
    int a=convImage.empty();
    __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "Image loaded??....................................%d>> ", a);
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
JNIEXPORT jint JNICALL
Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_loadVideoFromDevice(JNIEnv *env,
                                                                             jobject instance,
                                                                             jlong addr,
                                                                             jstring videoPath,
                                                                             jint chunkN) {

    Mat *pMatGr = (Mat *) addr;
    jboolean iscopy;
    const char *vpath = (env)->GetStringUTFChars(videoPath, &iscopy);
    VideoCapture cap1(vpath);
    if (!cap1.isOpened()) {
        return 0;
    }
    for (int fi = 0; fi < 120; fi++) {
        Mat frame;
        cap1 >> frame;
        if (frame.empty()) {
            frame = Mat::zeros(100 + fi, 400 + fi, CV_8UC3);//dummy frame
        } else {
        }
        __android_log_print(ANDROID_LOG_VERBOSE, "MyApp",
                            "fi...............arr......................%d>> ", fi);
        int chunkno = (int) chunkN;
        loadedFrameVec[chunkno].push_back(frame);
        frame.release();
    }

    return 1;
}
}

extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_CoREoperationPerFrame(JNIEnv *env, jobject instance, jlong addr, jint fi, jint chunkN) {

    Mat* pMatGr=(Mat*)addr;
    *pMatGr=loadedFrameVec[chunkN][fi].clone();
    CoRE_operation_per_frame(*pMatGr); //xxxOpt: pass fi as an input parameter instead of image vec[fi], use pMatGr as output parameter
    return;
 }
}

extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_initCoREparameters(JNIEnv *env, jobject instance) {
    Mat mat=Mat::zeros(900, 800, CV_8UC3);//dummy frame;
    for (int i = 0; i <10 ; ++i) {
        vector<Mat> temp;
        temp.push_back(mat);
        loadedFrameVec.push_back(temp);
    }

    ERI eri(3840,2048);
    Path path1;
    path1.updateReriCs();
    eri.atanvalue();
    eri.xz2LonMap();
    eri.xz2LatMap();
    path1.nonUniformListInit();
    path1.mapx();
    return;
}
}









