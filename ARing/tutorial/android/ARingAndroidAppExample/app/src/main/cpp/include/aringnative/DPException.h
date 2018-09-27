//
// Created by honam on 2018-05-15.
//

#ifndef DPEXCEPTION_H
#define DPEXCEPTION_H

#include <exception>
#include <string>
#include <stdexcept>

namespace dp {
    namespace exception {

        /**
        *@brief Exception class
        *@details The exceptions that occur while using the deepixel library are inherited from this class.
        */
        class DPException : public std::runtime_error {
        public:
            DPException(const std::string &message);

            virtual ~DPException() {}
        };

        /**
        *@brief Exception class
        *@details This exception is occurred when the application id is invalid.
        */
        class DPLicenseException : public DPException {
        public:
            DPLicenseException(const std::string &message);

            virtual ~DPLicenseException() {}
        };

        /**
        *@brief Exception class
        *@details This exception is occurred when the license is expired.
        */
        class DPLicenseExpiredException : public DPLicenseException {
        public:
            DPLicenseExpiredException(const std::string &message);

            virtual ~DPLicenseExpiredException() {}
        };

    }
}


#endif //DPEXCEPTION_H
