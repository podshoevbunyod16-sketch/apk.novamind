import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

export type Message = {
  id: string;
  role: 'user' | 'ai';
  content: string;
  timestamp: number;
};

export type ChatSession = {
  id: string;
  title: string;
  messages: Message[];
  timestamp: number;
};

export type ModelType = 'Deepseek R1' | 'GPT OSS' | 'GLM 4.7';

export type ToolMode = {
  prefix: string;
  placeholder: string;
  label: string;
};

export const TOOLS: ToolMode[] = [
  { prefix: '/search ', placeholder: 'Введите запрос для поиска...', label: 'Веб-поиск' },
  { prefix: '/image ', placeholder: 'Опишите изображение для генерации...', label: 'Генерация изображений' },
  { prefix: '/code ', placeholder: 'Опишите, какой код создать...', label: 'Помощник кода' },
  { prefix: '/services weather ', placeholder: 'Введите город для погоды...', label: 'Погода' },
  { prefix: '/services currency ', placeholder: 'Введите валюты (USD RUB)...', label: 'Курс валют' },
  { prefix: '/services wiki ', placeholder: 'Введите запрос для Википедии...', label: 'Википедия' },
];

export const COMPOSIO_TOOLS = [
  { slug: 'github', name: 'GitHub', icon: '🐙', connected: false },
  { slug: 'gmail', name: 'Gmail', icon: '📧', connected: false },
  { slug: 'notion', name: 'Notion', icon: '📝', connected: false },
  { slug: 'slack', name: 'Slack', icon: '💬', connected: false },
  { slug: 'googlecalendar', name: 'Google Calendar', icon: '📅', connected: false },
  { slug: 'googledrive', name: 'Google Drive', icon: '☁️', connected: false },
  { slug: 'trello', name: 'Trello', icon: '📋', connected: false },
  { slug: 'twitter', name: 'Twitter', icon: '🐦', connected: false },
  { slug: 'discord', name: 'Discord', icon: '🎮', connected: false },
  { slug: 'jira', name: 'Jira', icon: '🔵', connected: false },
  { slug: 'linear', name: 'Linear', icon: '⚡', connected: false },
  { slug: 'youtube', name: 'YouTube', icon: '▶️', connected: false },
  { slug: 'shopify', name: 'Shopify', icon: '🛒', connected: false },
  { slug: 'hubspot', name: 'HubSpot', icon: '🟠', connected: false },
  { slug: 'airtable', name: 'Airtable', icon: '🗃️', connected: false },
  { slug: 'dropbox', name: 'Dropbox', icon: '📦', connected: false },
  { slug: 'figma', name: 'Figma', icon: '🎨', connected: false },
  { slug: 'stripe', name: 'Stripe', icon: '💳', connected: false },
  { slug: 'zoom', name: 'Zoom', icon: '📹', connected: false },
  { slug: 'asana', name: 'Asana', icon: '🎯', connected: false },
];

