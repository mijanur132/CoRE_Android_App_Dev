
#include"path.h"
#include"image.h"
#include<stdio.h>
#include<math.h>
#include <stdlib.h>
#include <android/log.h>
#include "ERI.h"
#include "../../../../../OpenCV-android-sdk/sdk/native/jni/include/opencv2/core/mat.hpp"


#include <C:\opencv\build\include\opencv2\videoio.hpp>
#include <C:\opencv\build\include\opencv2\opencv.hpp>
#include <C:\opencv\build\include\opencv2\core\core.hpp>
#include <C:\opencv\build\include\opencv2\highgui\highgui.hpp>

using namespace cv;




void Path::RotateXYaxisERI2RERI(Mat & originERI, Mat& newERI, V3 directionbefore, V3 directionaftertilt, M33 reriCS)
{
    __android_log_write(ANDROID_LOG_ERROR, "from path....","came to Rotate function..");

	ERI oERI(originERI.cols, originERI.rows);
	V3 p = directionbefore;
	V3 p1 = directionaftertilt;
	V3 a = p.UnitVector();
	V3 b = p1.UnitVector();
	float m = p * p1;
	V3 dir = (p1^p).UnitVector();
	float angle = ((float)180 / (float)PI)* acos(m);
	angle = angle;
	if (angle == 0)
	{
		newERI = originERI;
		return;
	}
	//cout << a << " " << b << " m: " << m<<" angle: " <<angle << endl;

	//system("pause");

	for (int j = 0; j < newERI.rows; j++)
	{
		for (int i = 0; i < newERI.cols; i++)
		{
			V3 q = oERI.Unproject(j, i);
			//cout << q << endl;
			//cout << reriCS << endl;
			q = reriCS.Inverted() * q;
			//cout << q << endl;
			//q = q.RotateThisVectorAboutDirection(dir, angle);
			int u = oERI.Lon2PixJ(oERI.GetXYZ2Longitude(q));
			int v = oERI.Lat2PixI(oERI.GetXYZ2Latitude(q));
			newERI.at<Vec3b>(j, i) = originERI.at<Vec3b>(v, u);

		}
	}

	originERI=newERI;

}
