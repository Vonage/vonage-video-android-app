# Strip VonageLogger calls (and their argument-building code) in minified builds via
# -assumenosideeffects. Also targets the $default bridge methods Kotlin generates for
# default parameters, since call sites invoke those, not the instance methods directly.
-assumenosideeffects class com.vonage.logger.VonageLogger {
    public void v(java.lang.String, java.lang.String, java.lang.Throwable);
    public static void v$default(com.vonage.logger.VonageLogger, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
    public void d(java.lang.String, java.lang.String, java.lang.Throwable);
    public static void d$default(com.vonage.logger.VonageLogger, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
    public void i(java.lang.String, java.lang.String, java.lang.Throwable);
    public static void i$default(com.vonage.logger.VonageLogger, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
    public void w(java.lang.String, java.lang.String, java.lang.Throwable);
    public static void w$default(com.vonage.logger.VonageLogger, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
    public void e(java.lang.String, java.lang.String, java.lang.Throwable);
    public static void e$default(com.vonage.logger.VonageLogger, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
    public void log(com.vonage.logger.LogLevel, java.lang.String, java.lang.String, java.lang.Throwable);
    public static void log$default(com.vonage.logger.VonageLogger, com.vonage.logger.LogLevel, java.lang.String, java.lang.String, java.lang.Throwable, int, java.lang.Object);
}
