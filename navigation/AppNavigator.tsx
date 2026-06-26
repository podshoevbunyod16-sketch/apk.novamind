import React from 'react';
import { Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { useApp } from '../context/AppContext';
import AuthScreen from '../screens/AuthScreen';
import ChatScreen from '../screens/ChatScreen';
import HistoryScreen from '../screens/HistoryScreen';
import ToolsScreen from '../screens/ToolsScreen';
import SettingsScreen from '../screens/SettingsScreen';
import ComposioScreen from '../screens/ComposioScreen';
import { colors } from '../theme/colors';

export type RootStackParamList = {
  Auth: undefined;
  Main: undefined;
  Composio: undefined;
};

export type MainTabParamList = {
  'Чат': undefined;
  'История': undefined;
  'Инструменты': undefined;
  'Настройки': undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<MainTabParamList>();

function TabIcon({ focused, emoji }: { focused: boolean; emoji: string }) {
  return (
    <Text style={{ fontSize: focused ? 22 : 20, opacity: focused ? 1 : 0.6 }}>
      {emoji}
    </Text>
  );
}

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: colors.bgSurface,
          borderTopWidth: 1,
          borderTopColor: colors.borderSoft,
          height: 64,
          paddingBottom: 8,
          paddingTop: 6,
        },
        tabBarLabelStyle: {
          fontSize: 10,
          fontWeight: '600',
        },
        tabBarActiveTintColor: colors.textAccent,
        tabBarInactiveTintColor: colors.textMuted,
      }}
    >
      <Tab.Screen
        name="Чат"
        component={ChatScreen}
        options={{ tabBarIcon: ({ focused }) => <TabIcon focused={focused} emoji="💬" /> }}
      />
      <Tab.Screen
        name="История"
        component={HistoryScreen}
        options={{ tabBarIcon: ({ focused }) => <TabIcon focused={focused} emoji="📜" /> }}
      />
      <Tab.Screen
        name="Инструменты"
        component={ToolsScreen}
        options={{ tabBarIcon: ({ focused }) => <TabIcon focused={focused} emoji="🛠️" /> }}
      />
      <Tab.Screen
        name="Настройки"
        component={SettingsScreen}
        options={{ tabBarIcon: ({ focused }) => <TabIcon focused={focused} emoji="⚙️" /> }}
      />
    </Tab.Navigator>
  );
}

export default function AppNavigator() {
  const { user } = useApp();

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!user ? (
          <Stack.Screen name="Auth" component={AuthScreen} />
        ) : (
          <>
            <Stack.Screen name="Main" component={MainTabs} />
            <Stack.Screen
              name="Composio"
              component={ComposioScreen}
              options={{
                headerShown: true,
                headerStyle: { backgroundColor: colors.bgSurface },
                headerTintColor: colors.textPrimary,
                headerTitleStyle: { fontWeight: '700' },
                headerBackVisible: true,
              }}
            />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
