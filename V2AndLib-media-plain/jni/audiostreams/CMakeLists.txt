CMAKE_MINIMUM_REQUIRED(VERSION 2.6)

find_package(Java REQUIRED)
find_package(JNI)

include(UseJava)

if (JNI_FOUND)
    message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message (STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()

AUX_SOURCE_DIRECTORY( . SOURCES )

include_directories(${JNI_INCLUDE_DIRS})

ADD_LIBRARY( audiostreams SHARED ${SOURCES} )
TARGET_LINK_LIBRARIES(audiostreams mpg123)
