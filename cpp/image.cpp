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






void testrotationxyframe( Mat& frame)
{

	float hfov = 110.0f;
	PPC camera(hfov, 400, 200);

	Path path1;
	
	Mat newERI = Mat::zeros(frame.rows, frame.cols, frame.type());

	V3 p = camera.GetVD();
	camera.Pan(180.0f);
	camera.Tilt(30.0f);
	V3 p1 = camera.GetVD();

	// build local coordinate system of RERI
	V3 xaxis = camera.a.UnitVector();
	V3 yaxis = camera.b.UnitVector()*-1.0f;
	V3 zaxis = xaxis ^ yaxis;
	M33 reriCS;
	reriCS[0] = xaxis;
	reriCS[1] = yaxis;
	reriCS[2] = zaxis;

    __android_log_write(ANDROID_LOG_ERROR, "from image...","came to Rotate function..");

	path1.RotateXYaxisERI2RERI(frame, newERI, p, p1, reriCS);	
	

	

}
