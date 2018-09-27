#include <jni.h>
#include <android/log.h>
#include <DPFactoryForAndroid.h>
#include <IARing.h>
#include <DPException.h>
#include <android/native_window_jni.h>
#include <future>
#include <thread>

#define TAG "DPARingProcessNative"
#define LOGF(tag, ...) __android_log_print(ANDROID_LOG_FATAL, tag, __VA_ARGS__)

#define TIMER_BEGIN { double __time__ = cv::getTickCount();
#define TIMER_NOW   ((double(cv::getTickCount()) - __time__) / cv::getTickFrequency())
#define TIMER_END   }


extern "C" {

using namespace dp::aringnative;
std::shared_ptr<IARing> g_ptrARing;
jobject g_surface = nullptr;
JavaVM *g_pJvm = nullptr;

std::mutex g_processMutex;
std::mutex g_postprocessMutex;

int g_srcWidth = 0;
int g_srcHeight = 0;
float g_earringScale = 1.0f;
cv::Mat g_matEarring;
cv::Mat g_matRotatedEarring;
cv::Point g_earringAnchorPos;
std::shared_ptr<std::thread> g_processingThread;
std::shared_ptr<std::thread> g_postProcessingThread;
std::condition_variable g_condition;
std::condition_variable g_postCondition;
std::queue<std::tuple<cv::Mat, cv::Mat>> g_processQ;
std::queue<std::tuple<cv::Mat, dp::aringnative::DPARingResult>> g_postProcessQ;
std::promise<void> g_promiseProcess;
std::promise<void> g_promisePostprocess;

void processingThread();
void postProcessingThread();
void drawEar(cv::Mat &dst, cv::Mat matEarPic, cv::Mat mask, bool isLeft, cv::Point earringPos,
             float rotX, float earringScaleY);
void setPostprocessThreadInput(cv::Mat &dst, dp::aringnative::DPARingResult &result);

void init() {
    if (g_processingThread.get() == nullptr) {
        g_processingThread.reset(new std::thread(processingThread));
    }
    if (g_postProcessingThread.get() == nullptr) {
        g_postProcessingThread.reset(
                new std::thread(postProcessingThread));
    }
}

void initJVM(JNIEnv *env) {
    if (g_pJvm == nullptr) {
        jint rs = env->GetJavaVM(&g_pJvm);
        assert (rs == JNI_OK);
        LOGF(TAG, "saved java VM.");
    }
}

void preProcessing(jbyte *pSrcBuffer, cv::Mat &src, cv::Mat &dst) {
    const cv::Mat srcYuv(g_srcHeight + g_srcHeight / 2, g_srcWidth, CV_8UC1, pSrcBuffer);
    src = srcYuv.clone();

    // convert YUV -> BGR
    cv::cvtColor(src, dst, CV_YUV2RGBA_NV21);
}

dp::aringnative::DPARingResult processing(cv::Mat src, cv::Mat &dst) {
    try {
        // Get env pointer.
        assert(g_pJvm != nullptr);
        JNIEnv *env;
        jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
        assert (rs == JNI_OK);

        dp::aringnative::DPARingResult result = g_ptrARing->DetectFace(src,
                                                                       dp::aringnative::DP_IMAGE_TYPE::YUV_NV21);

        return result;
    } catch (const std::exception &ex) {
        LOGF(TAG, "Exception ============================================");
        LOGF(TAG, "%s", ex.what());
    } catch (...) {
        LOGF(TAG, "Exception ============================================");
        LOGF(TAG, "Unknown exception");
    }
}

void postProcessing(cv::Mat dst, dp::aringnative::DPARingResult result) {
    // Get env pointer.
    JNIEnv *env;
    jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
    assert (rs == JNI_OK);

    try {
        if (result.detected) {
            drawEar(dst, g_matEarring, result.mask, true, result.leftEarringPos, result.rotX,
                    result.earringScaleYLeft); // left earing 그리기
            drawEar(dst, g_matEarring, result.mask, false, result.rightEarringPos,
                    result.rotX, result.earringScaleYRight); // right earing 그리기
        }

        // Initialize a pDstRgba variable.
        // A pointer of buffer is assigned to pDstRgab.
        ANativeWindow *m_pNativeWindow = ANativeWindow_fromSurface(env, g_surface);
        ANativeWindow_acquire(m_pNativeWindow);
        ANativeWindow_setBuffersGeometry(m_pNativeWindow, g_srcWidth, g_srcHeight,
                                         0 /*format unchanged*/);

        ANativeWindow_Buffer buf;
        if (int32_t err = ANativeWindow_lock(m_pNativeWindow, &buf, NULL)) {
            ANativeWindow_release(m_pNativeWindow);
            std::ostringstream s;
            s << "ANativeWindow_lock failed with error code " << err;
            throw std::runtime_error(s.str());
        }

        if (buf.stride != g_srcWidth) {
            std::ostringstream s;
            s << "buf.stride(" << buf.stride << ") is not equal srcWidth(" << g_srcWidth
              << ").";
            throw std::runtime_error(s.str());
        }

        uint8_t *dstLumaPtr = reinterpret_cast<uint8_t *>(buf.bits);
        cv::Mat m_dstRgba = cv::Mat(g_srcHeight, buf.stride, CV_8UC4,
                                    dstLumaPtr); // TextureView buffer, use stride as width

        dst.copyTo(m_dstRgba);

        if (m_pNativeWindow != nullptr) {
            ANativeWindow_unlockAndPost(m_pNativeWindow);
            ANativeWindow_release(m_pNativeWindow);
        }

    } catch (const std::exception &ex) {
        LOGF(TAG, "Post processing exception ============================================");
        LOGF(TAG, "%s", ex.what());
    } catch (...) {
        LOGF(TAG, "Post processing exception ============================================");
        LOGF(TAG, "Unknown exception");
    }
}

void processingThread() {
    std::future<void> fut = g_promiseProcess.get_future();
    while (fut.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout) {
        cv::Mat src, dst;
        {
            std::unique_lock<std::mutex> locker(g_processMutex);
            if (g_processQ.empty()) {
                g_condition.wait(locker);
            }

            while (!g_processQ.empty()) {
                std::tuple<cv::Mat, cv::Mat> t = g_processQ.front();
                g_processQ.pop();

                src = std::get<0>(t);
                dst = std::get<1>(t);
            }
        }
        if (!src.empty() && !dst.empty()) {
            dp::aringnative::DPARingResult result = processing(src, dst);

            // Execute post-processing.
            setPostprocessThreadInput(dst, result);
        }
    }
}

void postProcessingThread() {
    std::future<void> fut = g_promisePostprocess.get_future();
    while (fut.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout) {
        cv::Mat dst;
        dp::aringnative::DPARingResult result;
        {
            std::unique_lock<std::mutex> locker(g_postprocessMutex);
            if (g_postProcessQ.empty()) {
                g_postCondition.wait(locker);
            }

            while (!g_postProcessQ.empty()) {
                std::tuple<cv::Mat, dp::aringnative::DPARingResult> t = g_postProcessQ.front();
                g_postProcessQ.pop();

                dst = std::get<0>(t);
                result = std::get<1>(t);
            }
        }
        if (!dst.empty())
            postProcessing(dst, result);
    }
}

void setProcessThreadInput(cv::Mat &src, cv::Mat &dst) {
    std::unique_lock<std::mutex> locker(g_processMutex);
    g_processQ.push(std::make_tuple(src, dst));
    g_condition.notify_all();
}

void setPostprocessThreadInput(cv::Mat &dst, dp::aringnative::DPARingResult &result) {
    std::unique_lock<std::mutex> locker(g_postprocessMutex);
    g_postProcessQ.push(std::make_tuple(dst, result));
    g_postCondition.notify_all();
}

void drawEar(cv::Mat &dst, cv::Mat matEarPic, cv::Mat mask, bool isLeft, cv::Point earringPos,
             float rotX, float earringScaleY) {
    if (earringPos.x <= 0 || earringPos.y <= 0
        || earringPos.x >= dst.cols || earringPos.y >= dst.rows)
        return;

    cv::Mat matEarring;
    float earringScale = g_earringScale * earringScaleY * 0.6f;
    cv::resize(g_matRotatedEarring, matEarring, cv::Size(0, 0), earringScale, earringScale);

    cv::Rect earringRect(0, 0, matEarring.cols, matEarring.rows);
    earringRect = earringRect + earringPos - (g_earringAnchorPos * earringScale);

    cv::Rect safeRect(0, 0, dst.cols, dst.rows);
    earringRect = earringRect & safeRect;

    if (matEarring.size().area() != earringRect.area())
        return;

    // To apply mask only when its not visible
    if (isLeft && rotX >= -15) {
        matEarring.setTo(0, mask(earringRect));
    } else if (!isLeft && rotX <= 15) {
        matEarring.setTo(0, mask(earringRect));
    }

    cv::Mat earingMask;
    cv::compare(matEarring, 0, earingMask, cv::CMP_GT);

    cv::subtract(dst(earringRect), earingMask, dst(earringRect));
    cv::add(matEarring, dst(earringRect), dst(earringRect));
}

JNIEXPORT void JNICALL
Java_com_example_android_camera2basic_Camera2BasicFragment_create(JNIEnv *env, jobject,
                                                                  jobject activity,
                                                                  jstring earringAbolutePath) {
    try {
        g_ptrARing = dp::android::DPFactoryForAndroid::CreateInstance<IARing>
                (env, activity);
    } catch (const dp::exception::DPLicenseExpiredException &ex) {
        LOGF(TAG, "%s", ex.what());
    } catch (const dp::exception::DPLicenseException &ex) {
        LOGF(TAG, "%s", ex.what());
    } catch (const std::exception &ex) {
        LOGF(TAG, "%s", ex.what());
    }

    const char *pEarringAbsolutePath = env->GetStringUTFChars(earringAbolutePath,
                                                              0);
    g_matEarring = cv::imread(pEarringAbsolutePath);
    if (g_matEarring.empty()) {
        LOGF("ARing", "Cannot open file(%s).", pEarringAbsolutePath);
        return;
    }
    cv::cvtColor(g_matEarring, g_matEarring, CV_BGR2RGBA);

    init();
    initJVM(env);
}

JNIEXPORT void JNICALL
Java_com_example_android_camera2basic_Camera2BasicFragment_init(JNIEnv *env, jobject,
                                                                jint width, jint height,
                                                                jobject surface) {
    // Portrait 모드 이기 때문에 width 와 height 값 위치 변경
    // Camera sensor 는 landscape 에 맞춰져 있다.
    g_ptrARing->initialize((int) width, (int) height,
                           dp::aringnative::DP_IMAGE_ROTATION::CW_270); // ARing 초기화
    g_surface = env->NewGlobalRef(surface);
    g_srcWidth = width;
    g_srcHeight = height;

    // Rotate earring image.
    cv::Mat tmpDst = g_matEarring.t();
    cv::flip(tmpDst, g_matRotatedEarring, 1);
    g_earringAnchorPos.x = (int) (g_matRotatedEarring.cols / 8.0f * 7.0f);
    g_earringAnchorPos.y = (int) (g_matRotatedEarring.rows / 2.0f);
    g_earringScale = (float) g_srcWidth / (float) g_matRotatedEarring.cols;
}

JNIEXPORT void JNICALL
Java_com_example_android_camera2basic_Camera2BasicFragment_process(JNIEnv *env, jobject,
                                                                   jbyteArray srcBuffer, jint width,
                                                                   jint height) {

    jbyte *pSrcBuffer = env->GetByteArrayElements(srcBuffer, NULL);
    cv::Mat src;
    cv::Mat dst;

    preProcessing(pSrcBuffer, src, dst);
    setProcessThreadInput(src, dst);
}


}
