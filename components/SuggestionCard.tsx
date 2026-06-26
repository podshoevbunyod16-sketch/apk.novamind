import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';

interface Props {
  icon: string;
  title: string;
  desc: string;
  onPress: () => void;
}

export default function SuggestionCard({ icon, title, desc, onPress }: Props) {
  return (
    <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.8}>
      <Text style={styles.icon}>{icon}</Text>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.desc}>{desc}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 12,
    flex: 1,
    minHeight: 100,
  },
  icon: {
    fontSize: 22,
    marginBottom: 6,
  },
  title: {
    fontSize: 12.5,
    fontWeight: '700',
    color: colors.textPrimary,
    marginBottom: 3,
  },
  desc: {
    fontSize: 11,
    color: colors.textMuted,
    lineHeight: 16,
  },
});
