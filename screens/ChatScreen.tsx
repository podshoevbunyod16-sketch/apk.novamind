import React, { useState, useRef, useCallback, useEffect } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, FlatList, StyleSheet,
  KeyboardAvoidingView, Platform, SafeAreaView, StatusBar,
} from 'react-native';
import { useApp, TOOLS } from '../context/AppContext';
import { colors } from '../theme/colors';
import ChatMessage from '../components/ChatMessage';
import TypingIndicator from '../components/TypingIndicator';
import SuggestionCard from '../components/SuggestionCard';
import ModelSelectorModal from '../components/ModelSelectorModal';

const SUGGESTIONS = [
  { icon: '🌤️', title: 'Погода', desc: 'Узнайте погоду в любом городе', mode: TOOLS[3] },
  { icon: '💻', title: 'Напиши код', desc: 'Сгенерировать код на любом языке', mode: TOOLS[2] },
  { icon: '💱', title: 'Курс валют', desc: 'Актуальные курсы валют', mode: TOOLS[4] },
  { icon: '🔍', title: 'Поиск в интернете', desc: 'Найти информацию через DuckDuckGo', mode: TOOLS[0] },
];

const AI_RESPONSES: Record<string, string> = {
  default: 'Привет! Я ваш интеллектуальный AI-ассистент. Чем могу помочь?',
  weather: 'Погода сегодня отличная! ☀️ Температура около 22°C, ясно, без осадков. Ветер северо-западный 3-5 м/с.',
  code: '```python\ndef hello_world():\n    print("Hello, World!")\n    return "Успех!"\n\nhello_world()\n```\n\nВот простой пример кода на Python. Если нужен другой язык или более сложный пример, скажите!',
  currency: 'Актуальные курсы валют:\n\n- **USD → RUB**: 92.45\n- **EUR → RUB**: 98.12\n- **GBP → RUB**: 115.30\n- **CNY → RUB**: 12.78\n\n*Данные носят ознакомительный характер.',
  search: 'Я нашёл информацию по вашему запросу. Вот что удалось найти:\n\n1. **Основная статья** — подробный обзор темы\n2. **Свежие новости** — последние события\n3. **Экспертное мнение** — аналитика от специалистов\n\nХотите, чтобы я углубился в какую-то конкретную тему?',
  wiki: 'По данным Википедии:\n\n> **Определение**\n> \n> Это интересная тема с богатой историей и множеством примеров применения.\n\n*Источник: Wikipedia*',
  image: '![Image](https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600)\n\nВот генерация по вашему описанию. Если нужна другая вариация — опишите детальнее!',
};

