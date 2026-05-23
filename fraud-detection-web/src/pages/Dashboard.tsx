import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiClient } from '../api/client';
import type { TransactionResponse, Device, TransactionStatsResponse } from '../api/types';

const Dashboard: React.FC = () => {
  const { user, token, logout } = useAuth();
  const [amount, setAmount] = useState('');
  const [location, setLocation] = useState('BR');
  const [device, setDevice] = useState<Device>('MOBILE');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<TransactionResponse | null>(null);
  const [history, setHistory] = useState<TransactionResponse[]>([]);
  const [stats, setStats] = useState<TransactionStatsResponse | null>(null);
  const [error, setError] = useState('');

  const fetchData = async () => {
    if (!token) return;
    
    // Carregar histórico
    try {
      const historyData = await apiClient.get<TransactionResponse[]>('/transactions/history', token);
      setHistory(historyData);
    } catch (err) {
      console.error('Erro ao carregar histórico', err);
    }

    // Carregar estatísticas separadamente para não travar o histórico
    try {
      const statsData = await apiClient.get<TransactionStatsResponse>('/transactions/stats', token);
      console.log('Estatísticas recebidas:', statsData);
      
      // Garantir que os campos numéricos sejam tratados corretamente
      if (statsData) {
        const processedStats = {
          ...statsData,
          totalAmount: Number(statsData.totalAmount) || 0,
          averageRiskScore: Number(statsData.averageRiskScore) || 0,
          totalTransactions: Number(statsData.totalTransactions) || 0
        };
        setStats(processedStats);
      }
    } catch (err) {
      console.error('Erro ao carregar estatísticas', err);
    }
  };

  useEffect(() => {
    fetchData();
  }, [token]);

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
      
      // Atualização imediata do histórico local para melhor UX
      setHistory(prev => [response, ...prev]);
      
      // Sync completo com a API após pequeno delay
      setTimeout(() => {
        fetchData();
      }, 800);

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

  const exportToCSV = () => {
    if (history.length === 0) return;
    
    const headers = ['Data', 'Hora', 'Valor', 'Local', 'Dispositivo', 'Score', 'Status'];
    const rows = history.map(item => [
      new Date(item.timestamp).toLocaleDateString('pt-BR'),
      new Date(item.timestamp).toLocaleTimeString('pt-BR'),
      item.amount.toString(),
      item.location,
      item.device,
      item.riskScore.toString(),
      item.status
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(r => r.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `relatorio_fraude_${new Date().getTime()}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div style={{ animation: 'fadeIn 0.5s ease-out' }}>
      <header className="nav-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ 
            width: '48px', 
            height: '48px', 
            background: 'var(--primary)', 
            borderRadius: '14px', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            color: 'white',
            boxShadow: '0 4px 12px var(--primary-glow)'
          }}>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
          </div>
          <div>
            <h2 style={{ marginBottom: 0, fontSize: '1.4rem', color: 'var(--text-main)', letterSpacing: '-0.03em' }}>FraudGuard Pro</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 600 }}>SISTEMA DE ANÁLISE EM TEMPO REAL</p>
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '2rem' }}>
          <div className="hide-mobile" style={{ textAlign: 'right' }}>
            <p style={{ fontWeight: 800, fontSize: '0.95rem', marginBottom: 0, color: 'var(--text-main)' }}>{user?.name}</p>
          </div>
          <button onClick={logout} className="btn-ghost" style={{ 
            fontSize: '0.85rem', 
            color: 'var(--danger)', 
            background: 'var(--danger-bg)',
            padding: '0.5rem 1rem',
            border: '1px solid rgba(239, 68, 68, 0.1)',
            borderRadius: 'var(--radius-md)'
          }}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            Sair
          </button>
        </div>
      </header>

      {/* Stats Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
        <div className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)' }}>TOTAL ANALISADO</span>
          <span style={{ fontSize: '1.75rem', fontWeight: 900 }}>{stats?.totalTransactions || 0}</span>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)' }}>Transações</span>
        </div>
        <div className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)' }}>VOLUME TOTAL</span>
          <span style={{ fontSize: '1.75rem', fontWeight: 900 }}>R$ {stats?.totalAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || '0,00'}</span>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--success)' }}>Movimentado</span>
        </div>
        <div className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)' }}>RISCO MÉDIO</span>
          <span style={{ fontSize: '1.75rem', fontWeight: 900, color: getScoreColor(stats?.averageRiskScore || 0) }}>
            {stats?.averageRiskScore.toFixed(1) || '0.0'}
          </span>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-soft)' }}>Pontos</span>
        </div>
        <div className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)' }}>BLOQUEIOS</span>
          <span style={{ fontSize: '1.75rem', fontWeight: 900, color: 'var(--danger)' }}>{stats?.blockedCount || 0}</span>
          <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--danger)' }}>Fraudes evitadas</span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(380px, 1fr))', gap: '2.5rem', alignItems: 'start', marginBottom: '3rem' }}>
        {/* Simulation Panel */}
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ padding: '0.75rem', background: 'var(--primary-light)', color: 'var(--primary)', borderRadius: '12px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
            </div>
            <h3 style={{ marginBottom: 0, fontSize: '1.25rem' }}>Simular Transação</h3>
          </div>
          
          <form onSubmit={handleTransaction}>
            <div className="input-group">
              <label>Valor da Operação</label>
              <div style={{ position: 'relative' }}>
                <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', fontWeight: 800, color: 'var(--text-soft)' }}>R$</span>
                <input 
                  type="number" 
                  step="0.01" 
                  value={amount} 
                  onChange={(e) => setAmount(e.target.value)} 
                  required 
                  placeholder="0,00"
                  style={{ paddingLeft: '2.75rem', fontSize: '1.125rem', fontWeight: 700 }}
                />
              </div>
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
              <div className="input-group">
                <label>Localização (ISO)</label>
                <div style={{ position: 'relative' }}>
                  <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                  </span>
                  <input 
                    type="text" 
                    maxLength={2} 
                    value={location} 
                    onChange={(e) => setLocation(e.target.value.toUpperCase())} 
                    required 
                    placeholder="BR"
                    style={{ paddingLeft: '2.75rem' }}
                  />
                </div>
              </div>
              <div className="input-group">
                <label>Dispositivo</label>
                <select value={device} onChange={(e) => setDevice(e.target.value as Device)}>
                  <option value="MOBILE">📱 Mobile</option>
                  <option value="DESKTOP">💻 Desktop</option>
                  <option value="TABLET">平板 Tablet</option>
                  <option value="NEW_DEVICE">✨ Novo Device</option>
                  <option value="UNKNOWN">❓ Desconhecido</option>
                </select>
              </div>
            </div>

            <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem', padding: '1.125rem' }} disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner"></span>
                  Analisando Riscos...
                </>
              ) : (
                <>
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                  Executar Análise Inteligente
                </>
              )}
            </button>
          </form>
          {error && <div className="error-alert" style={{ marginTop: '2rem' }}>{error}</div>}
        </div>

        {/* Result Panel */}
        <div className="card" style={{ 
          borderTop: result ? `6px solid ${getScoreColor(result.riskScore)}` : '1px solid var(--card-border)',
          background: result ? 'rgba(255, 255, 255, 0.95)' : 'var(--glass-bg)'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ padding: '0.75rem', background: 'var(--secondary-light)', color: 'var(--secondary)', borderRadius: '12px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
            </div>
            <h3 style={{ marginBottom: 0, fontSize: '1.25rem' }}>Relatório de Inteligência</h3>
          </div>

          {!result && !loading && (
            <div style={{ textAlign: 'center', padding: '4rem 0' }}>
              <div style={{ width: '80px', height: '80px', background: 'var(--secondary-light)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.5rem', color: 'var(--text-soft)' }}>
                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" style={{ margin: '0 auto' }}><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
              </div>
              <p style={{ color: 'var(--text-muted)', fontSize: '1rem', fontWeight: 600, maxWidth: '240px', margin: '0 auto' }}>
                Submeta uma transação para visualizar o diagnóstico de fraude.
              </p>
            </div>
          )}

          {loading && (
            <div style={{ textAlign: 'center', padding: '4.5rem 0' }}>
              <div className="loading-animation"></div>
              <p style={{ color: 'var(--primary)', fontWeight: 800, marginTop: '1.5rem', letterSpacing: '0.05em' }}>CONSULTANDO MOTOR DE REGRAS...</p>
            </div>
          )}

          {result && (
            <div style={{ animation: 'fadeInUp 0.4s ease-out' }}>
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center', 
                marginBottom: '2.5rem', 
                padding: '1.25rem', 
                background: 'var(--bg-app)', 
                borderRadius: 'var(--radius-md)',
                boxShadow: 'var(--shadow-inner)'
              }}>
                <span style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', letterSpacing: '0.1em' }}>VEREDITO FINAL</span>
                <span className={`badge ${getStatusBadgeClass(result.status)}`}>
                  {result.status === 'APPROVED' && <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>}
                  {result.status === 'SUSPICIOUS' && <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>}
                  {result.status === 'BLOCKED' && <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>}
                  {result.status}
                </span>
              </div>

              <div className="score-display">
                <div style={{ fontSize: '3.5rem', fontWeight: 900, color: getScoreColor(result.riskScore), lineHeight: 1 }}>
                  {result.riskScore}
                </div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-soft)', fontWeight: 800, marginTop: '0.25rem' }}>RISK SCORE</div>
              </div>

              {result.fraudLogs.length > 0 ? (
                <div style={{ marginTop: '2.5rem' }}>
                  <h4 style={{ fontSize: '0.875rem', color: 'var(--text-main)', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.625rem' }}>
                    <div style={{ width: '24px', height: '24px', background: 'var(--warning-bg)', color: 'var(--warning)', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                    </div>
                    Pontos de Atenção Identificados:
                  </h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    {result.fraudLogs.map((log, index) => (
                      <div key={index} style={{ 
                        padding: '1rem', 
                        background: 'white', 
                        borderRadius: 'var(--radius-md)', 
                        fontSize: '0.9rem', 
                        borderLeft: '4px solid var(--warning)',
                        boxShadow: 'var(--shadow-sm)',
                        fontWeight: 500,
                        color: 'var(--text-muted)'
                      }}>
                        {log.reason}
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <div style={{ 
                  marginTop: '2.5rem', 
                  textAlign: 'center', 
                  padding: '1.5rem', 
                  background: 'linear-gradient(135deg, var(--success-bg) 0%, #fff 100%)', 
                  borderRadius: 'var(--radius-lg)', 
                  color: 'var(--success)', 
                  fontSize: '0.95rem', 
                  fontWeight: 700,
                  border: '1px solid rgba(16, 185, 129, 0.2)',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '0.75rem'
                }}>
                  <div style={{ width: '40px', height: '40px', background: 'var(--success)', color: 'white', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 10px rgba(16, 185, 129, 0.3)' }}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                  </div>
                  Nenhuma irregularidade detectada nesta operação.
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* History Table */}
      <div className="card" style={{ padding: '2.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '2.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{ padding: '0.75rem', background: 'var(--secondary-light)', color: 'var(--secondary)', borderRadius: '12px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
            </div>
            <h3 style={{ marginBottom: 0, fontSize: '1.25rem' }}>Histórico de Atividades</h3>
          </div>
          <button 
            onClick={exportToCSV} 
            className="btn-ghost" 
            disabled={history.length === 0}
            style={{ 
              fontSize: '0.85rem', 
              gap: '0.5rem', 
              background: 'var(--primary-light)', 
              color: 'var(--primary)',
              padding: '0.625rem 1.25rem' 
            }}
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
            Exportar CSV
          </button>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: '0 0.75rem' }}>
            <thead>
              <tr style={{ textAlign: 'left' }}>
                <th style={{ padding: '0 1rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', textTransform: 'uppercase' }}>Data/Hora</th>
                <th style={{ padding: '0 1rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', textTransform: 'uppercase' }}>Valor</th>
                <th style={{ padding: '0 1rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', textTransform: 'uppercase' }}>Local/Device</th>
                <th style={{ padding: '0 1rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', textTransform: 'uppercase' }}>Risco</th>
                <th style={{ padding: '0 1rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-soft)', textTransform: 'uppercase' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {history.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-soft)', fontWeight: 600 }}>
                    Nenhuma atividade registrada.
                  </td>
                </tr>
              ) : (
                history.map((item) => (
                  <tr key={item.id} className="history-row" style={{ background: 'rgba(255,255,255,0.4)', transition: 'all 0.2s' }}>
                    <td style={{ padding: '1rem', borderRadius: '12px 0 0 12px', border: '1px solid var(--border)', borderRight: 'none' }}>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>{new Date(item.timestamp).toLocaleDateString('pt-BR')}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-soft)' }}>{new Date(item.timestamp).toLocaleTimeString('pt-BR')}</div>
                    </td>
                    <td style={{ padding: '1rem', border: '1px solid var(--border)', borderLeft: 'none', borderRight: 'none', fontWeight: 800 }}>
                      R$ {item.amount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                    </td>
                    <td style={{ padding: '1rem', border: '1px solid var(--border)', borderLeft: 'none', borderRight: 'none' }}>
                      <div style={{ fontWeight: 600 }}>{item.location}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-soft)' }}>{item.device}</div>
                    </td>
                    <td style={{ padding: '1rem', border: '1px solid var(--border)', borderLeft: 'none', borderRight: 'none' }}>
                      <div style={{ 
                        width: '36px', 
                        height: '36px', 
                        borderRadius: '50%', 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'center', 
                        background: 'var(--bg-app)',
                        color: getScoreColor(item.riskScore),
                        fontWeight: 900,
                        fontSize: '0.85rem',
                        border: `2px solid ${getScoreColor(item.riskScore)}20`
                      }}>
                        {item.riskScore}
                      </div>
                    </td>
                    <td style={{ padding: '1rem', borderRadius: '0 12px 12px 0', border: '1px solid var(--border)', borderLeft: 'none' }}>
                      <span className={`badge ${getStatusBadgeClass(item.status)}`} style={{ fontSize: '0.65rem' }}>{item.status}</span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      
      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
        .history-row:hover {
          background: #fff !important;
          transform: scale(1.005);
          box-shadow: var(--shadow-sm);
        }
      `}</style>
    </div>
  );
};

export default Dashboard;
