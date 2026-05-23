import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import type { TransactionResponse, Device } from '../api/types';

const Dashboard: React.FC = () => {
  const { user, token, logout } = useAuth();
  const [amount, setAmount] = useState('');
  const [location, setLocation] = useState('BR');
  const [device, setDevice] = useState<Device>('MOBILE');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<TransactionResponse | null>(null);
  const [error, setError] = useState('');

  const handleTransaction = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);

    try {
      const response = await apiClient.post<TransactionResponse>(
        '/transactions/create',
        { amount: parseFloat(amount), location, device },
        token || ''
      );
      setResult(response);
    } catch (err: any) {
      setError(err.message || 'Erro ao processar análise de risco');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'APPROVED': return 'badge-approved';
      case 'SUSPICIOUS': return 'badge-suspicious';
      case 'BLOCKED': return 'badge-blocked';
      default: return '';
    }
  };

  const getScoreColor = (score: number) => {
    if (score > 70) return 'var(--danger)';
    if (score > 30) return 'var(--warning)';
    return 'var(--success)';
  };

  return (
    <div>
      <header className="nav-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ width: '40px', height: '40px', background: 'var(--primary)', borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white' }}>🛡️</div>
          <div>
            <h2 style={{ marginBottom: 0, fontSize: '1.25rem', color: 'var(--text-main)' }}>FraudGuard Pro</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Análise em tempo real</p>
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ textAlign: 'right' }}>
            <p style={{ fontWeight: 700, fontSize: '0.875rem', marginBottom: 0 }}>{user?.name}</p>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>{user?.email}</p>
          </div>
          <button onClick={logout} className="btn-ghost" style={{ fontSize: '0.875rem', color: 'var(--danger)' }}>
            Sair
          </button>
        </div>
      </header>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '2rem', alignItems: 'start' }}>
        {/* Painel de Entrada */}
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <span style={{ fontSize: '1.25rem' }}>💸</span>
            <h3 style={{ marginBottom: 0 }}>Simular Transação</h3>
          </div>
          
          <form onSubmit={handleTransaction}>
            <div className="input-group">
              <label>Valor da Operação (R$)</label>
              <input 
                type="number" 
                step="0.01" 
                value={amount} 
                onChange={(e) => setAmount(e.target.value)} 
                required 
                placeholder="0.00"
              />
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="input-group">
                <label>Localização (ISO)</label>
                <input 
                  type="text" 
                  maxLength={2} 
                  value={location} 
                  onChange={(e) => setLocation(e.target.value.toUpperCase())} 
                  required 
                  placeholder="BR"
                />
              </div>
              <div className="input-group">
                <label>Dispositivo</label>
                <select value={device} onChange={(e) => setDevice(e.target.value as Device)}>
                  <option value="MOBILE">Mobile</option>
                  <option value="DESKTOP">Desktop</option>
                  <option value="TABLET">Tablet</option>
                  <option value="NEW_DEVICE">Novo Dispositivo</option>
                  <option value="UNKNOWN">Desconhecido</option>
                </select>
              </div>
            </div>

            <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner"></span>
                  Processando...
                </>
              ) : 'Executar Análise de Risco'}
            </button>
          </form>
          {error && <div className="error-alert" style={{ marginTop: '1.5rem' }}>{error}</div>}
        </div>

        {/* Painel de Resultado */}
        <div className="card" style={{ borderLeft: result ? `4px solid ${getScoreColor(result.riskScore)}` : '1px solid var(--border)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
            <span style={{ fontSize: '1.25rem' }}>📊</span>
            <h3 style={{ marginBottom: 0 }}>Relatório de Risco</h3>
          </div>

          {!result && !loading && (
            <div style={{ textAlign: 'center', padding: '3rem 0' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem', opacity: 0.3 }}>📉</div>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                Aguardando dados para processamento...
              </p>
            </div>
          )}

          {loading && (
            <div style={{ textAlign: 'center', padding: '3rem 0' }}>
              <div className="loading-animation"></div>
              <p style={{ color: 'var(--primary)', fontWeight: 600, marginTop: '1rem' }}>Consultando motor de regras...</p>
            </div>
          )}

          {result && (
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', padding: '1rem', background: 'var(--bg)', borderRadius: 'var(--radius-md)' }}>
                <span style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--text-muted)' }}>VEREDITO</span>
                <span className={`badge ${getStatusBadgeClass(result.status)}`}>{result.status}</span>
              </div>

              <div className="score-display">
                <div style={{ fontSize: '2.5rem', fontWeight: 800, color: getScoreColor(result.riskScore) }}>
                  {result.riskScore}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>SCORE</div>
              </div>

              {result.fraudLogs.length > 0 ? (
                <div style={{ marginTop: '2rem' }}>
                  <h4 style={{ fontSize: '0.875rem', color: 'var(--text-main)', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span>🚩</span> Pontos de Atenção:
                  </h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                    {result.fraudLogs.map((log, index) => (
                      <div key={index} style={{ padding: '0.75rem', background: 'var(--bg)', borderRadius: 'var(--radius-sm)', fontSize: '0.875rem', borderLeft: '3px solid var(--warning)' }}>
                        {log.reason}
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <div style={{ marginTop: '2rem', textAlign: 'center', padding: '1rem', background: 'var(--success-bg)', borderRadius: 'var(--radius-md)', color: 'var(--success)', fontSize: '0.875rem', fontWeight: 600 }}>
                  ✅ Nenhuma irregularidade detectada
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