export default function ChatScreen() {
  const {
    user, sessions, currentSessionId, setCurrentSessionId,
    addMessage, createSession, selectedModel, setSelectedModel,
    webSearchOn, setWebSearchOn, reasoningOn, setReasoningOn,
    autoSearchOn, setAutoSearchOn, isTyping, setIsTyping,
  } = useApp();

  const [inputText, setInputText] = useState('');
  const [modelModalVisible, setModelModalVisible] = useState(false);
  const [inputMode, setInputMode] = useState<{ prefix: string; placeholder: string } | null>(null);
  const flatListRef = useRef<FlatList>(null);

  const currentSession = sessions.find(s => s.id === currentSessionId);
  const messages = currentSession?.messages || [];
  const showWelcome = messages.length === 0 && !isTyping;

  useEffect(() => {
    if (!currentSessionId) {
      createSession();
    }
  }, [currentSessionId]);

  const scrollToBottom = useCallback(() => {
    setTimeout(() => flatListRef.current?.scrollToEnd({ animated: true }), 100);
  }, []);

  const getAIResponse = (text: string): string => {
    const lower = text.toLowerCase();
    if (lower.includes('/services weather') || lower.includes('погод')) return AI_RESPONSES.weather;
    if (lower.includes('/code') || lower.includes('код')) return AI_RESPONSES.code;
    if (lower.includes('/services currency') || lower.includes('валют')) return AI_RESPONSES.currency;
    if (lower.includes('/search')) return AI_RESPONSES.search;
    if (lower.includes('/services wiki') || lower.includes('вики')) return AI_RESPONSES.wiki;
    if (lower.includes('/image') || lower.includes('изображ')) return AI_RESPONSES.image;
    return AI_RESPONSES.default;
  };

  const sendMessage = useCallback(async (text?: string) => {
    const msg = (text || inputText).trim();
    if (!msg || isTyping || !currentSessionId) return;

    let finalMsg = msg;
    if (inputMode && !msg.startsWith('/')) {
      finalMsg = inputMode.prefix + msg;
      setInputMode(null);
    }

    addMessage(currentSessionId, { role: 'user', content: finalMsg });
    setInputText('');
    setIsTyping(true);
    scrollToBottom();

    setTimeout(() => {
      setIsTyping(false);
      const reply = getAIResponse(finalMsg);
      addMessage(currentSessionId, { role: 'ai', content: reply });
      scrollToBottom();
    }, 1500 + Math.random() * 1500);
  }, [inputText, isTyping, currentSessionId, inputMode, addMessage, setIsTyping, scrollToBottom]);

  const activateMode = (mode: { prefix: string; placeholder: string }) => {
    setInputMode(mode);
  };

  const handleSuggestion = (mode: { prefix: string; placeholder: string }) => {
    activateMode(mode);
  };

  const renderItem = useCallback(({ item }: { item: typeof messages[0] }) => (
    <ChatMessage message={item} />
  ), []);

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor={colors.bgSurface} />
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 0}
      >
        {/* Top Bar */}
        <View style={styles.topbar}>
          <TouchableOpacity style={styles.modelSelector} onPress={() => setModelModalVisible(true)} activeOpacity={0.8}>
            <View style={styles.modelDot} />
            <Text style={styles.modelText}>{selectedModel}</Text>
            <Text style={styles.chevron}>▼</Text>
          </TouchableOpacity>

          <View style={styles.topbarActions}>
            <View style={styles.statusBadge}>
              <View style={styles.statusDot} />
              <Text style={styles.statusText}>Online</Text>
            </View>
            <TouchableOpacity style={styles.iconBtn} onPress={() => {
              if (currentSessionId) {
                createSession();
              }
            }} activeOpacity={0.8}>
              <Text style={styles.iconBtnText}>🗑️</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Chat */}
        <FlatList
          ref={flatListRef}
          data={messages}
          keyExtractor={item => item.id}
          renderItem={renderItem}
          contentContainerStyle={styles.chatContent}
          ListEmptyComponent={showWelcome ? (
            <View style={styles.welcome}>
              <View style={styles.welcomeOrb}>
                <Text style={styles.welcomeOrbText}>✦</Text>
              </View>
              <Text style={styles.welcomeTitle}>
                Привет! Я <Text style={styles.welcomeTitleAccent}>Khirad</Text>
              </Text>
              <Text style={styles.welcomeDesc}>
                Ваш интеллектуальный AI-ассистент. Задайте любой вопрос или используйте инструменты.
              </Text>
              <View style={styles.suggestions}>
                {SUGGESTIONS.map((s, i) => (
                  <SuggestionCard
                    key={i}
                    icon={s.icon}
                    title={s.title}
                    desc={s.desc}
                    onPress={() => handleSuggestion(s.mode)}
                  />
                ))}
              </View>
            </View>
          ) : null}
          ListFooterComponent={isTyping ? <TypingIndicator /> : null}
          onContentSizeChange={scrollToBottom}
        />

        {/* Input Area */}
        <View style={styles.inputArea}>
          <View style={styles.inputToolbar}>
            <TouchableOpacity
              style={[styles.toolbarBtn, webSearchOn && styles.toolbarBtnActive]}
              onPress={() => setWebSearchOn(!webSearchOn)}
              activeOpacity={0.8}
            >
              <Text style={styles.toolbarIcon}>🔍</Text>
              <Text style={[styles.toolbarText, webSearchOn && styles.toolbarTextActive]}>Поиск</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.toolbarBtn, reasoningOn && styles.toolbarBtnActive]}
              onPress={() => setReasoningOn(!reasoningOn)}
              activeOpacity={0.8}
            >
              <Text style={styles.toolbarIcon}>💭</Text>
              <Text style={[styles.toolbarText, reasoningOn && styles.toolbarTextActive]}>Рассуждение</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.toolbarBtn, autoSearchOn && styles.toolbarBtnActive]}
              onPress={() => setAutoSearchOn(!autoSearchOn)}
              activeOpacity={0.8}
            >
              <Text style={styles.toolbarIcon}>🔍➕</Text>
              <Text style={[styles.toolbarText, autoSearchOn && styles.toolbarTextActive]}>Авто поиск</Text>
            </TouchableOpacity>
          </View>

          <View style={styles.inputWrapper}>
            <TextInput
              style={styles.input}
              placeholder={inputMode ? inputMode.placeholder : 'Напишите сообщение...'}
              placeholderTextColor={colors.textMuted}
              value={inputText}
              onChangeText={setInputText}
              multiline
              maxLength={2000}
              onSubmitEditing={() => sendMessage()}
              blurOnSubmit={false}
            />
            <View style={styles.inputActions}>
              <TouchableOpacity
                style={styles.sendBtn}
                onPress={() => sendMessage()}
                disabled={!inputText.trim() || isTyping}
                activeOpacity={0.8}
              >
                <Text style={[styles.sendIcon, (!inputText.trim() || isTyping) && styles.sendIconDisabled]}>▶️</Text>
              </TouchableOpacity>
            </View>
          </View>

          <Text style={styles.inputFooter}>Khirad может ошибаться. Проверяйте важную информацию.</Text>
        </View>
      </KeyboardAvoidingView>

      <ModelSelectorModal
        visible={modelModalVisible}
        onClose={() => setModelModalVisible(false)}
        selected={selectedModel}
        onSelect={setSelectedModel}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.bgBase },
  flex: { flex: 1 },
  topbar: {
    height: 52,
    paddingHorizontal: 14,
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: colors.borderSoft,
    backgroundColor: colors.bgSurface,
  },
  modelSelector: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    paddingHorizontal: 10,
    paddingVertical: 5,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 99,
  },
  modelDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: colors.accentLight,
    shadowColor: colors.accentLight,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.6,
    shadowRadius: 6,
    elevation: 2,
  },
  modelText: { fontSize: 12, color: colors.textAccent, fontWeight: '600' },
  chevron: { fontSize: 10, color: colors.textAccent, marginLeft: 2 },
  topbarActions: { marginLeft: 'auto', flexDirection: 'row', gap: 6, alignItems: 'center' },
  statusBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingHorizontal: 8,
    paddingVertical: 3,
    backgroundColor: colors.successBg,
    borderWidth: 1,
    borderColor: 'rgba(34,197,94,0.25)',
    borderRadius: 99,
  },
  statusDot: {
    width: 5,
    height: 5,
    borderRadius: 3,
    backgroundColor: colors.success,
    shadowColor: colors.success,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 5,
    elevation: 2,
  },
  statusText: { fontSize: 10.5, color: colors.success, fontWeight: '600' },
  iconBtn: {
    width: 34,
    height: 34,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconBtnText: { fontSize: 14 },
  chatContent: { paddingHorizontal: 14, paddingVertical: 16, flexGrow: 1 },
  welcome: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    paddingVertical: 30,
    minHeight: 400,
  },
  welcomeOrb: {
    width: 70,
    height: 70,
    borderRadius: 35,
    backgroundColor: colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
    shadowColor: colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 40,
    elevation: 6,
  },
  welcomeOrbText: { fontSize: 32 },
  welcomeTitle: { fontSize: 22, fontWeight: '800', color: colors.textPrimary, marginBottom: 8 },
  welcomeTitleAccent: { color: colors.accentLight },
  welcomeDesc: {
    fontSize: 13.5,
    color: colors.textSecondary,
    textAlign: 'center',
    maxWidth: 400,
    lineHeight: 22,
    marginBottom: 24,
  },
  suggestions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    width: '100%',
    maxWidth: 500,
  },
  inputArea: {
    paddingHorizontal: 14,
    paddingTop: 10,
    paddingBottom: 14,
    backgroundColor: colors.bgSurface,
    borderTopWidth: 1,
    borderTopColor: colors.borderSoft,
  },
  inputToolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginBottom: 8,
    flexWrap: 'wrap',
  },
  toolbarBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    paddingHorizontal: 9,
    paddingVertical: 4,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderRadius: 99,
  },
  toolbarBtnActive: {
    backgroundColor: colors.accentDim,
    borderColor: colors.accent,
  },
  toolbarIcon: { fontSize: 12 },
  toolbarText: { fontSize: 11.5, color: colors.textMuted },
  toolbarTextActive: { color: colors.accentLight },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: 8,
    backgroundColor: colors.bgInput,
    borderWidth: 1.5,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 10,
    paddingVertical: 8,
  },
  input: {
    flex: 1,
    fontSize: 14,
    color: colors.textPrimary,
    lineHeight: 22,
    maxHeight: 120,
    paddingTop: 4,
    paddingBottom: 4,
  },
  inputActions: { flexDirection: 'row', alignItems: 'center', gap: 5 },
  sendBtn: {
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.35,
    shadowRadius: 16,
    elevation: 4,
  },
  sendIcon: { fontSize: 14, color: '#fff' },
  sendIconDisabled: { color: colors.textMuted },
  inputFooter: {
    textAlign: 'center',
    fontSize: 10.5,
    color: colors.textMuted,
    marginTop: 6,
  },
});
