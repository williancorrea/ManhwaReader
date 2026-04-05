export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface GoogleLoginRequest {
  idToken: string;
}

export interface AuthResponse {
  accessToken: string;
  expiresIn: number;
  isNewUser: boolean | null;
}

export interface ApiErrorItem {
  key: string;
  message: string;
  detail: string | null;
}

export interface ApiError {
  origin: string;
  method: string;
  status: {
    code: number;
    description: string;
  };
  dateTime: string;
  items: ApiErrorItem[];
}
