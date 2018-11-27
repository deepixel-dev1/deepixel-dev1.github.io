#include <jni.h>
#include <android/log.h>
#include <DPFactoryForAndroid.h>
#include <IAR1ing.h>
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

using namespace dp::ar1ingnative;
std::shared_ptr<IAR1ing> g_ptrAR1ing;
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
cv::Mat g_matRing;
cv::Mat g_matBand;

#if defined(FACEAR)
std::queue<std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingFaceOutput>> g_postProcessQ;
#elif defined(HANDAR)
std::queue<std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingHandOutput>> g_postProcessQ;
#else
std::queue<std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingWristOutput>> g_postProcessQ;
#endif
std::promise<void> g_promiseProcess;
std::promise<void> g_promisePostprocess;

void processingThread();
void postProcessingThread();
void draw_band_or_ring(const cv::_InputOutputArray &_matDisplay, std::vector<cv::Point2f> pts,
					   float thickness, cv::Mat matBand);

#if defined(FACEAR)

void drawEar(cv::Mat &dst, cv::Mat matEarPic, cv::Mat mask, bool isLeft, cv::Point earringPos,
			 float rotX, float earringScaleY);
void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingFaceOutput &result);

#elif defined(HANDAR)

void draw_ring(const cv::_InputOutputArray &_matDisplay,
			   dp::ar1ingnative::DPAR1ingHandOutput result);
void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingHandOutput &result);

#else

void draw_band(const cv::_InputOutputArray &_matDisplay,
			   dp::ar1ingnative::DPAR1ingWristOutput result);
void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingWristOutput &result);

#endif

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
	// Copy image to improve speed on android device.
	src = srcYuv.clone();

	// convert YUV -> BGR
	cv::cvtColor(src, dst, CV_YUV2RGBA_NV21);
	src = dst;
}

void setProcessThreadInput(cv::Mat &src, cv::Mat &dst) {
	std::unique_lock<std::mutex> locker(g_processMutex);
	g_processQ.push(std::make_tuple(src, dst));
	g_condition.notify_all();
}

#if defined(FACEAR)

dp::ar1ingnative::DPAR1ingFaceOutput processing(cv::Mat src, cv::Mat &dst) {
	try {
		// Get env pointer.
		assert(g_pJvm != nullptr);
		JNIEnv *env;
		jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
		assert (rs == JNI_OK);

		dp::ar1ingnative::DPAR1ingFaceInput input;
		input.matSrc = src;
		input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::RGBA_8888;
		input.earOffset3D[0] = 3;
		input.earOffset3D[1] = 2;
		input.earOffset3D[2] = 0;
		input.nMaskOffset = 1;
		input.nStableOffset = -4;
		dp::ar1ingnative::DPAR1ingFaceOutput result = g_ptrAR1ing->DetectFace(input);

		return result;
	} catch (const std::exception &ex) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "%s", ex.what());
	} catch (...) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "Unknown exception");
	}
}

void postProcessing(cv::Mat dst, dp::ar1ingnative::DPAR1ingFaceOutput result) {
	// Get env pointer.
	JNIEnv *env;
	jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
	assert (rs == JNI_OK);

	try {
		if (result.bDetected) {
			drawEar(dst, g_matEarring, result.matMask, true, result.ptLeftEarring, result.fRotX,
					result.fEarringScaleYLeft); // left earing 그리기
			drawEar(dst, g_matEarring, result.matMask, false, result.ptRightEarring,
					result.fRotX, result.fEarringScaleYRight); // right earing 그리기
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
			dp::ar1ingnative::DPAR1ingFaceOutput result = processing(src, dst);

			// Execute post-processing.
			setPostProcessThreadInput(dst, result);
		}
	}
}

