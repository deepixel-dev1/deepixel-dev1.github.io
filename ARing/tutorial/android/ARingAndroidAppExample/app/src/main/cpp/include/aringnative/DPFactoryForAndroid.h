#pragma once

#include <memory>
#include <jni.h>
#include <string>

namespace dp {
    namespace android {

        /**
        *@brief ARing 인스턴스 생성 클래스 (안드로이드용)
        *@details DPFactoryForAndroid는 해당 오브젝트 인터페이스를 인스턴스로 생성하는 클래스 이다.
        *         객체를 생성하는 과정에서 라이센스와 관련된 예외가 발생할 수 있다.
        *
        *@code
        * <android>
        * std::shared_ptr<dp::aringnative::IARing> g_ptrARing =
        *                 dp::android::DPFactoryForAndroid::CreateInstance<dp::aringnative::IARing>(
        *                 env, activity);
        *@endcode
        */
        class DPFactoryForAndroid {
        public:
            /*!
            * @brief 생성할 오브젝트의 인터페이스의 인스턴스를 생성
            * @param[in] env JNIEvn 포인터
            * @param[in] activity 어플리케이션의 Main activity
            * @exception dp::exception::DPLicenseException
            *            > 어플리케이션 id가 유요하지 않을 때 발생한다.
            * @exception dp::exception::DPLicenseExpiredException
            *            > 라이센스가 만기 되었을 때 발생한다.
			* @return 생성된 인스턴스의 쉐어드 포인터가 리턴
            */
            template<typename T>
            static std::shared_ptr<T>
            CreateInstance(JNIEnv *env, jobject activity) {
                return std::shared_ptr<T>((T *) GetObject(env, activity));
            }

        private:
            DPFactoryForAndroid() {}

            virtual ~DPFactoryForAndroid() {};

            static void *GetObject(JNIEnv *env, jobject activity);
        };

    }
}