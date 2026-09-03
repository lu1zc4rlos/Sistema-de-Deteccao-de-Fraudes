import { AuthProvider, useAuth } from './context/AuthContext'
import { LanguageProvider } from './context/LanguageContext'
import AuthPage from './pages/AuthPage'
import Dashboard from './pages/Dashboard'
import './styles/global.css'

function AppContent() {
  const { isAuthenticated } = useAuth();
  
  return (
    <div className="container">
      {!isAuthenticated ? (
        <AuthPage />
      ) : (
        <Dashboard />
      )}
    </div>
  )
}

function App() {
  return (
    <AuthProvider>
      <LanguageProvider>
        <AppContent />
      </LanguageProvider>
    </AuthProvider>
  )
}

export default App
