cmake_minimum_required(VERSION 3.22.1)
project(appcore CXX)

add_library(appcore SHARED native-lib.cpp)

target_include_directories(appcore PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

# Hide every symbol except those explicitly marked JNIEXPORT (only JNI_OnLoad).
target_compile_options(appcore PRIVATE
        -fvisibility=hidden
        -fvisibility-inlines-hidden
        -ffunction-sections
        -fdata-sections
        -Wall -Wextra)

target_link_options(appcore PRIVATE
        -Wl,--gc-sections
        -Wl,--exclude-libs,ALL
        # Strip all symbol tables from the shipped .so
        -s)