type AppContextType = {
  user: { nick: string; email: string; avatar: string; isAdmin: boolean } | null;
  setUser: (user: AppContextType['user']) => void;
  sessions: ChatSession[];
  currentSessionId: string | null;
  setCurrentSessionId: (id: string | null) => void;
  addMessage: (sessionId: string, message: Omit<Message, 'id' | 'timestamp'>) => void;
  createSession: () => string;
  deleteSession: (id: string) => void;
  selectedModel: ModelType;
  setSelectedModel: (model: ModelType) => void;
  webSearchOn: boolean;
  setWebSearchOn: (v: boolean) => void;
  reasoningOn: boolean;
  setReasoningOn: (v: boolean) => void;
  autoSearchOn: boolean;
  setAutoSearchOn: (v: boolean) => void;
  isTyping: boolean;
  setIsTyping: (v: boolean) => void;
  logout: () => void;
  composioConnections: Record<string, boolean>;
  setComposioConnected: (slug: string, connected: boolean) => void;
};

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AppContextType['user']>(null);
  const [sessions, setSessions] = useState<ChatSession[]>([]);
  const [currentSessionId, setCurrentSessionId] = useState<string | null>(null);
  const [selectedModel, setSelectedModel] = useState<ModelType>('Deepseek R1');
  const [webSearchOn, setWebSearchOn] = useState(false);
  const [reasoningOn, setReasoningOn] = useState(false);
  const [autoSearchOn, setAutoSearchOn] = useState(false);
  const [isTyping, setIsTyping] = useState(false);
  const [composioConnections, setComposioConnections] = useState<Record<string, boolean>>({});
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const userData = await AsyncStorage.getItem('nova_user');
        if (userData) setUser(JSON.parse(userData));
        const sessionsData = await AsyncStorage.getItem('nova_sessions');
        if (sessionsData) setSessions(JSON.parse(sessionsData));
        const modelData = await AsyncStorage.getItem('nova_model');
        if (modelData) setSelectedModel(modelData as ModelType);
        const composioData = await AsyncStorage.getItem('nova_composio');
        if (composioData) setComposioConnections(JSON.parse(composioData));
      } catch (e) {}
      setLoaded(true);
    })();
  }, []);

  useEffect(() => {
    if (!loaded) return;
    AsyncStorage.setItem('nova_user', JSON.stringify(user)).catch(() => {});
  }, [user, loaded]);

  useEffect(() => {
    if (!loaded) return;
    AsyncStorage.setItem('nova_sessions', JSON.stringify(sessions)).catch(() => {});
  }, [sessions, loaded]);

  useEffect(() => {
    if (!loaded) return;
    AsyncStorage.setItem('nova_model', selectedModel).catch(() => {});
  }, [selectedModel, loaded]);

  useEffect(() => {
    if (!loaded) return;
    AsyncStorage.setItem('nova_composio', JSON.stringify(composioConnections)).catch(() => {});
  }, [composioConnections, loaded]);

  const createSession = useCallback(() => {
    const id = Date.now().toString();
    const session: ChatSession = {
      id,
      title: 'Новый диалог',
      messages: [],
      timestamp: Date.now(),
    };
    setSessions(prev => [session, ...prev]);
    setCurrentSessionId(id);
    return id;
  }, []);

  const addMessage = useCallback((sessionId: string, message: Omit<Message, 'id' | 'timestamp'>) => {
    setSessions(prev => {
      const updated = prev.map(s => {
        if (s.id !== sessionId) return s;
        const newMsg: Message = {
          ...message,
          id: Date.now().toString() + Math.random().toString(36).slice(2),
          timestamp: Date.now(),
        };
        const messages = [...s.messages, newMsg];
        const title = messages.find(m => m.role === 'user')?.content.slice(0, 30) || 'Новый диалог';
        return { ...s, messages, title, timestamp: Date.now() };
      });
      return updated.sort((a, b) => b.timestamp - a.timestamp);
    });
  }, []);

  const deleteSession = useCallback((id: string) => {
    setSessions(prev => prev.filter(s => s.id !== id));
    if (currentSessionId === id) setCurrentSessionId(null);
  }, [currentSessionId]);

  const logout = useCallback(() => {
    setUser(null);
    setSessions([]);
    setCurrentSessionId(null);
    Promise.all([
      AsyncStorage.removeItem('nova_user'),
      AsyncStorage.removeItem('nova_sessions'),
      AsyncStorage.removeItem('nova_model'),
      AsyncStorage.removeItem('nova_composio'),
    ]).catch(() => {});
  }, []);

  const setComposioConnected = useCallback((slug: string, connected: boolean) => {
    setComposioConnections(prev => ({ ...prev, [slug]: connected }));
  }, []);

  if (!loaded) return null;

  return (
    <AppContext.Provider value={{
      user, setUser,
      sessions, currentSessionId, setCurrentSessionId,
      addMessage, createSession, deleteSession,
      selectedModel, setSelectedModel,
      webSearchOn, setWebSearchOn,
      reasoningOn, setReasoningOn,
      autoSearchOn, setAutoSearchOn,
      isTyping, setIsTyping,
      logout,
      composioConnections, setComposioConnected,
    }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useApp must be used within AppProvider');
  return ctx;
}
