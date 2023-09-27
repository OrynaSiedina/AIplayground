export interface LoginResponse {
  access_token: string;
  refresh_token: string;
}

export interface RegisterResponse {
  status: string;
}

export interface AuthorizationRequest {
  nickname?: string;
  password: string;
  email: string;
}
