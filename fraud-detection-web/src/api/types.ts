export type Role = 'ROLE_USER' | 'ROLE_ADMIN' | 'ROLE_ANALYST';
export type Device = 'MOBILE' | 'DESKTOP' | 'TABLET' | 'NEW_DEVICE' | 'UNKNOWN';
export type Status = 'APPROVED' | 'SUSPICIOUS' | 'BLOCKED';

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface UserResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserSummary;
}

export interface FraudLogResponse {
  reason: string;
  createdAt: string;
}

export interface TransactionRequest {
  amount: number;
  location: string;
  device: Device;
}

export interface TransactionResponse {
  id: number;
  amount: number;
  location: string;
  device: Device;
  timestamp: string;
  riskScore: number;
  status: Status;
  fraudLogs: FraudLogResponse[];
}
