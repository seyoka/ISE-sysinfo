/*
 *  Example JNI class
 *
 *  Copyright (c) 2024 Mark Burkley (mark.burkley@ul.ie)
 */

#include <string.h>

#include "cpuInfo.h"
#include "sysInfo.h"

  
JNIEXPORT jint JNICALL Java_sysInfo_intExample
  (JNIEnv *env, jobject obj, jint num) {
   return num * num;
}

  
JNIEXPORT jstring JNICALL Java_sysInfo_stringExample
    (JNIEnv *env, jobject obj, jstring string)
{
    const char *name = env->GetStringUTFChars(string, NULL);
    char msg[60] = "Hello ";
    jstring result;

    strcat(msg, name);
    env->ReleaseStringUTFChars(string, name);
      
    result = env->NewStringUTF(msg);
    return result;
}

