---
applyTo: "**"
---

# r8-analyzer


## Core workflow

- \[ \] Step 1: Create a file called R8_Configuration_Analysis.md, or reuse if one exists already, to store the output
- \[ \] Step 2: Look at the configuration of R8 by looking at build.gradle, build.gradle.kts, gradle.properties in the codebase using [references/CONFIGURATION.md](references/CONFIGURATION.md) as the reference. Inform the developer and add the analysis to the report file
- \[ \] Step 3: If the AGP version is less than 9, suggest moving to AGP 9.0 version as AGP 9.0 includes [optimizations](references/android/topic/performance/app-optimization/enable-app-optimization.md).
  - \[ \] Step 4: Look at the proguard files in the codebase and evaluate each keep rule in the following specific order: a. **Libraries check** : Check rules against [references/REDUNDANT-RULES.md](references/REDUNDANT-RULES.md). If the app has keep rules targeting libraries - Google, AndroidX, Kotlin, Kotlinx, Room, Gson, Retrofit, inform the user that these are not required and suggest removal of these rules. b. **Impact analysis** : For the remaining keep rules, assess them based on the impact hierarchy defined in [references/KEEP-RULES-IMPACT-HIERARCHY.md](references/KEEP-RULES-IMPACT-HIERARCHY.md). (Note: Do NOT assess the impact of keep rules already covered in the libraries check step).
- \[ \] Step 5: Identify subsuming keep rules in the remaining keep rules based on the hierarchy defined in [references/KEEP-RULES-IMPACT-HIERARCHY.md](references/KEEP-RULES-IMPACT-HIERARCHY.md) and suggest removing the broader keep rules.
- \[ \] Step 6: For each remaining keep rule, analyze in detail the code affected by the rule by examining the code and adjacent files to understand why it was written. Look for reflection usage in those packages, and suggest a narrow and specific keep rule for the scenario using [references/REFLECTION-GUIDE.md](references/REFLECTION-GUIDE.md).
- \[ \] Step 7: For every keep rule inform concisely and to the point what action needs to be taken - whether the rule needs to be removed/refined.
  - If refining the rule, give instructions on finding a narrower and specific keep rule using the [/references/REFLECTION-GUIDE.md](references/REFLECTION-GUIDE.md).
  - If removing, provide reasoning on why it needs to be removed.