void postProcessingThread() {
	std::future<void> fut = g_promisePostprocess.get_future();
	while (fut.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout) {
		cv::Mat dst;
		dp::ar1ingnative::DPAR1ingFaceOutput result;
		{
			std::unique_lock<std::mutex> locker(g_postprocessMutex);
			if (g_postProcessQ.empty()) {
				g_postCondition.wait(locker);
			}

			while (!g_postProcessQ.empty()) {
				std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingFaceOutput> t = g_postProcessQ.front();
				g_postProcessQ.pop();

				dst = std::get<0>(t);
				result = std::get<1>(t);
			}
		}
		if (!dst.empty())
			postProcessing(dst, result);
	}
}

void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingFaceOutput &result) {
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

#elif defined(HANDAR)

dp::ar1ingnative::DPAR1ingHandOutput processing(cv::Mat src, cv::Mat &dst) {
	try {
		// Get env pointer.
		assert(g_pJvm != nullptr);
		JNIEnv *env;
		jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
		assert (rs == JNI_OK);

		dp::ar1ingnative::DPAR1ingHandInput input;
		input.matSrc = src;
		input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::RGBA_8888;
		input.vFingers = {false, true, false, false, false};
		input.fOffset = 0.0f;
		input.bRightHand = false;
		dp::ar1ingnative::DPAR1ingHandOutput result = g_ptrAR1ing->DetectHand(input);

		return result;
	} catch (const std::exception &ex) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "%s", ex.what());
	} catch (...) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "Unknown exception");
	}
}

void postProcessing(cv::Mat dst, dp::ar1ingnative::DPAR1ingHandOutput result) {
	// Get env pointer.
	JNIEnv *env;
	jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
	assert (rs == JNI_OK);

	try {
		draw_ring(dst, result);

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
			dp::ar1ingnative::DPAR1ingHandOutput result = processing(src, dst);

			// Execute post-processing.
			setPostProcessThreadInput(dst, result);
		}
	}
}

void postProcessingThread() {
	std::future<void> fut = g_promisePostprocess.get_future();
	while (fut.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout) {
		cv::Mat dst;
		dp::ar1ingnative::DPAR1ingHandOutput result;
		{
			std::unique_lock<std::mutex> locker(g_postprocessMutex);
			if (g_postProcessQ.empty()) {
				g_postCondition.wait(locker);
			}

			while (!g_postProcessQ.empty()) {
				std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingHandOutput> t = g_postProcessQ.front();
				g_postProcessQ.pop();

				dst = std::get<0>(t);
				result = std::get<1>(t);
			}
		}
		if (!dst.empty())
			postProcessing(dst, result);
	}
}

void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingHandOutput &result) {
	std::unique_lock<std::mutex> locker(g_postprocessMutex);
	g_postProcessQ.push(std::make_tuple(dst, result));
	g_postCondition.notify_all();
}

void draw_ring(cv::InputOutputArray _matDisplay,
			   dp::ar1ingnative::DPAR1ingHandOutput result) {
	if (!result.bDetected) {
		return;
	}

	for (int i = 0; i < 5; i++) {
		if (!result.vFinger[i])
			continue;

		std::vector<cv::Point2f> fingerPt;
		for (int j = 0; j < result.vFingerPts[i].size(); j++) {
			fingerPt.push_back(result.vFingerPts[i][j]);
		}

		draw_band_or_ring(_matDisplay, fingerPt, 0.1f, g_matRing);
	}
}

#else

dp::ar1ingnative::DPAR1ingWristOutput processing(cv::Mat src, cv::Mat &dst) {
	try {
		// Get env pointer.
		assert(g_pJvm != nullptr);
		JNIEnv *env;
		jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
		assert (rs == JNI_OK);

		dp::ar1ingnative::DPAR1ingWristInput input;
		input.matSrc = src;
		input.enumImageType = dp::ar1ingnative::DP_IMAGE_TYPE::RGBA_8888;
		input.fOffset = 0.0f;
		input.bRightHand = false;
		dp::ar1ingnative::DPAR1ingWristOutput result = g_ptrAR1ing->DetectWrist(input);

		return result;
	} catch (const std::exception &ex) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "%s", ex.what());
	} catch (...) {
		LOGF(TAG, "Exception ============================================");
		LOGF(TAG, "Unknown exception");
	}
}

