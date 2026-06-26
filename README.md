# NovaMind Full — Автономное Android приложение

Вся программа NovaMind работает **внутри APK**! Python сервер Flask запускается прямо в приложении через Chaquopy.

## Как это работает

```
┌─────────────────────────────────────────┐
│           NovaMind APK                  │
│  ┌─────────────────────────────────┐    │
│  │      Android (Java/Kotlin)      │    │
│  │      - WebView UI               │    │
│  │      - File picker              │    │
│  └─────────────────────────────────┘    │
│                    │                     │
│  ┌─────────────────────────────────┐    │
│  │      Python (Chaquopy)          │    │
│  │      - Flask сервер             │    │
│  │      - AI API запросы           │    │
│  │      - История чатов            │    │
│  └─────────────────────────────────┘    │
│                    │                     │
│  ┌─────────────────────────────────┐    │
│  │      Интернет (Groq API)      │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

## Структура проекта

```
NovaMind-Full-Android/
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/novamind/app/
│   │   │   │   └── MainActivity.java      # Запускает Python + WebView
│   │   │   ├── python/
│   │   │   │   └── novamind_server.py     # Flask сервер (Python)
│   │   │   ├── res/                       # Android ресурсы
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle                   # Chaquopy конфигурация
│   ├── build.gradle
│   └── settings.gradle
├── .github/workflows/
│   └── build-apk.yml                      # Автосборка в GitHub
└── README.md
```

## Сборка через GitHub Actions

1. Загрузи все файлы в свой репозиторий
2. Перейди в **Actions** → **Build NovaMind Full APK**
3. Нажми **Run workflow**
4. Через 10–15 минут APK появится в **Releases**

## Ручная сборка (локально)

### Требования:
- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17
- Python 3.11 (для Chaquopy)

### Шаги:
```bash
cd android
./gradlew assembleRelease
```

APK будет в: `app/build/outputs/apk/release/`

## Настройка API ключа

Для работы с AI нужен API ключ:

1. Зарегистрируйся на [console.groq.com](https://console.groq.com)
2. Получи бесплатный API ключ
3. Добавь ключ в приложение (настройки → API ключ)

**Без ключа** приложение работает в демо-режиме с ограниченным функционалом.

## Возможности

- ✅ AI чат через Groq API
- ✅ Генерация кода (`/code`)
- ✅ Генерация изображений (`/image` через Pollinations)
- ✅ Поиск в интернете (`/search`)
- ✅ История чатов
- ✅ Тёмная тема
- ✅ Полностью автономно — не нужен отдельный сервер

## Поддерживаемые провайдеры

| Провайдер | Модели |
|-----------|--------|
| Groq | Llama 3.3 70B, Llama 3.1 8B |
| Cerebras | Qwen 3 235B, DeepSeek R1 |
| OpenRouter | Gemini 2.0 Flash |

## Лицензия

MIT — используй свободно.
