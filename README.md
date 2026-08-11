# wMetax ai v1 — Android prototype

نسخه اولیه رابط کاربری wMetax ai با Kotlin + Jetpack Compose.

## وضعیت
- Android / Dark-only
- نام رسمی: `wMetax ai`
- انتخاب AI: Auto + Manual
- مدل‌ها: ChatGPT, Claude, Grok, DeepSeek, Perplexity, Gemini
- Compare: معماری آن در نسخه بعدی وصل می‌شود
- فایل / Web Research / Auth: Backend integration بعدی
- محل تبلیغ: زیر selector مدل، به‌صورت placeholder

## Backend
پروژه Supabase فعلی برای wMetax با جدول‌های جداگانه `wmetax_*` آماده شده است.
Owner:
`farrokhzad743@gmail.com`

## نکته مدل‌ها
برچسب نسخه‌ها در UI باید از Backend/Provider metadata به‌روزرسانی شود و نباید hard-code دائمی باشد.

## ساخت
Android Studio را باز کنید، پوشه پروژه را Import کنید و Gradle Sync را انجام دهید.
اگر Gradle Wrapper ندارید، Android Studio خودش از Gradle سازگار استفاده می‌کند.

## قدم بعدی
1. اتصال Supabase Auth (Email + Google)
2. اتصال پروفایل و نقش Owner/Member
3. Edge Function برای AI Gateway
4. Web Search / Research
5. File upload + extraction/RAG
6. Streaming responses
7. Compare سه‌مدلی
8. Ad provider قابل‌تعویض
