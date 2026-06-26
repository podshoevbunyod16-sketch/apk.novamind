import React from 'react';
import { View, Text, TouchableOpacity, Modal, StyleSheet, Pressable } from 'react-native';
import { colors } from '../theme/colors';
import { ModelType } from '../context/AppContext';

interface Props {
  visible: boolean;
  onClose: () => void;
  selected: ModelType;
  onSelect: (model: ModelType) => void;
}

const MODELS: { name: ModelType; desc: string; color: string }[] = [
  { name: 'Deepseek R1', desc: 'Самая мощная модель', color: colors.modelPurple },
  { name: 'GPT OSS', desc: 'Баланс скорости и качества', color: colors.modelBlue },
  { name: 'GLM 4.7', desc: 'Максимальная скорость', color: colors.modelGreen },
];

export default function ModelSelectorModal({ visible, onClose, selected, onSelect }: Props) {
  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <Pressable style={styles.overlay} onPress={onClose}>
        <View style={styles.dropdown}>
          {MODELS.map(model => (
            <TouchableOpacity
              key={model.name}
              style={[styles.option, selected === model.name && styles.optionSelected]}
              onPress={() => { onSelect(model.name); onClose(); }}
              activeOpacity={0.8}
            >
              <View style={[styles.dot, { backgroundColor: model.color, shadowColor: model.color }]} />
              <View>
                <Text style={styles.optionName}>{model.name}</Text>
                <Text style={styles.optionDesc}>{model.desc}</Text>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'flex-start',
    paddingTop: 60,
    paddingHorizontal: 14,
  },
  dropdown: {
    backgroundColor: colors.bgPanel,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    padding: 4,
    maxWidth: 280,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 12 },
    shadowOpacity: 0.5,
    shadowRadius: 32,
    elevation: 10,
  },
  option: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 10,
  },
  optionSelected: {
    backgroundColor: colors.accentDim,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.6,
    shadowRadius: 6,
    elevation: 2,
  },
  optionName: {
    fontSize: 13,
    fontWeight: '600',
    color: colors.textPrimary,
  },
  optionDesc: {
    fontSize: 11,
    color: colors.textMuted,
    marginTop: 1,
  },
});
