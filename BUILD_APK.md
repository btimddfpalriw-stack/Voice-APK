# ساخت APK با یک دکمه

این پروژه برای GitHub Actions آماده شده است و نیازی به Android Studio ندارد.

## مراحل

1. یک Repository جدید در GitHub بساز.
2. محتویات این پوشه را داخل Repository آپلود کن.
3. وارد تب **Actions** شو.
4. از سمت چپ **Build APK** را انتخاب کن.
5. روی **Run workflow** و سپس دکمه سبز **Run workflow** بزن.
6. وقتی اجرا تمام شد، وارد همان اجرای موفق شو.
7. پایین صفحه در بخش **Artifacts** فایل `PersianVoiceAssistant-debug-apk.zip` را دانلود کن.
8. ZIP را باز کن و `app-debug.apk` را روی گوشی نصب کن.

### نکته
این Workflow یک APK تستی (Debug) می‌سازد که برای نصب و آزمایش مناسب است. برای انتشار در Google Play باید APK/AAB با کلید امضای مخصوص انتشار ساخته شود.
