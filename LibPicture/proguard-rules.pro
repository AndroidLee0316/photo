# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
 -keep public class * implements com.bumptech.glide.module.GlideModule
 -keep public class * extends com.bumptech.glide.AppGlideModule
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

#RxJava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex; long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef { rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef { rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
################crop###############
-keep class com.soundcloud.android.** { *; }
-keep interface com.soundcloud.android.** { *; }

# 保留在 Activity 中的方法setIsHeadImg 和 actionStart
-keepclassmembers class * extends android.app.Activity{
   *;
 }
-keep class com.pasc.lib.picture.takephoto.**{*;}
-keep class com.pasc.lib.picture.pictureSelect.ImagePicker{*;}
-keepattributes SourceFile,LineNumberTable,InnerClasses

# 对于R（资源）类中的静态属性不能被混淆
-keepclassmembers class **.R$* {
 public static <fields>;
}
-keep class com.pasc.lib.picture.util.**{*;}

