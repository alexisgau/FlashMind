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
-dontwarn com.gemalto.jp2.JP2Decoder

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.* { *; }
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.json.** { *; }
-keep,allowobfuscation,allowshrinking class kotlinx.serialization.internal.** { *; }

# Mantener las clases serializables y sus serializadores generados
-keep @kotlinx.serialization.Serializable class * {
    # Mantener el método secundario 'write$Self' generado
    static **$serializer serializer();
}

# Mantener el companion object que contiene el serializer
-keepclassmembers class * {
    @kotlinx.serialization.Serializer
    <init>(...);
}

# Regla específica si usas @SerialName (para mantener los nombres de los campos JSON)
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# --- Fin Kotlinx Serialization ---

# --- FIREBASE FIRESTORE MODELS ---
# Evita que R8 renombre las clases y sus campos (a, b, c...)
# y evita que elimine el constructor vacío necesario para la deserialización.

# Asegúrate de que esta ruta sea correcta: com.example.flashmind.data.network.dto
-keep class com.example.flashmind.data.network.dto.** { *; }

# También es buena práctica mantener los modelos de dominio si los usas en algún lugar con reflexión
-keep class com.example.flashmind.domain.model.** { *; }

# --- RETROFIT & GSON/SERIALIZATION ---

# 1. ¡CRÍTICO! Mantiene la información de genéricos (ej. Call<SummaryResponse>)
# Sin esto, Retrofit no sabe qué tipo de objeto devolver y lanza el ClassCastException.
-keepattributes Signature, InnerClasses, EnclosingMethod

# 2. Mantener las interfaces de Retrofit (donde tienes @GET, @POST)
# Esto evita que R8 renombre los métodos de tu API Service.
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# 3. Mantener OkHttp (si lo usas internamente)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# 4. Asegurar que tus DTOs (Data classes) no sean renombrados (ya lo tenías, pero verifica)
-keep class com.example.flashmind.data.network.dto.** { *; }
-keep class com.example.flashmind.data.network.model.** { *; }


# --- REGLAS CRÍTICAS PARA KOTLIN Y RETROFIT ---

# Mantiene la metadata de Kotlin.
# Retrofit + Coroutines necesitan esto para entender las funciones 'suspend'.
-keepattributes KotlinMetadata
-keep class kotlin.Metadata { *; }

# Mantiene TODA la información de firmas y anotaciones
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Protege explícitamente tu interfaz de la API (Ajusta el paquete si es diferente)
# Esto es vital: obliga a mantener los métodos 'generateSummary' y sus tipos de retorno.
-keep interface com.example.flashmind.data.network.** { *; }

# --- FIN REGLAS CRÍTICAS ---

# --- ARREGLO PARA CRASH EN ANDROID < 13 ---
-keep class androidx.core.content.pm.** { *; }
-keep class android.support.v4.content.pm.** { *; }
-dontwarn android.content.pm.PackageManager$PackageInfoFlags