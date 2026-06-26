import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';

export default function TypingIndicator() {
  return (
    <View style={styles.container}>
      <View style={[styles.avatar, styles.aiAvatar]}>
        <Text style={styles.avatarText}>✦</Text>
      </View>
      <View style={styles.body}>
        <Text style={styles.name}>NovaMind</Text>
        <View style={styles.bubble}>
          <View style={styles.dots}>
            <View style={styles.dot} />
            <View style={[styles.dot, styles.dotDelay1]} />
            <View style={[styles.dot, styles.dotDelay2]} />
          </View>
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
    color: colors.textMuted,
    paddingHorizontal: 4,
    marginBottom: 3,
  },
  bubble: {
    paddingHorizontal: 15,
    paddingVertical: 14,
    borderRadius: 14,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderTopLeftRadius: 4,
    alignSelf: 'flex-start',
  },
  dots: {
    flexDirection: 'row',
    gap: 5,
    alignItems: 'center',
  },
  dot: {
    width: 7,
    height: 7,
    borderRadius: 4,
    backgroundColor: colors.textMuted,
    opacity: 0.4,
  },
  dotDelay1: {
    opacity: 0.7,
  },
  dotDelay2: {
    opacity: 1,
  },
});
