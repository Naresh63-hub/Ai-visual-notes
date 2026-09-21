import { request } from './api';
import { AuthResponse, User } from '../types';

export const authService = {
  async register(name: string, email: string, password: string): Promise<AuthResponse> {
    const data = await request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
    });
    if (data.token) {
      localStorage.setItem('visualnotes_token', data.token);
      localStorage.setItem('visualnotes_user', JSON.stringify(data.user));
    }
    return data;
  },

  async login(email: string, password: string): Promise<AuthResponse> {
    const data = await request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
    if (data.token) {
      localStorage.setItem('visualnotes_token', data.token);
      localStorage.setItem('visualnotes_user', JSON.stringify(data.user));
    }
    return data;
  },

  logout(): void {
    localStorage.removeItem('visualnotes_token');
    localStorage.removeItem('visualnotes_user');
  },

  async getCurrentUser(): Promise<User | null> {
    try {
      const user = await request<User>('/auth/me');
      return user;
    } catch {
      return null;
    }
  },

  getStoredUser(): User | null {
    try {
      const userStr = localStorage.getItem('visualnotes_user');
      return userStr ? JSON.parse(userStr) : null;
    } catch {
      return null;
    }
  }
};
