#! /bin/bash

if [ ! -z "$1" ];then
    docker build -t libmpv-android .
fi

BUILD_CMD="cd /libmpv; ./buildscripts/build.sh mpv-android; cp /libmpv/libmpv/build/outputs/aar/libmpv-release.aar /output/libmpv.aar"

docker run -it --rm -v ./libmpv:/libmpv/libmpv -v ./aar-output:/output libmpv-android bash -c "$BUILD_CMD"