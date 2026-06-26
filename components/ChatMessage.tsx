import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';
import { Message } from '../context/AppContext';

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function formatContent(text: string): string {
  let html = escapeHtml(text);
  html = html.replace(/```(\w+)?\n?([\s\S]*?)```/g, (_, lang, code) => `<pre><code>${code.trim()}</code></pre>`);
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>');
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>');
  html = html.replace(/\n\n/g, '\n');
  html = html.replace(/\n/g, '\n');
  return html;
}

function stripHtml(html: string): string {
  return html
    .replace(/<pre><code>([\s\S]*?)<\/code><\/pre>/g, '\n```\n$1\n```\n')
    .replace(/<code>(.*?)<\/code>/g, '`$1`')
    .replace(/<strong>(.*?)<\/strong>/g, '*$1*')
    .replace(/<em>(.*?)<\/em>/g, '_$1_')
    .replace(/<br\/>/g, '\n')
    .replace(/<[^>]*>/g, '');
}

export default function ChatMessage({ message }: { message: Message }) {
  const isAI = message.role === 'ai';
  const formatted = isAI ? formatContent(message.content) : escapeHtml(message.content);
  const displayText = stripHtml(formatted);

  return (
    <View style={[styles.container, isAI ? styles.aiContainer : styles.userContainer]}>
      <View style={[styles.avatar, isAI ? styles.aiAvatar : styles.userAvatar]}>
        <Text style={styles.avatarText}>{isAI ? '✦' : '👤'}</Text>
      </View>
      <View style={styles.body}>
        <Text style={[styles.name, isAI ? styles.aiName : styles.userName]}>
          {isAI ? 'NovaMind' : 'Вы'}
        </Text>
        <View style={[styles.bubble, isAI ? styles.aiBubble : styles.userBubble]}>
          <Text style={[styles.bubbleText, isAI ? styles.aiBubbleText : styles.userBubbleText]}>
            {displayText}
          </Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    gap: 10,
    maxWidth: 750,
    width: '100%',
    alignSelf: 'center',
    marginBottom: 16,
  },
  aiContainer: {
    flexDirection: 'row',
  },
  userContainer: {
    flexDirection: 'row-reverse',
  },
  avatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 2,
  },
  aiAvatar: {
    backgroundColor: colors.accent,
    shadowColor: colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 12,
    elevation: 4,
  },
  userAvatar: {
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.border,
  },
  avatarText: {
    fontSize: 14,
  },
  body: {
    flex: 1,
    maxWidth: '85%',
  },
  name: {
    fontSize: 11,
    fontWeight: '600',
    paddingHorizontal: 4,
    marginBottom: 3,
  },
  aiName: {
    color: colors.textMuted,
  },
  userName: {
    color: colors.textMuted,
    textAlign: 'right',
  },
  bubble: {
    paddingHorizontal: 15,
    paddingVertical: 11,
    borderRadius: 14,
  },
  aiBubble: {
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderTopLeftRadius: 4,
  },
  userBubble: {
    backgroundColor: colors.accent,
    borderTopRightRadius: 4,
    shadowColor: colors.accent,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.35,
    shadowRadius: 20,
    elevation: 4,
  },
  bubbleText: {
    fontSize: 13.5,
    lineHeight: 22,
  },
  aiBubbleText: {
    color: colors.textPrimary,
  },
  userBubbleText: {
    color: '#fff',
  },
});
