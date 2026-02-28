// File: frontend-timebox/src/lib/store/authStore.ts
import { create } from 'zustand';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: number | null;
  isAuthenticated: boolean;
  setTokens: (accessToken: string, refreshToken: string) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: typeof window !== 'undefined' ? sessionStorage.getItem('accessToken') : null,
  refreshToken: typeof window !== 'undefined' ? sessionStorage.getItem('refreshToken') : null,
  userId: null,
  isAuthenticated: typeof window !== 'undefined' ? !!sessionStorage.getItem('accessToken') : false,

  setTokens: (accessToken, refreshToken) => {
    sessionStorage.setItem('accessToken', accessToken);
    sessionStorage.setItem('refreshToken', refreshToken);
    set({ accessToken, refreshToken, isAuthenticated: true });
  },

  clearAuth: () => {
    sessionStorage.clear();
    set({ accessToken: null, refreshToken: null, userId: null, isAuthenticated: false });
  },
}));
