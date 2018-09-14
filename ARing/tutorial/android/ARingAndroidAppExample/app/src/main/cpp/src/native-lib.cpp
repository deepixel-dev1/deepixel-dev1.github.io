#include <jni.h>
#include <android/log.h>
#include <DPFactoryForAndroid.h>
#include <IARing.h>
#include <DPException.h>

#define TAG "DPARingProcessNative"
#define LOGF(tag, ...) __android_log_print(ANDROID_LOG_FATAL, tag, __VA_ARGS__)

#define TIMER_BEGIN { double __time__ = cv::getTickCount();
#define TIMER_NOW   ((double(cv::getTickCount()) - __time__) / cv::getTickFrequency())
#define TIMER_END   }


extern "C" {

using namespace dp::aringnative;
std::shared_ptr<IARing> g_ptrARing;

void drawEar(cv::Mat &dst, cv::Mat matEarPic, cv::Mat mask, bool isLeft, cv::Point earringPos,
             float rotX, float earringScaleY) {
    if (earringPos.x <= 0 || earringPos.y <= 0
        || earringPos.x >= dst.cols || earringPos.y >= dst.rows)
        return;

    cv::Mat matEarring;
    float earringScale = (float)dst.rows * earringScaleY * 0.6f / (float)matEarPic.rows;
    cv::resize(matEarPic, matEarring, cv::Size(0, 0), earringScale, earringScale);

    cv::Rect earingRect(0, 0, matEarring.cols, matEarring.rows);
    earingRect = earingRect + earringPos;
    earingRect = earingRect - cv::Point(earingRect.width / 2, earingRect.width / 4);

    cv::Rect safeRect(0, 0, dst.cols, dst.rows);
    earingRect = earingRect & safeRect;

    if (matEarring.size().area() != earingRect.area())
        return;

    // To apply mask only when its not visible
    if (isLeft && rotX >= -15) {
        matEarring.setTo(0, mask(earingRect));
    } else if (!isLeft && rotX <= 15) {
        matEarring.setTo(0, mask(earingRect));
    }

    cv::Mat earingMask;
    cv::compare(matEarring, 0, earingMask, cv::CMP_GT);

    cv::subtract(dst(earingRect), earingMask, dst(earingRect));
    cv::add(matEarring, dst(earingRect), dst(earingRect));
}

JNIEXPORT void JNICALL
Java_maka_deepixel_xyz_makaandroidappexample_MainActivity_init(JNIEnv *env, jobject,
                                                               jobject activity) {
    try {
        g_ptrARing = dp::android::DPFactoryForAndroid::CreateInstance<IARing>
                (env, activity);
    } catch (const dp::exception::DPLicenseExpiredException &ex) {
        LOGF(TAG, "1%s", ex.what());
    } catch (const dp::exception::DPLicenseException &ex) {
        LOGF(TAG, "12%s", ex.what());
    } catch (const std::exception &ex) {
        LOGF(TAG, "123%s", ex.what());
    }
}

JNIEXPORT void JNICALL
Java_maka_deepixel_xyz_makaandroidappexample_MainActivity_process(JNIEnv *env, jobject,
                                                                  jstring videoAbsolutePath, jstring earringAbolutePath) {
    // For testing
    // You have to insert "test_video.avi" file into /Android/data/'your application id'/files directory.
    const char *pVideoAbsolutePath = env->GetStringUTFChars(videoAbsolutePath,
                                                            0);//"/storage/emulated/0/Download/test_video.avi";//env->GetStringUTFChars(videoAbsolutePath, 0);

    const char *pEarringAbsolutePath = env->GetStringUTFChars(earringAbolutePath,
                                                            0);
    cv::VideoCapture videoCapture;
    if (!videoCapture.open(pVideoAbsolutePath)) {
        LOGF("ARing", "Cannot open file(%s).", pVideoAbsolutePath);
        return;
    }

    cv::Mat earingPic = cv::imread(pEarringAbsolutePath);
    if(earingPic.empty()){
        LOGF("ARing", "Cannot open file(%s).", pEarringAbsolutePath);
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

     //귀걸이 사진 입력

    int frameNum = 0;
    while (true) {
        cv::Mat matProcessing;
        if (!videoCapture.read(matProcessing))
            break;
        if (matProcessing.empty())
            break;

        DPARingResult result = g_ptrARing->DetectFace(matProcessing); //ARing API 실행

        if (result.detected) {
            drawEar(matProcessing, earingPic, result.mask, true, result.leftEarringPos, result.rotX, result.earringScaleYLeft); // left earing 그리기
            drawEar(matProcessing, earingPic, result.mask, false, result.rightEarringPos, result.rotX, result.earringScaleYRight); // right earing 그리기
        }

        videoWriter.write(matProcessing);
        frameNum++;
    }
}


}
