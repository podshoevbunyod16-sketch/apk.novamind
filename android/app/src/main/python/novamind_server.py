import os
import sys
import json
import threading
import time
from datetime import datetime

# Add the app directory to path
sys.path.insert(0, os.path.dirname(__file__))

try:
    from flask import Flask, request, jsonify, render_template_string
    import requests
except ImportError as e:
    print(f"Import error: {e}")

# Global variables
server_thread = None
app_instance = None
server_running = False
contents = []

# System prompt
system_prompt = """Ты — продвинутый AI ассистент NovaMind.

Твоя цель:
- Давать максимально полезные, конкретные и применимые ответы
- Минимизировать воду и общие фразы
- Работать как эксперт

Правила:
1. Структура: краткий вывод → основная часть → пример
2. Если задача не уточнена — предложи варианты
3. Код → рабочий, современный, без мусора
4. Всегда думай как инженер, отвечай как эксперт
5. Никогда не пиши общие фразы, не дублируй очевидное"""

# Providers config
PROVIDERS = {
    "groq": {
        "url": "https://api.groq.com/openai/v1/chat/completions",
        "models": [
            {"id": "llama-3.3-70b-versatile", "name": "Llama 3.3 70B"},
            {"id": "llama-3.1-8b-instant", "name": "Llama 3.1 8B"},
        ]
    },
    "cerebras": {
        "url": "https://api.cerebras.ai/v1/chat/completions",
        "models": [
            {"id": "qwen-3-235b-a22b-instruct-2507", "name": "Qwen 3 235B"},
            {"id": "deepseek-r1-distill-llama-70b", "name": "DeepSeek R1 Distill"}
        ]
    },
    "openrouter": {
        "url": "https://openrouter.ai/api/v1/chat/completions",
        "models": [
            {"id": "google/gemini-2.0-flash-001", "name": "Gemini 2.0 Flash"},
        ]
    }
}

current_provider = "groq"
current_model = "llama-3.3-70b-versatile"