void postProcessing(cv::Mat dst, dp::ar1ingnative::DPAR1ingWristOutput result) {
	// Get env pointer.
	JNIEnv *env;
	jint rs = g_pJvm->AttachCurrentThread(&env, NULL);
	assert (rs == JNI_OK);

	try {

		draw_band(dst, result);

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
			dp::ar1ingnative::DPAR1ingWristOutput result = processing(src, dst);

			// Execute post-processing.
			setPostProcessThreadInput(dst, result);
		}
	}
}

void postProcessingThread() {
	std::future<void> fut = g_promisePostprocess.get_future();
	while (fut.wait_for(std::chrono::milliseconds(1)) == std::future_status::timeout) {
		cv::Mat dst;
		dp::ar1ingnative::DPAR1ingWristOutput result;
		{
			std::unique_lock<std::mutex> locker(g_postprocessMutex);
			if (g_postProcessQ.empty()) {
				g_postCondition.wait(locker);
			}

			while (!g_postProcessQ.empty()) {
				std::tuple<cv::Mat, dp::ar1ingnative::DPAR1ingWristOutput> t = g_postProcessQ.front();
				g_postProcessQ.pop();

				dst = std::get<0>(t);
				result = std::get<1>(t);
			}
		}
		if (!dst.empty())
			postProcessing(dst, result);
	}
}

