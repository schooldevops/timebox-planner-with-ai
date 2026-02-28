// File: frontend-timebox/src/lib/api/auth.ts
import { apiClient } from './client';

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserResponse {
  userId: number;
  email: string;
  name: string;
  createdAt: string;
}

export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<UserResponse>('/api/v1/auth/register', data),

  login: (data: LoginRequest) =>
    apiClient.post<TokenResponse>('/api/v1/auth/login', data),

  logout: () =>
    apiClient.post('/api/v1/auth/logout'),

  refresh: (refreshToken: string) =>
    apiClient.post<{ accessToken: string; expiresIn: number }>('/api/v1/auth/refresh', { refreshToken }),
};

// File: frontend-timebox/src/lib/api/tasks.ts
export interface TaskResponse {
  taskId: number;
  title: string;
  description: string | null;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  estimatedMinutes: number | null;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE' | 'CANCELLED';
  tags: { tagId: number; name: string; color: string | null }[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  priority?: 'HIGH' | 'MEDIUM' | 'LOW';
  estimatedMinutes?: number;
  tagIds?: number[];
}

export const taskApi = {
  create: (data: CreateTaskRequest) =>
    apiClient.post<TaskResponse>('/api/v1/tasks', data),

  getAll: (params?: { status?: string; priority?: string; page?: number; size?: number }) =>
    apiClient.get<{ content: TaskResponse[]; totalElements: number; totalPages: number }>('/api/v1/tasks', { params }),

  getById: (taskId: number) =>
    apiClient.get<TaskResponse>(`/api/v1/tasks/${taskId}`),

  update: (taskId: number, data: CreateTaskRequest) =>
    apiClient.put<TaskResponse>(`/api/v1/tasks/${taskId}`, data),

  delete: (taskId: number) =>
    apiClient.delete(`/api/v1/tasks/${taskId}`),
};
