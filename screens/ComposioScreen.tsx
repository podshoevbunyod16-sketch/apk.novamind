import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, SafeAreaView, Alert } from 'react-native';
import { useApp, COMPOSIO_TOOLS } from '../context/AppContext';
import { colors } from '../theme/colors';

export default function ComposioScreen() {
  const { composioConnections, setComposioConnected } = useApp();

  const toggleConnection = (slug: string) => {
    const connected = !!composioConnections[slug];
    setComposioConnected(slug, !connected);
    Alert.alert(
      !connected ? 'Подключено' : 'Отключено',
      `${COMPOSIO_TOOLS.find(t => t.slug === slug)?.name} ${!connected ? 'успешно подключен' : 'отключен'}`,
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Composio MCP</Text>
        <Text style={styles.headerSub}>Интеграции с сервисами</Text>
      </View>
      <ScrollView contentContainerStyle={styles.scroll}>
        <Text style={styles.sectionLabel}>Доступные интеграции</Text>
        <View style={styles.grid}>
          {COMPOSIO_TOOLS.map(tool => {
            const connected = !!composioConnections[tool.slug];
            return (
              <TouchableOpacity
                key={tool.slug}
                style={[styles.card, connected && styles.cardConnected]}
                onPress={() => toggleConnection(tool.slug)}
                activeOpacity={0.8}
              >
                {connected && <View style={styles.connectedDot} />}
                <Text style={styles.cardIcon}>{tool.icon}</Text>
                <Text style={styles.cardName}>{tool.name}</Text>
                <Text style={[styles.cardStatus, connected && styles.cardStatusConnected]}>
                  {connected ? '✅ Подключено' : 'Нажмите для подключения'}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>

        <View style={styles.hintCard}>
          <Text style={styles.hintText}>💡 После подключения используйте команды:</Text>
          <Text style={styles.hintCode}>/composio accounts</Text>
          <Text style={styles.hintCode}>/composio tools [name]</Text>
          <Text style={styles.hintCode}>/composio do [action]</Text>
        </View>
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
  headerSub: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  scroll: { padding: 14 },
  sectionLabel: {
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1.2,
    textTransform: 'uppercase',
    color: colors.textMuted,
    marginBottom: 10,
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
    borderRadius: 12,
    padding: 12,
    width: '31%',
    alignItems: 'center',
    position: 'relative',
  },
  cardConnected: {
    borderColor: colors.success,
  },
  connectedDot: {
    position: 'absolute',
    top: 6,
    right: 6,
    width: 7,
    height: 7,
    borderRadius: 4,
    backgroundColor: colors.success,
  },
  cardIcon: { fontSize: 22, marginBottom: 5 },
  cardName: { fontSize: 11, fontWeight: '600', color: colors.textPrimary, textAlign: 'center' },
  cardStatus: { fontSize: 9, color: colors.textMuted, marginTop: 3, textAlign: 'center' },
  cardStatusConnected: { color: colors.success },
  hintCard: {
    backgroundColor: colors.bgCard,
    borderRadius: 14,
    padding: 14,
    marginTop: 16,
    borderWidth: 1,
    borderColor: colors.borderSoft,
  },
  hintText: { fontSize: 12, color: colors.textSecondary, marginBottom: 8 },
  hintCode: {
    fontSize: 12,
    color: colors.textAccent,
    fontFamily: 'monospace',
    backgroundColor: colors.bgHover,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
    marginBottom: 4,
  },
});
