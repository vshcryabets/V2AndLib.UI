CMAKE_MINIMUM_REQUIRED(VERSION 2.6)

PROJECT(V2AndLib-media-plain)
INCLUDE_DIRECTORIES(mpg123 jpeg-9a lame libpng) 
ADD_DEFINITIONS(-g -O2 -fsigned-char -Wall)

ADD_SUBDIRECTORY( mpg123 ./out/mpg123 )
ADD_SUBDIRECTORY( jpeg-9a ./out/jpeg )
ADD_SUBDIRECTORY( libpng ./out/libpng )
ADD_SUBDIRECTORY( audiostreams ./out/audiostreams )
ADD_SUBDIRECTORY( graphics ./out/graphics )
ADD_SUBDIRECTORY( lame ./out/lame )
