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

using namespace std;
using namespace cv;

extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_salt(JNIEnv *env, jobject instance, jlong addr) {

    Mat* pMatGr=(Mat*)addr;

   Mat convImage= imread("/storage/emulated/0/Download/1.jpg");
   testrotationxyframe(convImage);
    if (convImage.empty()) {
        *pMatGr = Mat::zeros(100, 400, CV_8UC3);
    }
    else{
        *pMatGr=convImage;
    }
   //pMatGr=&convImage;
   //memcpy(pMatGr->data, data, pMatGr->step * pMatGr->rows);

}
}

extern "C"
{
JNIEXPORT void JNICALL Java_ch_hepia_iti_opencvnativeandroidstudio_MainActivity_video(JNIEnv *env, jobject instance, jlong addr) {

    Mat frame;
    Mat* pMatGr=(Mat*)addr;
    VideoCapture cap1("http://10.0.2.2:80/output.avi");
    if (!cap1.isOpened()) {  //!!!!!! ALWAYS CLOSED !!!!!
        __android_log_write(ANDROID_LOG_ERROR, "From_Native",
                            "capture isn't open. closing..");
        exit( EXIT_FAILURE);
    }

    for (int fi = 0; fi < 2; fi++)
    {
        cap1 >> frame;
        __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "cap1 %d", frame.empty());
    }
    if (frame.empty())
    {
        __android_log_print(ANDROID_LOG_VERBOSE, "MyApp", "empty frame");
        *pMatGr = Mat::zeros(100, 400, CV_8UC3);
    }
    else{
        *pMatGr=frame;
       }
}

}

