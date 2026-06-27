package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.agon.app.BuildConfig
import com.agon.app.data.model.UserProfile
import com.agon.app.data.remote.GoogleSignInHelper
import com.agon.app.ui.theme.Accent
import com.agon.app.ui.theme.Accent40
import com.agon.app.ui.theme.BgBase
import com.agon.app.ui.theme.TextMuted
import com.agon.app.ui.theme.TextPrimary

@Composable
fun AuthScreen(
    onLogin: (UserProfile) -> Unit,
) {
    var isRegister by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0D14),
                        Color(0xFF1A1A2E),
                        Color(0xFF12121F),
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(24.dp),
        ) {
            if (isRegister) {
                RegisterForm(
                    onRegister = { onLogin(it) },
                    onSwitch = { isRegister = false },
                )
            } else {
                LoginForm(
                    onLogin = { onLogin(it) },
                    onSwitch = { isRegister = true },
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    onLogin: (UserProfile) -> Unit,
    onSwitch: () -> Unit,
) {
    var nick by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var showCode by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LogoHeader(title = "Вход в аккаунт", subtitle = "Войдите, чтобы использовать AI-ассистента")

        OutlinedTextField(
            value = nick,
            onValueChange = { nick = it; error = "" },
            label = { Text("Никнейм") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it; error = "" },
            label = { Text("Код доступа") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showCode) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            trailingIcon = {
                IconButton(onClick = { showCode = !showCode }) {
                    Icon(
                        imageVector = if (showCode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                    )
                }
            },
        )

        if (error.isNotBlank()) {
            Text(
                text = error,
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Запомнить меня", style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                if (nick.isBlank() || code.isBlank()) {
                    error = "Заполните все поля"
                } else {
                    onLogin(UserProfile(nick = nick, code = if (rememberMe) code else "", isAdmin = code == "007"))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
        ) {
            Text("Войти", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("или", modifier = Modifier.padding(horizontal = 8.dp), color = TextMuted)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(14.dp))

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val googleHelper = remember { GoogleSignInHelper(context as android.app.Activity) }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { onLogin(UserProfile(nick = "Guest", isAdmin = false)) },
                modifier = Modifier.weight(1f),
            ) {
                Text("Гость")
            }
            GoogleSignInButton(
                onClick = {
                    scope.launch {
                        val profile = googleHelper.signIn(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        if (profile != null) {
                            onLogin(
                                UserProfile(
                                    nick = profile.name,
                                    email = profile.email,
                                    avatarUrl = profile.picture,
                                    isAdmin = false,
                                    googleLogin = true,
                                )
                            )
                        } else {
                            error = "Ошибка входа через Google"
                        }
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onSwitch) {
            Text("Нет аккаунта? Зарегистрироваться", color = Accent)
        }
    }
}

@Composable
private fun RegisterForm(
    onRegister: (UserProfile) -> Unit,
    onSwitch: () -> Unit,
) {
    var nick by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var agree by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LogoHeader(title = "Регистрация", subtitle = "Создайте аккаунт для доступа к AI")

        OutlinedTextField(
            value = nick,
            onValueChange = { nick = it; error = "" },
            label = { Text("Никнейм *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it; error = "" },
            label = { Text("Код доступа *") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it; error = "" },
            label = { Text("Подтвердите код *") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = agree, onCheckedChange = { agree = it })
            Text("Я согласен с правилами использования", style = MaterialTheme.typography.bodyMedium)
        }

        if (error.isNotBlank()) {
            Text(
                text = error,
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Button(
            onClick = {
                when {
                    nick.isBlank() || code.isBlank() -> error = "Заполните все поля"
                    code.length < 3 -> error = "Код должен быть не менее 3 символов"
                    code != confirm -> error = "Коды не совпадают"
                    !agree -> error = "Примите условия использования"
                    else -> onRegister(UserProfile(nick = nick, code = code, isAdmin = false))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
        ) {
            Text("Зарегистрироваться", modifier = Modifier.padding(vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onSwitch) {
            Text("Уже есть аккаунт? Войти", color = Accent)
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
    ) {
        Text("Войти через Google", modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
private fun LogoHeader(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Accent)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text("NM", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Khirad", style = MaterialTheme.typography.titleLarge, color = Color(0xFF1A1A2E))
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold)
    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMuted, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun OutlinedButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent40),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
    ) {
        content()
    }
}
