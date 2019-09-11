#pragma once
#include "ppc.h"
#include"ERI.h"
#include "m33.h"
#include "../../../../../OpenCV-android-sdk/sdk/native/jni/include/opencv2/core/mat.hpp"

//using namespace cv;


class Path 
{

public:

	void RotateXYaxisERI2RERI(cv::Mat & origninalERI, cv::Mat & newERI, V3 pb, V3 pa, M33 reriCS);//direction based

};
