#pragma once

#include <memory>
#include <jni.h>
#include <string>

namespace dp {
    namespace android {

        /**
        *@brief API Control Interface
        *@details This is a class to create instance.
        */
        class DPFactoryForAndroid {
        public:
            /*!
            * @brief Create instance of T that is interface of the object to be created.
            * @param[in] env Pointer of JNIEvn
            * @param[in] activity Main activity of the application.
			* @return An instance of shared pointer that contain a pointer of T instance.
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