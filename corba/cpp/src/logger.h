#ifndef LOGGER_H
#define LOGGER_H

#include <string>

#include "Alitheia.h"

namespace Alitheia
{
    class Logger
    {
    public:
        static const std::string NameSqoOss;
        static const std::string NameSqoOssService;
        static const std::string NameSqoOssDatabase;
        static const std::string NameSqoOssSecurity;
        static const std::string NameSqoOssMessaging;
        static const std::string NameSqoOssWebServices;
        static const std::string NameSqoOssScheduling;
        static const std::string NameSqoOssUpdater;
        static const std::string NameSqoOssWebAdmin;
        static const std::string NameSqoOssTDS;
        static const std::string NameSqoOssFDS;
        static const std::string NameSqoOssMetric;
        static const std::string NameSqoOssTester;

        explicit Logger( const std::string& name = NameSqoOss );
        virtual ~Logger();

        void debug( const std::string& message );
        void info( const std::string& message );
        void warn( const std::string& message );
        void error( const std::string& message );

        std::string name() const;

    private:
        std::string m_name;
        alitheia::Logger_var m_logger;
    };
}

#endif
