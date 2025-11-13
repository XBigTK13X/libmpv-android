#! /bin/bash

if [ -z "$1" ];then
    echo "Version required (current version below)"
    ls -lt ~/maven-repo/com/libmpv/android-libmpv | head -n 2 | tail -n 1
    exit 1
fi

./buildscripts/build.sh mpv-android -n

python script/publish-local.py $1