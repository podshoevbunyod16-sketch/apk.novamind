import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, SafeAreaView } from 'react-native';
import { useApp, TOOLS } from '../context/AppContext';
import { colors } from '../theme/colors';
import { useNavigation } from '@react-navigation/native';

const SERVICES = [
  { icon: '🌤️', label: 'Погода', desc: 'Узнайте погоду в любом городе', mode: TOOLS[3] },
  { icon: '💱', label: 'Курс валют', desc: 'Актуальные курсы валют', mode: TOOLS[4] },
  { icon: '📚', label: 'Википедия', desc: 'Поиск по Википедии', mode: TOOLS[5] },
  { icon: '🔍', label: 'Веб-поиск', desc: 'Поиск информации в интернете', mode: TOOLS[0] },
  { icon: '🎨', label: 'Генерация изображений', desc: 'Создайте визуальный контент', mode: TOOLS[1] },
  { icon: '💻', label: 'Помощник кода', desc: 'Сгенерируйте код на любом языке', mode: TOOLS[2] },
];

export default function ToolsScreen() {
  const { setCurrentSessionId, createSession } = useApp();
  const navigation = useNavigation<any>();

  const activateTool = (mode: typeof TOOLS[0]) => {
    const id = createSession();
    setCurrentSessionId(id);
    navigation.navigate('Чат', { activateMode: mode });
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Сервисы и инструменты</Text>
      </View>
      <ScrollView contentContainerStyle={styles.scroll}>
        <Text style={styles.sectionLabel}>Основные</Text>
        <View style={styles.grid}>
          {SERVICES.slice(0, 3).map((s, i) => (
            <TouchableOpacity key={i} style={styles.card} onPress={() => activateTool(s.mode)} activeOpacity={0.8}>
              <Text style={styles.cardIcon}>{s.icon}</Text>
              <Text style={styles.cardLabel}>{s.label}</Text>
              <Text style={styles.cardDesc}>{s.desc}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <Text style={styles.sectionLabel}>Инструменты AI</Text>
        <View style={styles.grid}>
          {SERVICES.slice(3).map((s, i) => (
            <TouchableOpacity key={i} style={styles.card} onPress={() => activateTool(s.mode)} activeOpacity={0.8}>
              <Text style={styles.cardIcon}>{s.icon}</Text>
              <Text style={styles.cardLabel}>{s.label}</Text>
              <Text style={styles.cardDesc}>{s.desc}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <TouchableOpacity
          style={styles.composioCard}
          onPress={() => navigation.navigate('Composio')}
          activeOpacity={0.8}
        >
          <Text style={styles.composioIcon}>🔗</Text>
          <View>
            <Text style={styles.composioLabel}>Composio MCP</Text>
            <Text style={styles.composioDesc}>Интеграции с сервисами</Text>
          </View>
          <Text style={styles.composioArrow}>→</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.bgBase },
  header: {
    paddingHorizontal: 16,
    paddingVertical: 14,
    borderBottomWidth: 1,
    borderBottomColor: colors.borderSoft,
    backgroundColor: colors.bgSurface,
  },
  headerTitle: { fontSize: 17, fontWeight: '700', color: colors.textPrimary },
  scroll: { padding: 14, gap: 16 },
  sectionLabel: {
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1.2,
    textTransform: 'uppercase',
    color: colors.textMuted,
    marginTop: 8,
    marginBottom: 4,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  card: {
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderRadius: 14,
    padding: 14,
    width: '48%',
    minHeight: 110,
  },
  cardIcon: { fontSize: 24, marginBottom: 8 },
  cardLabel: { fontSize: 13, fontWeight: '700', color: colors.textPrimary, marginBottom: 3 },
  cardDesc: { fontSize: 11, color: colors.textMuted, lineHeight: 16 },
  composioCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    backgroundColor: colors.bgCard,
    borderWidth: 1,
    borderColor: colors.borderSoft,
    borderRadius: 14,
    padding: 14,
    marginTop: 8,
  },
  composioIcon: { fontSize: 24 },
  composioLabel: { fontSize: 14, fontWeight: '700', color: colors.textPrimary },
  composioDesc: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  composioArrow: { marginLeft: 'auto', fontSize: 18, color: colors.textMuted },
});
