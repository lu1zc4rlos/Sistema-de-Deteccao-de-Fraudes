import React, { createContext, useContext, useState, useEffect } from 'react';
import type { UserResponse, UserSummary } from '../api/types';

interface AuthContextType {
  user: UserSummary | null;
  token: string | null;
  login: (data: UserResponse) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserSummary | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const savedUser = localStorage.getItem('fraud_user');
    const savedToken = localStorage.getItem('fraud_token');
    if (savedUser && savedToken) {
      setUser(JSON.parse(savedUser));
      setToken(savedToken);
    }
  }, []);

  const login = (data: UserResponse) => {
    setUser(data.user);
    setToken(data.accessToken);
    localStorage.setItem('fraud_user', JSON.stringify(data.user));
    localStorage.setItem('fraud_token', data.accessToken);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('fraud_user');
    localStorage.removeItem('fraud_token');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