void setPostProcessThreadInput(cv::Mat &dst, dp::ar1ingnative::DPAR1ingWristOutput &result) {
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

void draw_band(cv::InputOutputArray _matDisplay,
			   dp::ar1ingnative::DPAR1ingWristOutput result) {
	if (!result.bDetected)
		return;

	std::vector<cv::Point2f> vWristPt;
	for (int j = 0; j < result.vWristPt.size(); j++) {
		vWristPt.push_back(result.vWristPt[j]);
	}

	draw_band_or_ring(_matDisplay, vWristPt, 0.05f, g_matBand);
}

#endif

cv::Point2d getOrthogornal(cv::Point2d pt1, cv::Point2d pt2) {
	cv::Point2d diffPt = pt2 - pt1;

	float l = std::sqrt(diffPt.x * diffPt.x + diffPt.y * diffPt.y);
	cv::Point2f smallDiff = diffPt / l;

	return cv::Point2d(smallDiff.y, -smallDiff.x);
}

void draw_band_or_ring(cv::InputOutputArray _matDisplay, std::vector<cv::Point2f> pts,
					   float thickness, cv::Mat matBand) {
	cv::Mat matDisplay = _matDisplay.getMat();

	std::vector<cv::Point2f> vWristPt = pts;
	std::vector<cv::Point2f> imagePt;

	vWristPt.resize(3);
	imagePt.resize(3);

	int nRingThickness = thickness * matBand.cols;
	imagePt[0] = cv::Point2f(nRingThickness, matBand.rows / 2);
	imagePt[1] = cv::Point2f(matBand.cols - nRingThickness, matBand.rows / 2);
	imagePt[2] = cv::Point2f(matBand.cols / 2, 0);

	LOGF("TEST", "0 = %0.3f, %0.3f     %.0.3f, %.0.3f", vWristPt[0].x, vWristPt[0].y, vWristPt[1].x,
		 vWristPt[1].y);
	cv::Point2f wristCt = cv::Point2f(vWristPt[0] + vWristPt[1]) / 2.0;
	cv::Point2f diffPt = vWristPt[1] - vWristPt[0];
	float wristWidth = std::sqrt(diffPt.x * diffPt.x + diffPt.y * diffPt.y);

	cv::Point2d ortho_vec = getOrthogornal(cv::Point2d(vWristPt[0]), cv::Point2d(vWristPt[1]));

	float wristHeight = wristWidth * (matBand.rows - nRingThickness * 2) / matBand.cols;
	vWristPt[2] = wristCt + cv::Point2f(ortho_vec) * wristHeight;

	// Find the original bounding rect
	cv::Rect wristRect = cv::boundingRect(vWristPt);

	cv::Point cnt = (wristRect.tl() + wristRect.br()) / 2;
	cv::Point rad = cv::Point(wristRect.width, wristRect.height);
	wristRect = cv::Rect(cnt - rad, cnt + rad);

	// Skip when band is out of range
	cv::Rect safeRect(0, 0, matDisplay.cols, matDisplay.rows);
	cv::Rect cropedRect = safeRect & wristRect;
	if (cropedRect.area() != wristRect.area()) {
		return;
	}

	// Process the bare minimum area of band
	vWristPt[0] -= cv::Point2f(wristRect.tl());
	vWristPt[1] -= cv::Point2f(wristRect.tl());
	vWristPt[2] -= cv::Point2f(wristRect.tl());

	cv::Mat matBand_canvas;
	cv::Mat matWrist(vWristPt);
	cv::Mat matImage(imagePt);
	matWrist = matWrist.reshape(1, 3);
	matImage = matImage.reshape(1, 3);

	matWrist.convertTo(matWrist, CV_32F);
	matImage.convertTo(matImage, CV_32F);

	cv::Mat matAffine = cv::estimateRigidTransform(matImage, matWrist, false);
	if (matAffine.empty()) {
		return;
	}
	cv::warpAffine(matBand, matBand_canvas, matAffine, wristRect.size());

	// Extract useful area from the band image
	cv::Mat bandMask;
	cv::cvtColor(matBand_canvas, bandMask, cv::COLOR_BGR2GRAY);
	bandMask = bandMask > 30;

	cv::cvtColor(matBand_canvas, matBand_canvas, CV_BGR2RGBA);
	matBand_canvas.copyTo(matDisplay(wristRect), bandMask);
}

JNIEXPORT void JNICALL
Java_com_example_android_camera2basic_Camera2BasicFragment_create(JNIEnv *env, jobject,
																  jobject activity,
																  jstring earringAbolutePath,
																  jstring ringAbolutePath,
																  jstring bandAbolutePath) {
	try {
		g_ptrAR1ing = dp::android::DPFactoryForAndroid::CreateInstance<IAR1ing>
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
		LOGF("AR1ing", "Cannot open file(%s).", pEarringAbsolutePath);
		return;
	}
	cv::cvtColor(g_matEarring, g_matEarring, CV_BGR2RGBA);

	const char *pRingAbsolutePath = env->GetStringUTFChars(ringAbolutePath,
														   0);
	g_matRing = cv::imread(pRingAbsolutePath);
	if (g_matRing.empty()) {
		LOGF("AR1ing", "Cannot open file(%s).", pRingAbsolutePath);
		return;
	}

	const char *pBandAbsolutePath = env->GetStringUTFChars(bandAbolutePath,
														   0);
	g_matBand = cv::imread(pBandAbsolutePath);
	if (g_matBand.empty()) {
		LOGF("AR1ing", "Cannot open file(%s).", pBandAbsolutePath);
		return;
	}

	init();
	initJVM(env);
}

JNIEXPORT void JNICALL
Java_com_example_android_camera2basic_Camera2BasicFragment_init(JNIEnv *env, jobject,
																jint width, jint height,
																jobject surface) {
#ifdef FACEAR
	g_ptrAR1ing->initialize((int) width, (int) height,
							dp::ar1ingnative::DP_IMAGE_ROTATION::CW_270); // AR1ing 초기화
#else
	g_ptrAR1ing->initialize((int) width, (int) height,
							dp::ar1ingnative::DP_IMAGE_ROTATION::CW_90); // AR1ing 초기화
#endif
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
