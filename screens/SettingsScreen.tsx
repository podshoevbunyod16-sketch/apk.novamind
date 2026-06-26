import React, { useState } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, SafeAreaView, ScrollView, Modal, Pressable,
} from 'react-native';
import { useApp, ModelType } from '../context/AppContext';
import { colors } from '../theme/colors';

const MODELS: { name: ModelType; desc: string; color: string }[] = [
  { name: 'Deepseek R1', desc: 'Самая мощная модель', color: colors.modelPurple },
  { name: 'GPT OSS', desc: 'Баланс скорости и качества', color: colors.modelBlue },
  { name: 'GLM 4.7', desc: 'Максимальная скорость', color: colors.modelGreen },
];

export default function SettingsScreen() {
  const { user, logout, selectedModel, setSelectedModel } = useApp();
  const [modelModalVisible, setModelModalVisible] = useState(false);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Настройки</Text>
      </View>
      <ScrollView contentContainerStyle={styles.scroll}>
        {/* User Card */}
        <View style={styles.userCard}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{user?.avatar ? '🖼️' : '👤'}</Text>
          </View>
          <View style={styles.userInfo}>
            <Text style={styles.userName}>{user?.nick || 'Пользователь'}</Text>
            <Text style={styles.userPlan}>Базовый план</Text>
          </View>
        </View>

        {/* Model Selector */}
        <Text style={styles.sectionLabel}>Модель AI</Text>
        <TouchableOpacity style={styles.row} onPress={() => setModelModalVisible(true)} activeOpacity={0.8}>
          <View style={[styles.rowDot, { backgroundColor: MODELS.find(m => m.name === selectedModel)?.color || colors.accent }]} />
          <Text style={styles.rowText}>{selectedModel}</Text>
          <Text style={styles.rowArrow}>▸</Text>
        </TouchableOpacity>

        {/* Info */}
        <Text style={styles.sectionLabel}>О приложении</Text>
        <View style={styles.infoCard}>
          <Text style={styles.infoText}>🧠 <Text style={styles.infoBold}>NovaMind Khirad</Text></Text>
          <Text style={styles.infoSub}>AI-помощник на базе искусственного интеллекта</Text>
          <Text style={styles.infoVersion}>Версия 1.0.0-beta</Text>
        </View>

        {/* Logout */}
        <TouchableOpacity style={styles.logoutBtn} onPress={logout} activeOpacity={0.8}>
          <Text style={styles.logoutText}>Выйти из аккаунта</Text>
        </TouchableOpacity>
      </ScrollView>

      {/* Model Modal */}
      <Modal visible={modelModalVisible} transparent animationType="fade" onRequestClose={() => setModelModalVisible(false)}>
        <Pressable style={styles.modalOverlay} onPress={() => setModelModalVisible(false)}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Выберите модель</Text>
            {MODELS.map(model => (
              <TouchableOpacity
                key={model.name}
                style={[styles.modalOption, selectedModel === model.name && styles.modalOptionSelected]}
                onPress={() => { setSelectedModel(model.name); setModelModalVisible(false); }}
                activeOpacity={0.8}
              >
                <View style={[styles.modalDot, { backgroundColor: model.color }]} />
                <View>
                  <Text style={styles.modalOptionName}>{model.name}</Text>
                  <Text style={styles.modalOptionDesc}>{model.desc}</Text>
                </View>
              </TouchableOpacity>
            ))}
          </View>
        </Pressable>
      </Modal>
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
  userCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    backgroundColor: colors.bgCard,
    borderRadius: 14,
    padding: 14,
    borderWidth: 1,
    borderColor: colors.borderSoft,
  },
  avatar: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarText: { fontSize: 18 },
  userInfo: { flex: 1 },
  userName: { fontSize: 15, fontWeight: '700', color: colors.textPrimary },
  userPlan: { fontSize: 12, color: colors.textMuted, marginTop: 2 },
  sectionLabel: {
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1.2,
    textTransform: 'uppercase',
    color: colors.textMuted,
    marginTop: 8,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    backgroundColor: colors.bgCard,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    borderWidth: 1,
    borderColor: colors.borderSoft,
  },
  rowDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    shadowColor: colors.accent,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 6,
    elevation: 2,
  },
  rowText: { flex: 1, fontSize: 14, color: colors.textPrimary, fontWeight: '600' },
  rowArrow: { fontSize: 18, color: colors.textMuted },
  infoCard: {
    backgroundColor: colors.bgCard,
    borderRadius: 14,
    padding: 14,
    borderWidth: 1,
    borderColor: colors.borderSoft,
  },
  infoText: { fontSize: 14, color: colors.textPrimary, marginBottom: 4 },
  infoBold: { fontWeight: '700' },
  infoSub: { fontSize: 12, color: colors.textMuted, lineHeight: 18 },
  infoVersion: { fontSize: 11, color: colors.textMuted, marginTop: 8 },
  logoutBtn: {
    backgroundColor: colors.errorBg,
    borderWidth: 1,
    borderColor: 'rgba(239,68,68,0.25)',
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 8,
  },
  logoutText: { color: colors.error, fontSize: 14, fontWeight: '700' },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.6)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  modalContent: {
    backgroundColor: colors.bgPanel,
    borderRadius: 16,
    padding: 16,
    width: '100%',
    maxWidth: 320,
    borderWidth: 1,
    borderColor: colors.border,
  },
  modalTitle: { fontSize: 16, fontWeight: '700', color: colors.textPrimary, marginBottom: 12 },
  modalOption: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    padding: 10,
    borderRadius: 10,
  },
  modalOptionSelected: { backgroundColor: colors.accentDim },
  modalDot: { width: 8, height: 8, borderRadius: 4 },
  modalOptionName: { fontSize: 14, fontWeight: '600', color: colors.textPrimary },
  modalOptionDesc: { fontSize: 11, color: colors.textMuted, marginTop: 1 },
});
