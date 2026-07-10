# ProGuard rules for Go Tour N Travels

# Keep Kotlin metadata
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }

# Retrofit
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson models
-keep class com.gotourntravels.models.** { *; }
-keep class com.gotourntravels.network.dto.** { *; }

# Razorpay
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# Compose
-keep class androidx.compose.** { *; }
