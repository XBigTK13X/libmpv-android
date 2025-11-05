#! /bin/bash

docker build -t libmpv-android .

BUILD_CMD="cd /libmpv; ./buildscripts/build.sh mpv-android; cp /libmpv/libmpv/build/outputs/aar/libmpv-release.aar /output/libmpv.aar"

docker run -it --rm -v ./aar-output:/output libmpv-android bash -c "$BUILD_CMD"