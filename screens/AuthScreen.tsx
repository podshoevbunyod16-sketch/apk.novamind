import React, { useState } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView,
  Platform, ScrollView, Alert,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { useApp } from '../context/AppContext';
import { colors } from '../theme/colors';

export default function AuthScreen() {
  const { setUser } = useApp();
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [nick, setNick] = useState('');
  const [code, setCode] = useState('');
  const [confirmCode, setConfirmCode] = useState('');
  const [agree, setAgree] = useState(false);
  const [showCode, setShowCode] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = () => {
    setError('');
    if (!nick.trim() || !code.trim()) {
      setError('Заполните все поля');
      return;
    }
    setUser({ nick: nick.trim(), email: '', avatar: '', isAdmin: false });
  };

  const handleRegister = () => {
    setError('');
    if (!nick.trim() || !code.trim()) {
      setError('Заполните все поля');
      return;
    }
    if (code.length < 3) {
      setError('Код должен быть не менее 3 символов');
      return;
    }
    if (code !== confirmCode) {
      setError('Коды не совпадают');
      return;
    }
    if (!agree) {
      setError('Примите условия использования');
      return;
    }
    setUser({ nick: nick.trim(), email: '', avatar: '', isAdmin: false });
  };

  const quickLogin = (type: string) => {
    setUser({ nick: `${type}_User`, email: '', avatar: '', isAdmin: false });
  };

  return (
    <LinearGradient colors={['#0d0d14', '#1a1a2e', '#12121f']} style={styles.gradient}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.flex}>
        <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
          <View style={styles.card}>
            {mode === 'login' ? (
              <View>
                <View style={styles.header}>
                  <View style={styles.logoRow}>
                    <View style={styles.logoBadge}><Text style={styles.logoBadgeText}>NM</Text></View>
                    <Text style={styles.logoText}>Khirad</Text>
                  </View>
                  <Text style={styles.title}>Вход в аккаунт</Text>
                  <Text style={styles.subtitle}>Войдите, чтобы использовать AI-ассистента</Text>
                </View>

                <View style={styles.group}>
                  <Text style={styles.label}>Никнейм</Text>
                  <TextInput
                    style={styles.input}
                    placeholder="Введите никнейм"
                    placeholderTextColor={colors.textMuted}
                    value={nick}
                    onChangeText={setNick}
                    autoCapitalize="none"
                  />
                </View>

                <View style={styles.group}>
                  <Text style={styles.label}>Код доступа</Text>
                  <View style={styles.inputWrap}>
                    <TextInput
                      style={[styles.input, styles.inputWithIcon]}
                      placeholder="Введите код"
                      placeholderTextColor={colors.textMuted}
                      value={code}
                      onChangeText={setCode}
                      secureTextEntry={!showCode}
                    />
                    <TouchableOpacity style={styles.eyeBtn} onPress={() => setShowCode(!showCode)}>
                      <Text style={styles.eyeText}>{showCode ? '🙈' : '🙀'}</Text>
                    </TouchableOpacity>
                  </View>
                </View>

                <TouchableOpacity style={styles.submit} onPress={handleLogin} activeOpacity={0.85}>
                  <Text style={styles.submitText}>Войти</Text>
                </TouchableOpacity>

                {error ? <Text style={styles.error}>{error}</Text> : null}

                <View style={styles.divider}>
                  <View style={styles.dividerLine} />
                  <Text style={styles.dividerText}>или</Text>
                  <View style={styles.dividerLine} />
                </View>

                <View style={styles.socialRow}>
                  <TouchableOpacity style={styles.socialBtn} onPress={() => quickLogin('Guest')} activeOpacity={0.8}>
                    <Text style={styles.socialIcon}>👤</Text>
                    <Text style={styles.socialText}>Гость</Text>
                  </TouchableOpacity>
                </View>

                <View style={styles.footer}>
                  <Text style={styles.footerText}>Нет аккаунта? </Text>
                  <TouchableOpacity onPress={() => { setMode('register'); setError(''); }}>
                    <Text style={styles.footerLink}>Зарегистрироваться</Text>
                  </TouchableOpacity>
                </View>
              </View>
            ) : (
              <View>
                <View style={styles.header}>
                  <View style={styles.logoRow}>
                    <View style={styles.logoBadge}><Text style={styles.logoBadgeText}>NM</Text></View>
                    <Text style={styles.logoText}>Khirad</Text>
                  </View>
                  <Text style={styles.title}>Регистрация</Text>
                  <Text style={styles.subtitle}>Создайте аккаунт для доступа к AI</Text>
                </View>

                <View style={styles.group}>
                  <Text style={styles.label}>Никнейм *</Text>
                  <TextInput
                    style={styles.input}
                    placeholder="Ваш никнейм"
                    placeholderTextColor={colors.textMuted}
                    value={nick}
                    onChangeText={setNick}
                    autoCapitalize="none"
                  />
                </View>

                <View style={styles.group}>
                  <Text style={styles.label}>Код доступа *</Text>
                  <TextInput
                    style={styles.input}
                    placeholder="Придумайте код (минимум 3 символа)"
                    placeholderTextColor={colors.textMuted}
                    value={code}
                    onChangeText={setCode}
                    secureTextEntry
                  />
                </View>

                <View style={styles.group}>
                  <Text style={styles.label}>Подтвердите код *</Text>
                  <TextInput
                    style={styles.input}
                    placeholder="Повторите код"
                    placeholderTextColor={colors.textMuted}
                    value={confirmCode}
                    onChangeText={setConfirmCode}
                    secureTextEntry
                  />
                </View>

                <TouchableOpacity style={styles.checkboxRow} onPress={() => setAgree(!agree)} activeOpacity={0.8}>
                  <View style={[styles.checkbox, agree && styles.checkboxActive]}>
                    {agree && <Text style={styles.checkmark}>✓</Text>}
                  </View>
                  <Text style={styles.checkboxText}>Я согласен с правилами использования</Text>
                </TouchableOpacity>

                <TouchableOpacity style={styles.submit} onPress={handleRegister} activeOpacity={0.85}>
                  <Text style={styles.submitText}>Зарегистрироваться</Text>
                </TouchableOpacity>

                {error ? <Text style={styles.error}>{error}</Text> : null}

                <View style={styles.footer}>
                  <Text style={styles.footerText}>Уже есть аккаунт? </Text>
                  <TouchableOpacity onPress={() => { setMode('login'); setError(''); }}>
                    <Text style={styles.footerLink}>Войти</Text>
                  </TouchableOpacity>
                </View>
              </View>
            )}
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  gradient: { flex: 1 },
  flex: { flex: 1 },
  scroll: { flexGrow: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  card: {
    backgroundColor: '#fff',
    borderRadius: 20,
    width: '100%',
    maxWidth: 440,
    paddingHorizontal: 28,
    paddingVertical: 30,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 20 },
    shadowOpacity: 0.3,
    shadowRadius: 60,
    elevation: 10,
  },
  header: { alignItems: 'center', marginBottom: 24 },
  logoRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 16 },
  logoBadge: {
    backgroundColor: colors.accent,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  logoBadgeText: { color: '#fff', fontSize: 14, fontWeight: '900' },
  logoText: { fontSize: 18, fontWeight: '800', color: '#1a1a2e' },
  title: { fontSize: 22, fontWeight: '800', color: '#1a1a2e', marginBottom: 6 },
  subtitle: { fontSize: 13, color: '#999', textAlign: 'center' },
  group: { marginBottom: 14 },
  label: { fontSize: 13, fontWeight: '600', color: '#333', marginBottom: 6 },
  input: {
    width: '100%',
    paddingHorizontal: 14,
    paddingVertical: 12,
    borderWidth: 2,
    borderColor: '#e0e0e0',
    borderRadius: 10,
    fontSize: 15,
    color: '#1a1a2e',
    backgroundColor: '#fff',
  },
  inputWithIcon: { paddingRight: 44 },
  inputWrap: { position: 'relative' },
  eyeBtn: { position: 'absolute', right: 10, top: 10, padding: 6 },
  eyeText: { fontSize: 16 },
  submit: {
    width: '100%',
    paddingVertical: 14,
    backgroundColor: colors.accent,
    borderRadius: 10,
    alignItems: 'center',
    marginTop: 8,
  },
  submitText: { color: '#fff', fontSize: 16, fontWeight: '700' },
  error: { color: '#ef4444', fontSize: 12, textAlign: 'center', marginTop: 8, minHeight: 18 },
  divider: { flexDirection: 'row', alignItems: 'center', marginVertical: 16 },
  dividerLine: { flex: 1, height: 1, backgroundColor: '#e0e0e0' },
  dividerText: { color: '#ccc', fontSize: 13, marginHorizontal: 10 },
  socialRow: { flexDirection: 'row', gap: 10 },
  socialBtn: {
    flex: 1,
    paddingVertical: 10,
    borderWidth: 2,
    borderColor: '#e0e0e0',
    borderRadius: 10,
    backgroundColor: '#fff',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
  },
  socialIcon: { fontSize: 16 },
  socialText: { fontSize: 13, fontWeight: '600', color: '#333' },
  footer: { flexDirection: 'row', justifyContent: 'center', marginTop: 16 },
  footerText: { fontSize: 13, color: '#999' },
  footerLink: { fontSize: 13, color: colors.accent, fontWeight: '600' },
  checkboxRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 16 },
  checkbox: {
    width: 18,
    height: 18,
    borderWidth: 2,
    borderColor: '#ccc',
    borderRadius: 4,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkboxActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  checkmark: { color: '#fff', fontSize: 12, fontWeight: '700' },
  checkboxText: { fontSize: 13, color: '#333' },
});
