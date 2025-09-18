import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react';
import { User } from '../types';
import { authService } from '../services/api';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
}

interface AuthContextType extends AuthState {
  login: (phone: string, password: string) => Promise<boolean>;
  register: (name: string, phone: string, password: string, district: string, className: string) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
}

type AuthAction = 
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGOUT' }
  | { type: 'INIT_AUTH'; payload: { user: User | null; isAuthenticated: boolean } };

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  loading: true,
};

const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: action.payload };
    case 'LOGIN_SUCCESS':
      return { 
        ...state, 
        user: action.payload, 
        isAuthenticated: true, 
        loading: false 
      };
    case 'LOGOUT':
      return { 
        ...state, 
        user: null, 
        isAuthenticated: false, 
        loading: false 
      };
    case 'INIT_AUTH':
      return {
        ...state,
        user: action.payload.user,
        isAuthenticated: action.payload.isAuthenticated,
        loading: false,
      };
    default:
      return state;
  }
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  useEffect(() => {
    // Initialize auth state from localStorage
    const currentUser = authService.getCurrentUser();
    const isAuthenticated = authService.isAuthenticated();
    
    dispatch({
      type: 'INIT_AUTH',
      payload: { user: currentUser, isAuthenticated },
    });
  }, []);

  const login = async (phone: string, password: string): Promise<boolean> => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      const response = await authService.login({ phone, password });
      
      if (response.success && response.data) {
        const user: User = {
          id: response.data.userId,
          name: response.data.name,
          phone: response.data.phone,
          district: '', // Will be updated with user details later
          className: '',
          guest: response.data.guest,
        };
        dispatch({ type: 'LOGIN_SUCCESS', payload: user });
        return true;
      } else {
        dispatch({ type: 'SET_LOADING', payload: false });
        return false;
      }
    } catch (error) {
      console.error('Login error:', error);
      dispatch({ type: 'SET_LOADING', payload: false });
      return false;
    }
  };

  const register = async (
    name: string, 
    phone: string, 
    password: string, 
    district: string, 
    className: string
  ): Promise<{ success: boolean; message?: string }> => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      const response = await authService.register({ 
        name, 
        phone, 
        password, 
        district, 
        className 
      });
      
      if (response.success && response.data) {
        const user: User = {
          id: response.data.userId,
          name: response.data.name,
          phone: response.data.phone,
          district,
          className,
          guest: response.data.guest,
        };
        dispatch({ type: 'LOGIN_SUCCESS', payload: user });
        return { success: true };
      } else {
        dispatch({ type: 'SET_LOADING', payload: false });
        return { success: false, message: response.message || 'Registration failed' };
      }
    } catch (error: any) {
      console.error('Registration error:', error);
      dispatch({ type: 'SET_LOADING', payload: false });
      const errorMessage = error.response?.data?.message || 'Registration failed. Please try again.';
      return { success: false, message: errorMessage };
    }
  };

  const logout = () => {
    authService.logout();
    dispatch({ type: 'LOGOUT' });
  };

  const value: AuthContextType = {
    ...state,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};