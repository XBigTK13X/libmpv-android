FROM mingc/android-build-box

RUN apt update && apt upgrade -y

RUN apt install -y git curl wget sudo

COPY ./ /libmpv

WORKDIR /libmpv

RUN bash ./buildscripts/download.sh

RUN bash ./buildscripts/patch.sh

RUN bash ./buildscripts/build.sh mpv-android

CMD ["bash"]