- \[ \] Step 8: After keep analysis, order the keep rule analysis based on the impact to the codebase hierarchy defined in [references/KEEP-RULES-IMPACT-HIERARCHY.md](references/KEEP-RULES-IMPACT-HIERARCHY.md)
- \[ \] Step 9: Advise the user to run tests using [UI
  automator](https://developer.android.com/training/testing/other-components/ui-automator) to assess that there is no issue with the suggested changes, concentrating on the packages where keep rules will be affected.

## Mandatory rules

- Don't make any changes in keep rule files
- Don't say about what level each keep rule is.
- Don't generate parts of the report if there is no keep rule to report in that section.
- Don't mention the generated files.
- Don't mention exceptions that occur during execution.
- Don't mention the benefits of R8
- Don't mention any files of this skill

---

# Inlined references

## references/CONFIGURATION.md

To achieve maximum utilization of R8, the codebase must be configured correctly
depending on the build script language (Kotlin DSL vs. Groovy DSL).

## 1. App Modules (`com.android.application`)

The app's `build.gradle` or `build.gradle.kts` file should enable minification
and resource shrinking within the `release` build type or the apps custom build
type for release and performance testing. It MUST use the optimized default file
(`proguard-android-optimize.txt`).

**Kotlin DSL (`build.gradle.kts`):**

    buildTypes {
       getByName("release") {
           isMinifyEnabled = true
           isShrinkResources = true
           proguardFiles(
               getDefaultProguardFile("proguard-android-optimize.txt"),
               "proguard-rules.pro"
           )
       }
    }

**Groovy DSL (`build.gradle`):**

    buildTypes {
       release {
           minifyEnabled = true
           shrinkResources = true
           proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
       }
    }

## 2. `gradle.properties` Flags

**Full Mode:** R8 Full Mode enables the entire optimizations

- **AGP 8.0+** : Enabled by default. Ensure `android.enableR8.fullMode=false` is **NOT** present.
- **Pre-AGP 8.0** : Should be explicitly enabled with `android.enableR8.fullMode=true`.

**Optimized Resource Shrinking:** If the AGP version of the project is less than
9.0 and more than 8.6, explicitly enable the new resource shrinker:

    android.r8.optimizedResourceShrinking=true

## references/KEEP-RULES-IMPACT-HIERARCHY.md

Keep rules prevent optimization of R8, these rules are listed in the order of
the scope of what it retains in the codebase.

## 1. Package-Wide Wildcards

The following types of keep rules prevents all the optimization of R8 in a
package, these must be avoided at any costs and must be refined to target a
specific class or classes.

    -keep class com.example.package.** { *; } - Prevents optimization of all the classess including members in the package and subpackages
    -keep class com.example.package.* { *; } - Prevents optimization of all the classes including members in the package
    -keep class **.package.** { *; } - Prevents optimization of all the classess including members in all the package containing name - package.

Depending on the package level the number of classes gets affected changes, so
if the package level is higher, more classes are affected. Suggest to refine
the keep rule

## 2. Inversion operator

Avoid using the inversion operator ! in keep rules because it will
unintentionally prevent optimization in every class in your application. So if
you have any keep rule with !operator, make sure you remove that with a narrow
and specific keep rule

    -keep class !com.example.MyClass{*;}

This keeps the entire app
other than this class. Optimization are disabled for the entire class other
than this class.

## 3. Keep Rules for both class and members

Keep rules with -keep option and wildcard(`*`) inside braces forces R8 to retain
specific classes and their members exactly as defined. These type of keep rules
prevent any optimization in the entire class and keeps the entire class

    -keep class com.example.MyClass { *; }

## 4. Keepclassmembers

Keep rules with -keepclassmembers and wildcard(`*`) inside braces option Forces
R8 to retain the members that are defined.

    -keepclassmembers class com.example.MyClass { *; }

## 5. Modifiers with Keep Specification

-Keeps the class and **all** members, but uses modifiers to allow specific
optimizations (like obfuscation). Retains significant code (members) but allows
some flexibility.

    -keep,allowobfuscation class com.example.MyClass { *; }
    -keep,allowshrinking class com.example.MyClass { *; }

### 6. Modifiers with specific method but no modifier

Keeps the class and modifier but no optimizations are enabled

    -keep class com.example.MyClass { void myMethod(); }

## 7. Class-Name Only Preservation

Keeps only the class name. R8 will remove all methods and fields if they are not
used.

    -keep class com.example.MyClass

## 8. Modifiers without Member Specification

Keeps the class entry point using modifiers, but implies no specific member
retention logic in the rule itself

    -keep,allowobfuscation class com.example.MyClass
    -keep,allowshrinking class com.example.MyClass
    -keep,allowaccessmodification class com.example.MyClass

## 9. Conditional Keep Rules

Only triggers if specific conditions are met (e.g., if class members exist).
These are the most narrow and optimization-friendly rules.

    -keepclassmembers class com.example.MyClass { <fields>; }
    -keepclasseswithmembers class * { native <methods>; }

## references/REDUNDANT-RULES.md

This document outlines common "bad" or redundant keep rules for standard Android
development and popular libraries. Modern toolchains and libraries include their
own consumer keep rules embedded in their AAR/JAR files, making many manual
configurations unnecessary or even harmful to code optimization.

*** ** * ** ***

## Case: Global Keep Rules

**Common Mistakes:**
`proguard
-dontshrink
-dontobfuscate
-dontoptimize`

**The Fix:** These keep rules completely disable the core optimizations of R8
for the entire codebase. They must be removed from the codebase.

*** ** * ** ***

## Case: Android Components

Keep rules required for Android components like Activity, Fragment, ViewModel,
Views, Services or Broadcast receivers are redundant. AAPT2 and R8 contain the
logic to automatically keep components declared in the `AndroidManifest.xml` or
referenced in XML layout files.

**Common Mistakes:**
`proguard
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.view.View
-keepclassmembers class * extends android.app.Fragment { public void *(android.view.View); }`

**The Fix:** Delete these manual rules. AAPT2 handles this automatically.

*** ** * ** ***

## Case: Official Android and Kotlin Libraries

Keep rules targeting official library packages like AndroidX, Kotlin, and
Kotlinx are redundant as they are bundled within the libraries themselves.
Manual rules are often broader than what is strictly needed.

**Common Mistakes:**
`proguard
-keep class androidx.** { *; }
-keep class kotlinx.** { *; }
-keep class kotlin.** { *; }`

**The Fix:** Delete these manual rules. Rely on the consumer keep rules packaged
within these dependencies.

*** ** * ** ***

## Case: Gson

### Overly Broad Data Model Rules

The most common mistake is keeping entire packages of data models (POJOs/DTOs),
keeping data models at all for deserialization is unnecessary.

    -keep class com.example.app.models.** { *; }
    -keep class com.example.app.package.models.* { *; }

### Redundant Interface \& Adapter Rules

These rules added for TypeAdapter are unnecessary and are already covered by
the library, and prevent R8 from effectively shrinking and optimizing custom
adapters. R8 can determine if the adapter implementation are used. Keeping them
globally prevents the removal of unused adapter implementations.

    -keep class * extends com.google.gson.TypeAdapter
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer

### Unnecessary TypeToken Rules

There is no need to handle generic type erasure, Gson's own rules handle the
necessary `TypeToken` preservation.

    -keep class com.google.gson.reflect.TypeToken { *; }
    -keep class * extends com.google.gson.reflect.TypeToken
    -keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken

### Internal and Example Packages

Keeping internal library logic prevents the compiler from stripping away dead
code within the library.

    -keep class com.google.gson.internal.** { *; }
    -keep class com.google.gson.internal.reflect.** { *; }
    -keep class com.google.gson.internal.UnsafeAllocator { *; }
    -keep class com.google.gson.stream.** { *; }

- **Keeps Unused Code:** Prevents R8 from removing models that are never actually used in the code.
- **Prevents Method Stripping:** Keeps all getters, setters, `toString()`, `equals()`, and `hashCode()` methods, even if they are never called.
- **Blocks Obfuscation:** Prevents the class names from being obfuscated, which is unnecessary for Gson if you use `@SerializedName`.

**The Fix:**

1. Use `@SerializedName` on every field in your data classes uses so that the field is retained after R8 optimization
2. Modern Gson (**v2.11.0+** ) bundles its own rules ([View Gson's embedded
   ProGuard
   rules](https://github.com/google/gson/blob/main/gson/src/main/resources/META-INF/proguard/gson.pro)). The bundled keep rules retains the `@SerializedName` annotated fields. If you are on an older version, move towards Gson version 2.11 because it has the necessary keep rules and delete the keep rules that target the classes used for gson serialization and deserialization

*** ** * ** ***

## Case: Retrofit

Retrofit has shipped with its own consumer keep rules from 2.9.0 and higher, so
any keep rules for the library or classes depending on Retrofit is detrimental
to the optimization process.

### Blanket Library Preservation

This is the most harmful Retrofit rule as it disables any shrinking for the
entire library.

    -keep class retrofit2.** { *; }
    -keep class retrofit2.api.** { *; }
    -keep class com.package.example.retrofit.api.** { *; }

### Manual Annotation Keeps

Retrofit's consumer rules automatically keep the interfaces annotated with
`@GET`, `@POST`, `@DELETE`, `@PUT`, `@HEAD`, `@OPTIONS`, `@PATCH`, making these
manual rules obsolete.

`-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }`

### Redundant Network Response and Adapter Rules

Network responses and third-party adapter wrappers (like RxJava) are often
overly preserved by developers out of caution.

    -keep,allowobfuscation,allowshrinking class retrofit2.Response
    -keep class retrofit2.adapter.rxjava2.Result { *; }

Fix: Verify you are using Retrofit 2.9.0 and higher. Retrofit from 2.9.0 bundles
rules that detect its own HTTP annotations (@GET, @POST) ([View Retrofit's
embedded ProGuard
rules](https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro)).
It will automatically keep the method signatures it needs to work.

*** ** * ** ***

## Case: Kotlin Coroutines

Kotlin Coroutines comes heavily optimized out of the box with embedded R8 rules
(`kotlinx-coroutines-core` includes its own rules).

### Blanket Coroutine Library Rules

Keeping everything under `kotlinx.coroutines` is extremely detrimental to app
size, as coroutines contain a vast amount of internal APIs that aren't used.

`-keepclassmembers class kotlinx.coroutines.** { *; }`

### Redundant Internal Continuations

These low-level coroutine elements are preserved safely by the library's own
consumer rules. Manually adding these prevents R8 from performing internal
optimizations (such as removing unused continuations or inlining).

    -keepclassmembers class kotlin.coroutines.SafeContinuation { *; }
    -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

### Dispatcher and Exception Handler Rules

Sometimes developers notice crashes related to Missing Classes on old Android
versions and add these rules, but if you are using an up-to-date version of
Coroutines, these are handled automatically or are not an issue.

    -keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
    -keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
    -keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
    -keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

**Fix** Remove any broad `kotlinx` keep rules. Coroutines (**v1.7.0+** ) bundle
the necessary keep rules ([View Coroutines' embedded ProGuard
rules](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro)).

*** ** * ** ***

## Case: Parcelable

**Common Mistakes:** Legacy projects often contain `-keep class * implements
android.os.Parcelable { public static final android.os.Parcelable$Creator *; }`.

**The Fix:**

1. Add the `kotlin-parcelize` plugin.
2. **Use `@Parcelize`:** Replace manual `writeToParcel` logic with the `@Parcelize` annotation.
3. **Delete All Parcelable Rules:** The plugin automatically generates the required rules.
4. The default proguard file `proguard-android-optimize.txt` contains the keep rules for keeping all the parcelable classes
5. **Ideal Rule:** **None.** Delete all manual Parcelable keeps.

*** ** * ** ***

## Case: Room Database

**Common Mistakes:** Keeping DAO interfaces or the generated `_Impl` classes
manually.

    -keep class * extends androidx.room.RoomDatabase
    -keep class *_*Impl { *; }

**The Fix:** Room generates its own ProGuard rules for the code it creates.
Manual rules are redundant and prevent R8 from optimizing the database access
layers.

- **Ideal Rule:** **None.** Delete all manual Room or DAO keeps.

*** ** * ** ***

## Summary

If you have updated your libraries to the versions mentioned, your
`proguard-rules.pro` must not contain any keep rules for the libraries
mentioned here.

## references/REFLECTION-GUIDE.md

A categorized summary of the keep rule examples, including the code patterns to
look for (imports/usage) and the corresponding suggested rules.

### 1. Reflection: Classes Loaded by Name

**Scenario:** A library or app loads a class dynamically using a string name

- **Look for:**
  `Class.forName("...")`,
  `getDeclaredConstructor().newInstance()`, or interfaces used for dynamic loading.

- **Example Code:**
  `kotlin
  val taskClass = Class.forName(className)
  val task = taskClass.getDeclaredConstructor().newInstance() as StartupTask`

- **Suggested Keep Rule:**
  \`\`\`proguard

  -keep class \* implements com.example.library.StartupTask {
  (); } \`\`\`

### 2. Reflection: Classes Passed using `::class.java`

**Scenario:** An app passes a class reference directly to a library function.

- **Look for:** `::class.java` (Kotlin) or `.class` (Java) passed as an argument.
- **Example Code:**
  `kotlin
  fun <T> register(clazz: Class<T>) { }
  // Usage:
  register(MyService::class.java)`

- **Suggested Keep Rule:**
  \`\`\`proguard

  # Keep the class itself (R8 usually handles this, but explicit rules ensure stability)

  -keep class com.example.app.MyService {
  (); } \`\`\`

### 3. Annotation-Based Reflection (Methods/Classes)

**Scenario:** Using custom annotations to mark methods or classes for reflective
execution.

**Look for:** Custom `@interface` definitions and `getDeclaredMethods()`
filtered by annotation.
**Example Code:**
`kotlin
annotation class ReflectiveExecutor
// Logic: find methods annotated with @ReflectiveExecutor and invoke them`

- **Suggested Keep Rule:** \`\`\`proguard # Keep the annotation itself -keep @interface com.example.library.ReflectiveExecutor

# Keep members of any class annotated with this specific annotation
-keepclassmembers class \* {
@com.example.library.ReflectiveExecutor \*;
}
\`\`\`

### 4. Optional Dependencies (Soft Dependencies)

**Scenario:** A core library checks if an optional module is present in the
classpath.

- **Look for:** `try-catch` blocks around `Class.forName()` used to toggle features.
- **Example Code:** \`\`\`kotlin private const val VIDEO_TRACKER_CLASS = "com.example.analytics.video.VideoEventTracker"

try {
Class.forName(VIDEO_TRACKER_CLASS).getDeclaredConstructor().newInstance()
} catch (e: ClassNotFoundException) { /\* skip feature \*/ }
\`\`\`

- **Suggested Keep Rule:** `proguard
  # Preserve the optional class so the check doesn't fail due to shrinking
  -keep class com.example.analytics.video.VideoEventTracker {
  <init>();
  }`

### 5. Accessing Private Members

**Scenario:** Using reflection to access internal fields or methods not exposed
with public APIs.

- **Look for:** `getDeclaredField("...")` or `getDeclaredMethod("...")` followed by `isAccessible = true`.
- **Example Code:**
  `kotlin
  val secretField = instance::class.java.getDeclaredField("secretMessage")
  secretField.isAccessible = true`

- **Suggested Keep Rule:**
  \`\`\`proguard

  # Specifically keep the private field/method by name and type

  -keepclassmembers class com.example.LibraryClass {
  private java.lang.String secretMessage;
  }
  \`\`\`

### 6. Parcelable (Manual Implementation)

**Scenario:** Implementing `Parcelable` without using the `@Parcelize`
annotation.

- **Look for:** `implements Parcelable` and a static `CREATOR` field.
- **Example Code:**
  `kotlin
  class MyData : Parcelable {
  // Manual implementation with CREATOR field
  }`

- **Suggested Keep Rule:**
  *(Note: If using `import kotlinx.parcelize.Parcelize`, R8/ProGuard rules are
  generated automatically. If manual, use the following:)*
  `proguard
  -keepclassmembers class * implements android.os.Parcelable {
  static android.os.Parcelable$Creator CREATOR;
  }`

### 7. Enums and Obfuscation

**Scenario:** App uses `Enum.valueOf("STRING_NAME")` indirectly (e.g.,using JSON
deserialization) and the enum names get obfuscated.

- **Look for:** Unnecessary generic Enum keep rules in ProGuard files.
- **Example Code:**
  \`\`\`proguard

  # Unnecessary rule

  -keepclassmembers enum \* { \*; }
  \`\`\`
- **Suggested Keep Rule:**
  \*(Note: The default `proguard-android-optimize.txt` already contains the optimal
  rules for Enums (keeping `values()` and `valueOf(String)`). Any additional
  manual rules for Enums are redundant.) # No manual rule needed. Use default
  proguard-android-optimize.txt.

## references/android/topic/performance/app-optimization/enable-app-optimization.md

For the best user experience, you should optimize your app to make it as small
and fast as possible. Our app optimizer, called R8, streamlines your app by
removing unused code and resources, rewriting code to optimize runtime
performance, and more. To your users, this means:

- Faster startup time
- Reduced memory usage
- Improved rendering and runtime performance
- Fewer [ANRs](https://developer.android.com/topic/performance/anrs/keep-your-app-responsive)

> [!IMPORTANT]
> **Important:** You should always enable optimization for your app's release build; however, you probably don't want to enable it for tests or libraries. For more information about using R8 with tests, see [Test and troubleshoot the
> optimization](https://developer.android.com/topic/performance/app-optimization/test-and-troubleshoot-the-optimization). For more information about enabling R8 from libraries, see [Optimization for library authors](https://developer.android.com/topic/performance/app-optimization/library-optimization).

## R8 optimization overview

R8 uses a multi-phase process to optimize your app for size and speed. Key
operations include the following:

- **Code shrinking (also known as tree shaking)** : R8 identifies and removes
  unreachable code from your application and its library dependencies. By
  analyzing the entry points of your app (such as `Activities` or `Services`
  defined in the manifest), R8 builds a graph of referenced code and removes
  anything that remains unreferenced.

- **Logical optimizations**: R8 rewrites your code to improve execution
  efficiency and reduce overhead. Key techniques include:

  - **Method inlining**: R8 replaces a method call site with the actual body
    of the called method. This eliminates the overhead of a function call and
    lets R8 conduct further optimizations.

  - **Class merging**: R8 combines sets of classes and interfaces into a
    single class. This reduces the number of classes in the app, lowering
    memory pressure and improving startup speed.

- **Obfuscation (also known as minification)** : To reduce the size of the DEX
  file, R8 shortens the names of classes, fields, and methods (for example,
  `com.example.MyActivity` could become `a.b.a`).

Since 8.12.0 version of Android Gradle Plugin (AGP), R8 also optimizes resources
as part of its optimization phases. For more information, see [Optimized
resource shrinking](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization#optimize-resource-shrinking).

## Enable optimization

To enable app optimization, set `isMinifyEnabled = true` (for code optimization)
and `isShrinkResources = true` (for resource optimization) in your [release
build's](https://developer.android.com/studio/publish/preparing#turn-off-debugging) app-level build script as shown in the following code. We recommend
that you always enable both settings. We also recommend enabling app
optimization only in the final version of your app that you test before
publishing---usually your release build---because the optimizations increase the
build time of your project and can make debugging harder due to the way it
modifies code.

### Kotlin

```kotlin
android {
    buildTypes {
        release {

            // Enables code-related app optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true

            proguardFiles(
                // Default file with automatically generated optimization rules.
                getDefaultProguardFile("proguard-android-optimize.txt"),

                ...
            )
            ...
        }
    }
    ...
}
```

### Groovy

```groovy
android {
    buildTypes {
        release {

            // Enables code-related app optimization.
            minifyEnabled = true

            // Enables resource shrinking.
            shrinkResources = true

            // Default file with automatically generated optimization rules.
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')

            ...
        }
    }
}
```

## Optimize resource shrinking for even smaller apps

The 8.12.0 version of Android Gradle Plugin (AGP) introduces optimized resource
shrinking, which aims to integrate resource and code optimization to create even
smaller and faster apps.

Before optimized resource shrinking, Android Asset Packaging Tool (AAPT2)
generated keep rules that effectively treating resource shrinking separately
from code, often retaining inaccessible code or resources that referenced each
other.

With optimized resource shrinking, resources are considered like a part of
program code, forming the reference graph. When a collection of code or
resources is not referenced, it is not protected by a keep rule, and can be
removed.

### Enable optimized resource shrinking

To turn on the new optimized resource shrinking pipeline for a version of AGP
before 9.0.0, add the following to your project's `gradle.properties` file:

    android.r8.optimizedResourceShrinking=true

If you are using AGP 9.0.0 or a newer version, you don't need to set
`android.r8.optimizedResourceShrinking=true`. Optimized resource shrinking is
automatically applied when `isShrinkResources = true` is enabled in your build
configuration.

## Verify and configure R8 optimization settings

To enable R8 to use its [full optimization capabilities](https://developer.android.com/topic/performance/app-optimization/full-mode), remove the
following line from your project's `gradle.properties` file, if it exists:

    android.enableR8.fullMode=false # Remove this line from your codebase.

Note that enabling app optimization makes stack traces difficult to understand,
especially if R8 renames class or method names. To get stack traces that
correctly correspond to your source code, see [Recover the original stack
trace](https://developer.android.com/topic/performance/app-optimization/test-and-troubleshoot-the-optimization#recover-original-stack-trace).

If R8 is enabled, you should also [create Startup Profiles](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations) for even better
startup performance.

If you enable app optimization and it causes errors, here are some strategies to
fix them:

- [Add keep rules](https://developer.android.com/topic/performance/app-optimization/add-keep-rules) to keep some code untouched.
- [Adopt optimizations incrementally](https://developer.android.com/topic/performance/app-optimization/adopt-optimizations-incrementally).
- Update your code to [use libraries that are better suited for
  optimization](https://developer.android.com/topic/performance/app-optimization/choose-libraries-wisely).

> [!CAUTION]
> **Caution:** Tools that replace or modify R8's output can negatively impact runtime performance. R8 is careful about including and testing many optimizations at the code level, in [DEX layout](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations), and in correctly producing Baseline Profiles - other tools producing or modifying DEX files can break these optimizations, or otherwise regress performance.

If you are interested in optimizing your build speed, see [Configure how R8
runs](https://developer.android.com/build/r8-execution-profiles) for information on how to configure R8 based on your environment.

## AGP and R8 version behavior changes

The following table outlines the key features introduced in various versions of
the Android Gradle Plugin (AGP) and the R8 compiler.

| AGP version | Features introduced |
|---|---|
| 9.1 | **Classes repackaged by default:** R8 repackages classes (moving them to the unnamed package, at the top level) to compact DEX further, eliminating the need to specify `-repackageclasses` option. For information about how this works and how to opt out, see [global options](https://developer.android.com/topic/performance/app-optimization/global-options#global-options). |
| 9.0 | **Optimized resource shrinking:** Enabled by default (controlled using `android.r8.optimizedResourceShrinking`). [Optimized resource shrinking](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization#optimize-resource-shrinking) helps integrate resource shrinking with the code optimization pipeline, leading to smaller, faster apps. By optimizing both code and resource references simultaneously, it identifies and removes resources referenced exclusively from unused code. This is a significant improvement over the previous separate optimization processes. This is especially useful for apps that share substantial resources and code across different form factor verticals, with measured improvements of over 50% in app size. The resulting size reduction leads to smaller downloads, faster installations, and a better user experience with faster startup, improved rendering, and fewer ANRs. **Library rule filtering:** Support for global options (for example, `-dontobfuscate`) in library consumer rules has been dropped, and apps will filter them out. For more information, see [Add global options](https://developer.android.com/topic/performance/app-optimization/global-options). **Kotlin null checks:** Optimized by default (controlled using `-processkotlinnullchecks`). This version also introduced significant improvements in build speed. For more information, see [Global options for additional optimization](https://developer.android.com/topic/performance/app-optimization/global-options#global-options). **Optimize specific packages:** You can use `packageScope` to optimize specific packages. This is in experimental support. For more information, see [Optimize specified packages with `packageScope`](https://developer.android.com/topic/performance/app-optimization/optimize-specified-packages). **Optimized by default:** Support for `getDefaultProguardFile("proguard-android.txt")` has been dropped, because it includes `-dontoptimize`, which should be avoided. Instead, use `"proguard-android-optimize.txt"`. If you need to globally disable optimization in your app, [add the flag manually to a proguard file](https://developer.android.com/topic/performance/app-optimization/global-options#global-options-2). |
| 8.12 | **Resource shrinking:** Initial support added (Off by default. Enable using `isShrinkResources`). Resource shrinking works in tandem with R8 to identify and remove unused resources effectively. **Logcat retracing:** Support for automatic retracing in the Android Studio [Logcat window](https://developer.android.com/studio/debug/logcat). |
| 8.6 | **Improved retracing:** Includes filename and line number retracing by default for all `minSdk` levels (previously required `minSdk` 26+ in version 8.2). Updating R8 helps ensure that stack traces from obfuscated builds are readily and clearly readable. This version improves how line numbers and source files are mapped, making it easier for tools like the Android Studio Logcat to automatically retrace crashes to the original source code. |
| 8.0 | **Full mode by default:** [R8 full mode](https://developer.android.com/topic/performance/app-optimization/full-mode) provides significantly more powerful optimization. It is enabled by default. You can opt out using `android.enableR8.fullMode=false`. |
| 7.0 | **Full mode available:** Introduced as an opt-in feature using `android.enableR8.fullMode=true`. Full mode applies more powerful optimizations by making stricter assumptions about how your code uses reflection and other dynamic features. While it reduces app size and improves performance, it might require additional keep rules to prevent necessary code from being stripped. |

