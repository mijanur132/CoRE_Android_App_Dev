#include"image.h"
#include "../../../../../OpenCV-android-sdk/sdk/native/jni/include/opencv2/core.hpp"
#include<fstream>
#include<string.h>
#include <math.h>
#include <android/log.h>
#include "path.h"
#include <C:\opencv\build\include\opencv2\opencv.hpp>
#include <C:\opencv\build\include\opencv2\core\core.hpp>
#include <C:\opencv\build\include\opencv2\highgui\highgui.hpp>

using namespace cv;
using namespace std;


string filename;
int Is_MInv_calculated;
M33 M_Inv;
Mat convPixels( 512*0.8,960*0.8, CV_8UC3);

void CoRE_operation_per_frame(Mat & frame)
{
	Path path1;
	Mat ret1;
	vector<float> tstamps;
	int frameLen = 3840;
	int frameWidth = 2048;
	float hfov = 90.0f;
	float corePredictionMargin = 0.8;
	int compressionFactor = 5;
	int w = frameLen * hfov / 360;
	int h = frameWidth * hfov / 360;
	PPC camera2(hfov*corePredictionMargin, w*corePredictionMargin, h*corePredictionMargin);
	camera2.Pan(30);
	PPC refCam(hfov*corePredictionMargin, w*corePredictionMargin, h*corePredictionMargin);
	path1.CRERI2convOptimized(frame,  convPixels, camera2);
	frame=convPixels.clone(); //xxOpt: convpixels declare once at ERI

}

