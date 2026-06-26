import React from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, SafeAreaView } from 'react-native';
import { useApp } from '../context/AppContext';
import { colors } from '../theme/colors';

export default function HistoryScreen() {
  const { sessions, currentSessionId, setCurrentSessionId, deleteSession } = useApp();

  const renderItem = ({ item }: { item: typeof sessions[0] }) => {
    const isActive = item.id === currentSessionId;
    const date = new Date(item.timestamp).toLocaleDateString('ru-RU', {
      day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit',
    });

    return (
      <TouchableOpacity
        style={[styles.item, isActive && styles.itemActive]}
        onPress={() => setCurrentSessionId(item.id)}
        activeOpacity={0.8}
      >
        <View style={styles.dot} />
        <View style={styles.info}>
          <Text style={styles.title} numberOfLines={1}>{item.title}</Text>
          <Text style={styles.meta}>{item.messages.length} сообщений · {date}</Text>
        </View>
        <TouchableOpacity
          style={styles.deleteBtn}
          onPress={() => deleteSession(item.id)}
          activeOpacity={0.7}
        >
          <Text style={styles.deleteText}>✕</Text>
        </TouchableOpacity>
      </TouchableOpacity>
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>История чатов</Text>
      </View>
      {sessions.length === 0 ? (
        <View style={styles.empty}>
          <Text style={styles.emptyIcon}>📜</Text>
          <Text style={styles.emptyTitle}>История пуста</Text>
          <Text style={styles.emptyDesc}>Начните новый диалог во вкладке «Чат»</Text>
        </View>
      ) : (
        <FlatList
          data={sessions}
          keyExtractor={item => item.id}
          renderItem={renderItem}
          contentContainerStyle={styles.list}
        />
      )}
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
  list: { padding: 12, gap: 6 },
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    backgroundColor: colors.bgCard,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.borderSoft,
  },
  itemActive: {
    backgroundColor: colors.accentDim,
    borderColor: colors.border,
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: colors.accent,
  },
  info: { flex: 1 },
  title: { fontSize: 13, fontWeight: '600', color: colors.textPrimary, marginBottom: 2 },
  meta: { fontSize: 11, color: colors.textMuted },
  deleteBtn: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.errorBg,
    alignItems: 'center',
    justifyContent: 'center',
  },
  deleteText: { color: colors.error, fontSize: 12, fontWeight: '700' },
  empty: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 40,
  },
  emptyIcon: { fontSize: 48, marginBottom: 16 },
  emptyTitle: { fontSize: 16, fontWeight: '700', color: colors.textPrimary, marginBottom: 6 },
  emptyDesc: { fontSize: 13, color: colors.textMuted, textAlign: 'center' },
});
