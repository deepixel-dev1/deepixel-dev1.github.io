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
        *@brief MAKA API Control Interface
        *@details The exceptions that occur while using the MAKA library are inherited from this class.
        */
        class DPException : public std::runtime_error {
        public:
            DPException(const std::string &message);

            virtual ~DPException() {}
        };

        /**
        *@brief MAKA API Control Interface
        *@details This exception is occurred when the license file does not exists or is corrupt.
        */
        class DPLicenseException : public DPException {
        public:
            DPLicenseException(const std::string &message);

            virtual ~DPLicenseException() {}
        };

        /**
        *@brief MAKA API Control Interface
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
