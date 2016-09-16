# android_caffe_jni

```shell
git clone --recursive https://github.com/xxxzxxx/android_caffe_jni.git
```

## Build

### caffe-android-lib

```shell
cd caffe-android-lib
# build target device-api-level
./build_caffe.sh
```

## Setup

### symbolic link

case use android-api-level 14 module

```shell
cd caffe-mobile
rm libs
ln -s ./../caffe-android-lib/CaffeMobile/android-14 ./libs
```

case use android-api-level 21 module
```shell
cd caffe-mobile
rm libs
ln -s ./../caffe-android-lib/CaffeMobile/android-21 ./libs
```

