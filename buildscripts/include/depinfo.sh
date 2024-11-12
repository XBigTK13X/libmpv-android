#!/bin/bash -e

## Dependency versions

v_sdk=11076708_latest
v_ndk=27.1.12297006
v_sdk_build_tools=35.0.0

v_lua=5.2.4
v_libass=0.17.3
v_harfbuzz=10.0.1
v_fribidi=1.0.16
v_freetype=2-13-3
v_mbedtls=3.6.1
v_libplacebo=7.349.0
v_dav1d=1.4.3
v_ffmpeg=7.1
v_mpv=0.39.0


## Dependency tree
# I would've used a dict but putting arrays in a dict is not a thing

dep_mbedtls=()
dep_dav1d=()
dep_ffmpeg=(mbedtls dav1d)
dep_freetype2=()
dep_fribidi=()
dep_harfbuzz=()
dep_libass=(freetype fribidi harfbuzz)
dep_lua=()
dep_libplacebo=()
dep_mpv=(ffmpeg libass lua libplacebo)
dep_mpv_android=(mpv)

