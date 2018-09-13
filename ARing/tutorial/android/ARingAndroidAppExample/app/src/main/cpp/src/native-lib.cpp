#include <jni.h>
#include <android/log.h>
#include <DPFactoryForAndroid.h>
#include <IARing.h>
#include <DPException.h>


#define LOGF(tag, ...) __android_log_print(ANDROID_LOG_FATAL, tag, __VA_ARGS__)

#define TIMER_BEGIN { double __time__ = cv::getTickCount();
#define TIMER_NOW   ((double(cv::getTickCount()) - __time__) / cv::getTickFrequency())
#define TIMER_END   }


extern "C" {

using namespace dp::aringnative
std::shared_ptr<IARing> g_ptrARing;

void drawEar(cv::Mat dst, cv::Mat earingPic, DPARingResult result, bool isLeft) {
    float fEaring_scale_y = isLeft ? result.earringScaleYLeft : result.earringScaleYRight;
    cv::Mat matEarring;
    cv::resize(earingPic, matEarring, cv::Size(0, 0), fEaring_scale_y, fEaring_scale_y);

    if (matEarring.empty())
        return;

    cv::Point earingPt = isLeft ? result.leftEarringPos : result.rightEarringPos;

    cv::Rect earingRect(0, 0, matEarring.cols, matEarring.rows);
    earingRect = earingRect + earingPt;
    earingRect = earingRect - cv::Point(earingRect.width / 2, earingRect.width / 4);


    cv::Rect safeRect(0, 0, dst.cols, dst.rows);
    earingRect = earingRect & safeRect;

    if (matEarring.size().area() != earingRect.area())
        return;


    if (isLeft) {
        if (!(result.rotX < -15))
            matEarring.setTo(0, result.mask(earingRect));
    } else {
        if (!(result.rotX > 15))
            matEarring.setTo(0, result.mask(earingRect));
    }

    cv::Mat earingMask;
    cv::compare(matEarring, 0, earingMask, cv::CMP_GT);

    cv::cvtColor(earingMask, earingMask, CV_BGR2RGBA); //모바일에서는 RGBA로 변환해야 함
    cv::cvtColor(matEarring, matEarring, CV_BGR2RGBA);

    cv::subtract(dst(earingRect), earingMask, dst(earingRect));
    cv::add(matEarring, dst(earingRect), dst(earingRect));
}

JNIEXPORT void JNICALL
Java_maka_deepixel_xyz_makaandroidappexample_MainActivity_init(JNIEnv *env, jobject,
                                                               jobject activity,
                                                               jstring licenseFilename) {
    try {
        jboolean isCopy;
        std::string licenseFilenameStr = env->GetStringUTFChars(licenseFilename, &isCopy);
        g_ptrARing = dp::android::DPFactoryForAndroid::CreateInstance<IARing>
                (env, activity);
    } catch (dp::exception::DPLicenseExpiredException ex) {
        LOGF("ARing", "%s", ex.what());
    }
}

JNIEXPORT void JNICALL
Java_maka_deepixel_xyz_makaandroidappexample_MainActivity_process(JNIEnv *env, jobject,
                                                                  jstring videoAbsolutePath) {
    // For testing
    // You have to insert "test_video.avi" file into /Android/data/'your application id'/files directory.
    const char *pVideoAbsolutePath = env->GetStringUTFChars(videoAbsolutePath,
                                                            0);//"/storage/emulated/0/Download/test_video.avi";//env->GetStringUTFChars(videoAbsolutePath, 0);

    cv::VideoCapture videoCapture;
    if (!videoCapture.open(pVideoAbsolutePath)) {
        LOGF("ARing", "Cannot open file(%s).", pVideoAbsolutePath);
        return;
    }

    double width = videoCapture.get(cv::CAP_PROP_FRAME_WIDTH);
    double height = videoCapture.get(cv::CAP_PROP_FRAME_HEIGHT);
    double fps = videoCapture.get(cv::CAP_PROP_FPS);

    g_ptrARing->initialize((int) width, (int) height, 0, 0, 0); // ARing 초기화

    std::string resultVideo = pVideoAbsolutePath;
    resultVideo = resultVideo.substr(0, resultVideo.length() - 4) + "_result.avi";
    cv::VideoWriter videoWriter;
    if (!videoWriter.open(resultVideo, cv::VideoWriter::fourcc('M', 'J', 'P', 'G'), fps,
                          cv::Size(width, height))) {
        LOGF("ARing", "Cannot save file(%s).", resultVideo.c_str());
        return;
    }
    resultVideo = resultVideo.substr(0, resultVideo.length() - 11) + "_log.txt";

    cv::Mat earingPic = cv::imread("/storage/emulated/0/Download/bvlgari_earing.png"); //귀걸이 사진 입력

    int frameNum = 0;
    while (true) {
        cv::Mat matProcessing;
        if (!videoCapture.read(matProcessing))
            break;
        if (matProcessing.empty())
            break;

        DPARingResult result = g_ptrARing->DetectFace(matProcessing); //ARing API 실행

        if (result.detected) {
            drawEar(matProcessing, earingPic, result, true); // left earing 그리기
            drawEar(matProcessing, earingPic, result, false); // right earing 그리기
        }

        videoWriter.write(matProcessing);
        frameNum++;
    }
}


}
