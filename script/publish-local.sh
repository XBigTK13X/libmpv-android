#! /bin/bash

if [ -z "$1" ];then
    ls -lrt /home/kretst/maven-repo/com/libmpv/android-libmpv
    exit 0
fi

python script/local-publish.py "$1"