# HTML Template for the chat interface
HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NovaMind AI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: #0a0a0a;
            color: #e0e0e0;
            height: 100vh;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }
        .header {
            background: #1a1a2e;
            padding: 12px 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid #2a2a3e;
        }
        .header h1 { font-size: 18px; color: #00d4aa; }
        .header .status { font-size: 12px; color: #00d4aa; }
        .chat-container {
            flex: 1;
            overflow-y: auto;
            padding: 16px;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        .message {
            max-width: 85%;
            padding: 12px 16px;
            border-radius: 16px;
            font-size: 14px;
            line-height: 1.5;
            word-wrap: break-word;
        }
        .message.user {
            align-self: flex-end;
            background: #0066cc;
            color: white;
            border-bottom-right-radius: 4px;
        }
        .message.assistant {
            align-self: flex-start;
            background: #1a1a2e;
            color: #e0e0e0;
            border-bottom-left-radius: 4px;
        }
        .message.assistant pre {
            background: #0a0a0a;
            padding: 8px;
            border-radius: 8px;
            overflow-x: auto;
            margin: 8px 0;
        }
        .message.assistant code {
            font-family: 'Courier New', monospace;
            font-size: 12px;
        }
        .input-area {
            background: #1a1a2e;
            padding: 12px 16px;
            display: flex;
            gap: 8px;
            border-top: 1px solid #2a2a3e;
        }
        .input-area input {
            flex: 1;
            background: #0a0a0a;
            border: 1px solid #2a2a3e;
            border-radius: 20px;
            padding: 10px 16px;
            color: #e0e0e0;
            font-size: 14px;
            outline: none;
        }
        .input-area input::placeholder { color: #666; }
        .input-area button {
            background: #00d4aa;
            border: none;
            border-radius: 20px;
            padding: 10px 20px;
            color: #0a0a0a;
            font-weight: bold;
            cursor: pointer;
        }
        .input-area button:disabled { background: #2a2a3e; color: #666; }
        .typing { color: #888; font-style: italic; }
        .error { color: #ff4444; }
        .commands {
            display: flex;
            gap: 6px;
            padding: 8px 16px;
            overflow-x: auto;
            background: #0a0a0a;
            border-bottom: 1px solid #1a1a2e;
        }
        .commands button {
            background: #1a1a2e;
            border: 1px solid #2a2a3e;
            border-radius: 16px;
            padding: 6px 12px;
            color: #00d4aa;
            font-size: 12px;
            white-space: nowrap;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🧠 NovaMind</h1>
        <span class="status" id="status">● Онлайн</span>
    </div>
    <div class="commands">
        <button onclick="insertCmd('/code ')">💻 Код</button>
        <button onclick="insertCmd('/image ')">🖼️ Картинка</button>
        <button onclick="insertCmd('/search ')">🔍 Поиск</button>
        <button onclick="insertCmd('/clear')">🗑️ Очистить</button>
    </div>
    <div class="chat-container" id="chat"></div>
    <div class="input-area">
        <input type="text" id="messageInput" placeholder="Напишите сообщение..." onkeypress="if(event.key==='Enter')sendMessage()">
        <button id="sendBtn" onclick="sendMessage()">Отправить</button>
    </div>

    <script>
        const chat = document.getElementById('chat');
        const input = document.getElementById('messageInput');
        const sendBtn = document.getElementById('sendBtn');
        let isTyping = false;

        function insertCmd(cmd) {
            input.value = cmd;
            input.focus();
        }

        function addMessage(text, isUser) {
            const div = document.createElement('div');
            div.className = 'message ' + (isUser ? 'user' : 'assistant');
            div.innerHTML = text;
            chat.appendChild(div);
            chat.scrollTop = chat.scrollHeight;
        }

        function addTyping() {
            const div = document.createElement('div');
            div.className = 'message assistant typing';
            div.id = 'typing';
            div.textContent = 'NovaMind пишет...';
            chat.appendChild(div);
            chat.scrollTop = chat.scrollHeight;
        }

        function removeTyping() {
            const t = document.getElementById('typing');
            if (t) t.remove();
        }

        async function sendMessage() {
            const msg = input.value.trim();
            if (!msg || isTyping) return;

            addMessage(msg, true);
            input.value = '';
            isTyping = true;
            sendBtn.disabled = true;
            addTyping();

            try {
                const res = await fetch('/send', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ message: msg })
                });
                const data = await res.json();
                removeTyping();
                if (data.error) {
                    addMessage('<span class="error">❌ ' + data.error + '</span>', false);
                } else {
                    addMessage(data.reply, false);
                }
            } catch (e) {
                removeTyping();
                addMessage('<span class="error">❌ Ошибка соединения</span>', false);
            }

            isTyping = false;
            sendBtn.disabled = false;
        }

        // Welcome message
        addMessage('👋 Привет! Я NovaMind — твой AI-ассистент. Задай мне любой вопрос или используй команды сверху.', false);
    </script>
</body>
</html>
"""

def create_app(data_dir):
    """Create Flask app instance"""
    global app_instance

    app = Flask(__name__)
    app.secret_key = "nova-secret-key"

    # Create necessary directories
    for d in ["uploads", "generated_images", "saved_codes"]:
        os.makedirs(os.path.join(data_dir, d), exist_ok=True)

    @app.route('/')
    def index():
        return render_template_string(HTML_TEMPLATE)

    @app.route('/send', methods=['POST'])
    def send():
        global contents
        data = request.get_json() or {}
        message = data.get('message', '').strip()

        if not message:
            return jsonify({'error': 'Пустое сообщение'})

        contents.append({"role": "user", "content": message})

        # Handle commands
        if message.startswith('/'):
            return handle_command(message)

        # Regular chat
        return handle_chat(message)

    def handle_chat(message):
        global contents

        # Get API key from environment or use demo mode
        api_key = os.environ.get("GROQ_API_KEY", "")

        if not api_key:
            reply = """⚠️ **API ключ не настроен**

Для работы с AI нужно добавить API ключ:

1. Получи ключ на [console.groq.com](https://console.groq.com)
2. Добавь его в настройки приложения

**Без ключа доступны только демо-ответы.**"""
            contents.append({"role": "assistant", "content": reply})
            return jsonify({'reply': reply})

        try:
            provider = PROVIDERS.get(current_provider, PROVIDERS["groq"])
            payload = {
                "model": current_model,
                "messages": [{"role": "system", "content": system_prompt}] + contents,
                "temperature": 0.7,
                "max_tokens": 4000,
            }

            headers = {
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json"
            }

            resp = requests.post(provider["url"], json=payload, headers=headers, timeout=60)
            resp.raise_for_status()

            reply = resp.json()["choices"][0]["message"]["content"]
            contents.append({"role": "assistant", "content": reply})

            if len(contents) > 20:
                contents = contents[-20:]

            return jsonify({'reply': reply})

        except Exception as e:
            error_msg = f"Ошибка: {str(e)}"
            return jsonify({'error': error_msg})

    def handle_command(cmd_line):
        global contents
        parts = cmd_line[1:].split(maxsplit=1)
        cmd = parts[0].lower()
        args = parts[1] if len(parts) > 1 else ""

        if cmd == "clear":
            contents.clear()
            return jsonify({'reply': '✅ История очищена'})

        if cmd == "history":
            if not contents:
                return jsonify({'reply': 'История пуста'})
            hist = "\n\n".join([f"**{msg['role']}**: {msg['content'][:200]}..." for msg in contents])
            return jsonify({'reply': hist})

        if cmd == "code":
            if not args:
                return jsonify({'reply': '⚠️ Укажите, какой код создать. Пример: /code калькулятор на Python'})

            api_key = os.environ.get("GROQ_API_KEY", "")
            if not api_key:
                return jsonify({'reply': '⚠️ Для генерации кода нужен API ключ'})

            try:
                payload = {
                    "model": current_model,
                    "messages": [
                        {"role": "system", "content": "Ты программист. Пиши чистый, production-ready код с комментариями."},
                        {"role": "user", "content": f"Напиши код: {args}"}
                    ],
                    "temperature": 0.3,
                    "max_tokens": 3000,
                }
                headers = {
                    "Authorization": f"Bearer {api_key}",
                    "Content-Type": "application/json"
                }
                resp = requests.post(PROVIDERS["groq"]["url"], json=payload, headers=headers, timeout=60)
                resp.raise_for_status()
                reply = resp.json()["choices"][0]["message"]["content"]
                return jsonify({'reply': f"💻 **Сгенерированный код:**\n\n{reply}"})
            except Exception as e:
                return jsonify({'error': str(e)})

        if cmd == "image":
            if not args:
                return jsonify({'reply': '⚠️ Укажите описание изображения. Пример: /image красивый закат'})

            try:
                import urllib.parse
                encoded = urllib.parse.quote(args)
                img_url = f"https://image.pollinations.ai/prompt/{encoded}?width=1024&height=1024&nologo=true"

                return jsonify({
                    'reply': f'🖼️ **Изображение:**\n\n![Image]({img_url})\n\n*Генерация через Pollinations.ai*'
                })
            except Exception as e:
                return jsonify({'error': str(e)})

        if cmd == "search":
            if not args:
                return jsonify({'reply': '⚠️ Укажите поисковый запрос. Пример: /search погода в Москве'})

            return jsonify({
                'reply': f'🔍 **Поиск по запросу "{args}":**\n\nПоиск в интернете требует настройки APILayer API ключа.\n\nРезультаты будут доступны после добавления ключа в настройки.'
            })

        return jsonify({'reply': f'❓ Неизвестная команда: /{cmd}. Доступные: /code, /image, /search, /clear, /history'})

    @app.route('/models_list')
    def models_list():
        providers_list = []
        for key, data in PROVIDERS.items():
            providers_list.append({"provider": key, "list": data["models"]})
        return jsonify({"models": providers_list, "current": current_model})

    @app.route('/api/admin/stats')
    def admin_stats():
        return jsonify({
            "models": PROVIDERS,
            "current_provider": current_provider,
            "current_model": current_model,
            "history_messages": len(contents)
        })

    app_instance = app
    return app

def start_server(data_dir):
    """Start Flask server in background thread"""
    global server_thread, server_running

    if server_running:
        return

    app = create_app(data_dir)
    server_running = True

    def run():
        try:
            print("Starting NovaMind server on port 5000...")
            app.run(host='127.0.0.1', port=5000, debug=False, threaded=True)
        except Exception as e:
            print(f"Server error: {e}")
            server_running = False

    server_thread = threading.Thread(target=run, daemon=True)
    server_thread.start()
    print("Server thread started")

def stop_server():
    """Stop the server"""
    global server_running
    server_running = False
    print("Server stopped")
