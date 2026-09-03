import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import type { UserResponse } from '../api/types';

const translations = {
  pt: {
    welcomeBack: 'Bem-vindo de volta',
    getStarted: 'Começar Agora',
    subtitleLogin: 'Proteja sua plataforma com inteligência',
    subtitleRegister: 'Crie sua conta no motor de antifraude',
    name: 'Nome Completo',
    email: 'E-mail Corporativo',
    password: 'Senha',
    confirmPassword: 'Confirmar Senha',
    loginBtn: 'Acessar Painel',
    registerBtn: 'Criar minha conta',
    newHere: 'Novo por aqui?',
    alreadyHaveAccount: 'Já tem uma conta?',
    signUp: 'Cadastre-se',
    signIn: 'Fazer login',
    emailError: 'O e-mail deve conter um "@".',
    passwordError: 'A senha deve ter pelo menos 8 caracteres, uma letra maiúscula, uma minúscula e um símbolo.',
    generalError: 'Erro ao autenticar. Verifique suas credenciais.',
    validationError: 'Verifique os campos obrigatórios (E-mail inválido ou senha fraca).',
    passwordsMismatch: 'As senhas não coincidem.',
    loading: 'Processando...'
  },
  en: {
    welcomeBack: 'Welcome back',
    getStarted: 'Get Started',
    subtitleLogin: 'Protect your platform with intelligence',
    subtitleRegister: 'Create your account on the anti-fraud engine',
    name: 'Full Name',
    email: 'Corporate Email',
    password: 'Password',
    confirmPassword: 'Confirm Password',
    loginBtn: 'Access Dashboard',
    registerBtn: 'Create Account',
    newHere: 'New here?',
    alreadyHaveAccount: 'Already have an account?',
    signUp: 'Sign up',
    signIn: 'Sign in',
    emailError: 'Email must contain an "@".',
    passwordError: 'Password must be at least 8 characters, with an uppercase, a lowercase, and a symbol.',
    generalError: 'Authentication error. Check your credentials.',
    validationError: 'Check required fields (Invalid email or weak password).',
    passwordsMismatch: 'Passwords do not match.',
    loading: 'Processing...'
  },
  es: {
    welcomeBack: 'Bienvenido de nuevo',
    getStarted: 'Empezar ahora',
    subtitleLogin: 'Proteja su plataforma con inteligencia',
    subtitleRegister: 'Cree su cuenta en el motor antifraude',
    name: 'Nombre completo',
    email: 'Correo electrónico corporativo',
    password: 'Contraseña',
    confirmPassword: 'Confirmar contraseña',
    loginBtn: 'Acceder al panel',
    registerBtn: 'Crear cuenta',
    newHere: '¿Nuevo aquí?',
    alreadyHaveAccount: '¿Ya tiene una cuenta?',
    signUp: 'Registrarse',
    signIn: 'Iniciar sesión',
    emailError: 'El correo electrónico debe contener un "@".',
    passwordError: 'La contraseña debe tener al menos 8 caracteres, con una letra mayúscula, una minúscula y un símbolo.',
    generalError: 'Error de autenticación. Verifique sus credenciales.',
    validationError: 'Verifique los campos obligatorios (correo electrónico no válido o contraseña débil).',
    passwordsMismatch: 'Las contraseñas no coinciden.',
    loading: 'Procesando...'
  }
};

const AuthPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [language, setLanguage] = useState<'pt' | 'en' | 'es'>('pt');
  const { login } = useAuth();
  
  const t = translations[language];

  const handleLanguageChange = (lang: 'pt' | 'en' | 'es') => {
    setLanguage(lang);
  };

  const validateEmail = (email: string) => {
    if (isLogin) return true;
    return email.includes('@');
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);
    if (!isLogin && value.length > 0 && !validateEmail(value)) {
      setEmailError(t.emailError);
    } else {
      setEmailError('');
    }
  };

  const validatePassword = (pwd: string) => {
    if (isLogin) return true;
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/;
    return regex.test(pwd);
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPassword(value);
    if (!isLogin && value.length > 0 && !validatePassword(value)) {
      setPasswordError(t.passwordError);
    } else {
      setPasswordError('');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isLogin) {
      if (!validateEmail(email) || !validatePassword(password)) {
        setError(t.validationError);
        return;
      }
      if (password !== confirmPassword) {
        setError(t.passwordsMismatch);
        return;
      }
    }
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        const response = await apiClient.post<UserResponse>('/auth/login', { email, password });
        login(response);
      } else {
        const response = await apiClient.post<UserResponse>('/auth/register', { name, email, password });
        login(response);
      }
    } catch (err: any) {
      setError(err.message || t.generalError);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '85vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div className="card" style={{ width: '100%', maxWidth: '460px', position: 'relative' }}>
        {/* Decorative Glow */}
        <div style={{ 
          position: 'absolute', 
          top: '-10%', 
          right: '-10%', 
          width: '60%', 
          height: '60%', 
          background: 'var(--accent-gradient)', 
          borderRadius: '50%', 
          filter: 'blur(60px)',
          opacity: 0.1,
          zIndex: 0
        }}></div>

        <div style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginBottom: '1rem' }}>
            <button onClick={() => handleLanguageChange('pt')} style={{ background: language === 'pt' ? 'rgba(0,0,0,0.1)' : 'transparent', border: '1px solid rgba(0,0,0,0.2)', cursor: 'pointer', padding: '0.2rem', borderRadius: '4px', color: 'black' }}>🇧🇷</button>
            <button onClick={() => handleLanguageChange('en')} style={{ background: language === 'en' ? 'rgba(0,0,0,0.1)' : 'transparent', border: '1px solid rgba(0,0,0,0.2)', cursor: 'pointer', padding: '0.2rem', borderRadius: '4px', color: 'black' }}>🇺🇸</button>
            <button onClick={() => handleLanguageChange('es')} style={{ background: language === 'es' ? 'rgba(0,0,0,0.1)' : 'transparent', border: '1px solid rgba(0,0,0,0.2)', cursor: 'pointer', padding: '0.2rem', borderRadius: '4px', color: 'black' }}>🇪🇸</button>
          </div>
          <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
            <div style={{ 
              width: '72px', 
              height: '72px', 
              background: 'var(--accent-gradient)', 
              borderRadius: '22px', 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center',
              margin: '0 auto 1.5rem',
              color: 'white',
              boxShadow: '0 10px 25px rgba(99, 102, 241, 0.4)',
              transform: 'rotate(-4deg)'
            }}>
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
            </div>
            <h1>{isLogin ? t.welcomeBack : t.getStarted}</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '1rem', fontWeight: 500 }}>
              {isLogin ? t.subtitleLogin : t.subtitleRegister}
            </p>
          </div>

          {error && (
            <div className="error-alert">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {!isLogin && (
              <div className="input-group">
                <label>{t.name}</label>
                <div style={{ position: 'relative' }}>
                  <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  </span>
                  <input 
                    type="text" 
                    placeholder="Luiz Silva"
                    value={name} 
                    onChange={(e) => setName(e.target.value)} 
                    required 
                    style={{ paddingLeft: '3rem' }}
                  />
                </div>
              </div>
            )}
            <div className="input-group">
              <label>{t.email}</label>
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                </span>
                <input 
                  type="email" 
                  placeholder="exemplo@empresa.com"
                  value={email} 
                  onChange={handleEmailChange} 
                  required 
                  style={{ paddingLeft: '3rem' }}
                />
              </div>
              {emailError && (
                <p style={{ color: 'var(--danger)', fontSize: '0.85rem', marginTop: '0.5rem' }}>{emailError}</p>
              )}
            </div>
            <div className="input-group">
              <label>{t.password}</label>
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </span>
                <input 
                  type={showPassword ? 'text' : 'password'} 
                  placeholder="••••••••"
                  value={password} 
                  onChange={handlePasswordChange} 
                  required 
                  style={{ paddingLeft: '3rem', paddingRight: '3rem' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    right: '0.5rem',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    padding: '0.5rem',
                    color: 'var(--text-soft)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    zIndex: 2,
                    minWidth: 'auto',
                    height: 'auto'
                  }}
                >
                  {showPassword ? (
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
                  ) : (
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  )}
                </button>
              </div>
              {passwordError && (
                <p style={{ color: 'var(--danger)', fontSize: '0.85rem', marginTop: '0.5rem' }}>{passwordError}</p>
              )}
            </div>

            {!isLogin && (
              <div className="input-group">
                <label>{t.confirmPassword}</label>
                <div style={{ position: 'relative' }}>
                  <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  </span>
                  <input 
                    type={showPassword ? 'text' : 'password'} 
                    placeholder="••••••••"
                    value={confirmPassword} 
                    onChange={(e) => setConfirmPassword(e.target.value)} 
                    required 
                    style={{ paddingLeft: '3rem' }}
                  />
                </div>
              </div>
            )}

            <button type="submit" className="btn-primary" style={{ width: '100%', padding: '1rem', marginTop: '1rem' }} disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner"></span>
                  {t.loading}
                </>
              ) : isLogin ? t.loginBtn : t.registerBtn}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '2.5rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border)' }}>
            <p style={{ fontSize: '0.95rem', color: 'var(--text-muted)', fontWeight: 500 }}>
              {isLogin ? t.newHere : t.alreadyHaveAccount} {' '}
              <button 
                onClick={() => setIsLogin(!isLogin)}
                className="btn-ghost"
                style={{ 
                  padding: '0.5rem 1rem', 
                  fontSize: '0.95rem', 
                  color: 'var(--primary)', 
                  fontWeight: 700,
                  background: 'var(--primary-light)',
                  borderRadius: 'var(--radius-sm)'
                }}
              >
                {isLogin ? t.signUp : t.signIn}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
