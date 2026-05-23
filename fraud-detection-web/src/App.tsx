import { AuthProvider, useAuth } from './context/AuthContext'
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
      <AppContent />
    </AuthProvider>
  )
}

export default App
