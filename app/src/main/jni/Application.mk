# $Id: Application.mk 212 2015-05-15 10:22:36Z oparviai $
#
# Build library bilaries for all supported architectures
#

APP_ABI := armeabi-v7a #armeabi
APP_OPTIM := release
APP_STL := stlport_static
APP_CPPFLAGS := -fexceptions # -D SOUNDTOUCH_DISABLE_X86_OPTIMIZATIONS
APP_ALLOW_MISSING_DEPS=true