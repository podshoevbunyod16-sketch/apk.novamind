package com.agon.app.data

import android.content.Context
import android.net.Uri
import com.agon.app.data.model.AdminLoginResponse
import com.agon.app.data.model.AdminSettingsRequest
import com.agon.app.data.model.AdminStatsResponse
import com.agon.app.data.model.AutoSearchResponse
import com.agon.app.data.model.ChatMessage
import com.agon.app.data.model.CommandResponse
import com.agon.app.data.model.MessageRole
import com.agon.app.data.model.ModelsListResponse
import com.agon.app.data.model.SendResponse
import com.agon.app.data.model.SwitchResponse
import com.agon.app.data.model.WebSearchResponse
import com.agon.app.data.remote.NovaMindApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class ChatRepository(context: Context) {

    private val api = NovaMindApi(context)

    suspend fun sendMessage(
        message: String,
        reasoning: Boolean,
        webSearch: Boolean,
        autoSearch: Boolean,
        serverUrl: String,
    ): ChatMessage = withContext(Dispatchers.IO) {
        if (message.startsWith("/")) {
            return@withContext handleCommand(message, serverUrl)
        }

        if (serverUrl.isBlank()) {
            return@withContext ChatMessage(MessageRole.AI, fallbackReply(message))
        }

        if (autoSearch) {
            val auto = api.autoSearch(message, serverUrl)
            if (auto.needs_search && auto.reply != null) {
                return@withContext ChatMessage(MessageRole.AI, auto.reply)
            }
            if (auto.error != null && serverUrl.isNotBlank()) {
                return@withContext ChatMessage(MessageRole.AI, "❌ Ошибка авто поиска: ${auto.error}")
            }
        }

        if (webSearch) {
            val resp = api.webSearch(message, serverUrl)
            val text = resp.reply ?: resp.error?.let { "❌ Ошибка: $it" }
            if (text != null) return@withContext ChatMessage(MessageRole.AI, text)
        }

        val resp = api.sendMessage(message, reasoning, serverUrl)
        val text = resp.reply ?: resp.error?.let { "❌ Ошибка: $it" } ?: fallbackReply(message)
        ChatMessage(MessageRole.AI, text)
    }

    suspend fun handleCommand(command: String, serverUrl: String): ChatMessage = withContext(Dispatchers.IO) {
        val parts = command.removePrefix("/").split(" ", limit = 2)
        val cmd = parts[0].lowercase()
        val args = parts.getOrNull(1) ?: ""

        when (cmd) {
            "clear" -> ChatMessage(MessageRole.AI, "🗑 История очищена")
            "history" -> ChatMessage(MessageRole.AI, "История сохраняется локально в приложении.")
            "image" -> {
                if (args.isBlank()) {
                    ChatMessage(MessageRole.AI, "❌ Укажите описание изображения: /image закат над морем")
                } else {
                    val encoded = URLEncoder.encode(args, "UTF-8")
                    val url = "https://image.pollinations.ai/prompt/$encoded?width=1024&height=1024&nologo=true"
                    ChatMessage(
                        MessageRole.AI,
                        content = "✅ Изображение сгенерировано:",
                        imageUrl = url,
                    )
                }
            }

            "search" -> {
                if (args.isBlank()) {
                    ChatMessage(MessageRole.AI, "❌ Укажите запрос: /search последние новости")
                } else {
                    val resp = api.webSearch(args, serverUrl)
                    val text = resp.reply ?: resp.error?.let { "❌ Ошибка: $it" }
                        ?: "🔍 Результаты по запросу \"$args\" недоступны в автономном режиме. Подключите сервер NovaMind в настройках."
                    ChatMessage(MessageRole.AI, text)
                }
            }

            "code" -> {
                if (args.isBlank()) {
                    ChatMessage(MessageRole.AI, "❌ Укажите, какой код создать: /code калькулятор на Python")
                } else {
                    val resp = api.sendCommand(command, serverUrl)
                    val text = resp.result ?: resp.reply ?: resp.error?.let { "❌ Ошибка: $it" }
                        ?: fallbackCode(args)
                    ChatMessage(MessageRole.AI, text)
                }
            }

            "research" -> {
                if (args.isBlank()) {
                    ChatMessage(MessageRole.AI, "❌ Укажите вопрос: /research как работает нейросеть")
                } else {
                    val resp = api.sendCommand(command, serverUrl)
                    val text = resp.result ?: resp.reply ?: resp.error?.let { "❌ Ошибка: $it" }
                        ?: "🔍 Глубокое исследование \"$args\" недоступно в автономном режиме."
                    ChatMessage(MessageRole.AI, text)
                }
            }

            else -> {
                val resp = api.sendCommand(command, serverUrl)
                val text = resp.result ?: resp.reply ?: resp.error?.let { "❌ Ошибка: $it" }
                    ?: "Команда /$cmd выполнена (автономный режим)."
                ChatMessage(MessageRole.AI, text)
            }
        }
    }

    suspend fun uploadImage(uri: Uri, description: String, serverUrl: String): ChatMessage =
        withContext(Dispatchers.IO) {
            val resp = api.uploadImage(uri, description, serverUrl)
            val text = resp.result ?: resp.error?.let { "❌ Ошибка: $it" }
                ?: "⚠️ Изображение получено, но анализ недоступен."
            ChatMessage(MessageRole.AI, text)
        }

    suspend fun uploadDocument(uri: Uri, description: String, serverUrl: String): ChatMessage =
        withContext(Dispatchers.IO) {
            val resp = api.uploadDocument(uri, description, serverUrl)
            val text = resp.result ?: resp.error?.let { "❌ Ошибка: $it" }
                ?: "⚠️ Файл получен, но анализ недоступен."
            ChatMessage(MessageRole.AI, text)
        }

    suspend fun listModels(serverUrl: String): ModelsListResponse =
        withContext(Dispatchers.IO) { api.listModels(serverUrl) }

    suspend fun switchModel(modelId: String, serverUrl: String): SwitchResponse =
        withContext(Dispatchers.IO) { api.switchModel(modelId, serverUrl) }

    suspend fun adminLogin(code: String, serverUrl: String): AdminLoginResponse =
        withContext(Dispatchers.IO) { api.adminLogin(code, serverUrl) }

    suspend fun adminStats(serverUrl: String): AdminStatsResponse =
        withContext(Dispatchers.IO) { api.adminStats(serverUrl) }

    suspend fun adminSaveSettings(settings: AdminSettingsRequest, serverUrl: String): Boolean =
        withContext(Dispatchers.IO) { api.adminSaveSettings(settings, serverUrl) }

    private fun fallbackReply(message: String): String {
        return when {
            message.lowercase().contains("привет") || message.lowercase().contains("hello") ->
                "Привет! Я Khirad (NovaMind). Сервер не подключён, но я могу отвечать в демо-режиме."

            message.lowercase().contains("погода") ->
                "🌤 Чтобы узнать погоду, используйте боковое меню **Погода** или введите `/services weather город`."

            message.lowercase().contains("курс") || message.lowercase().contains("валют") ->
                "💱 Для курса валют используйте `/services currency USD RUB`."

            else -> "🤖 Сервер NovaMind не подключён. Настройте URL в разделе **Настройки** или используйте команды `/image`, `/code`, `/clear`."
        }
    }

    private fun fallbackCode(request: String): String {
        return """
            |```python
            |# Пример кода по запросу: $request
            |def example():
            |    print("Hello from Khirad!")
            |    return 42
            |
            |if __name__ == "__main__":
            |    example()
            |```
            |
            |Подключите сервер NovaMind для генерации production-ready кода.
        """.trimMargin()
    }
}
