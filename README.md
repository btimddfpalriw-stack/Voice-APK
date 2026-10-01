# دستیار صوتی فارسی حرفه‌ای — نسخه آماده ساخت APK

این پروژه برای این طراحی شده که با کمترین دردسر در Android Studio باز شود و APK خروجی بگیرد.

## ساخت APK با Android Studio

1. Android Studio را نصب کن.
2. همین پوشه `PersianVoiceAssistantAndroidPro` را در Android Studio با گزینه **Open** باز کن.
3. اگر Android Studio درخواست نصب SDK یا Build Tools داد، گزینه **Install/Accept** را بزن.
4. صبر کن تا **Gradle Sync** کامل شود.
5. از منوی **Build → Build APK(s)** استفاده کن.
6. فایل ساخته‌شده معمولاً اینجاست:

`app/build/outputs/apk/debug/app-debug.apk`

7. فایل APK را به گوشی منتقل کن و نصب کن.

## اجرای مستقیم روی گوشی از Android Studio

اگر گوشی را با USB وصل کنی و **USB debugging** را فعال کنی، می‌توانی دکمه ▶ Run را بزنی و برنامه مستقیم روی گوشی نصب و اجرا شود؛ در این حالت نیازی به ساخت دستی APK نداری.

## مجوزها

بار اول روی **فعال‌کردن دستیار** بزن و اجازه میکروفون را بده. در Android 13 به بعد، اجازه اعلان هم ممکن است نمایش داده شود.

## قابلیت‌ها

- فرمان صوتی فارسی با کلمه بیدارباش «دستیار»
- باز کردن Google، YouTube، Chrome، Telegram، Discord، Downloads و Calculator
- جستجو در Google و YouTube
- سرویس foreground برای ادامه کار در پس‌زمینه
- پاسخ صوتی فارسی با Text-to-Speech

## محدودیت Android

یک برنامه عادی Android اجازه force-stop کردن برنامه‌های دیگر را ندارد؛ بنابراین فرمان‌هایی مثل «تلگرام رو ببند» فقط می‌توانند محدودیت Android را اعلام کنند، نه اینکه برنامه را اجباری ببندند.

## نکته مهم

برای ساخت APK، Android Studio باید SDK و Gradle را دریافت/تنظیم کند. بعد از یک بار Sync، دفعات بعد ساخت پروژه خیلی ساده‌تر خواهد بود.
