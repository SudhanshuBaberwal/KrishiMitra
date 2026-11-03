
export type User = {
  id: string;
  name: string;
  email: string;
  role: "FARMER" | string;
};

export type AuthData = {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};

export type AuthResponse = ApiResponse<AuthData>;

export type LoginInput = {
  email: string;
  password: string;
};

export type RegisterInput = {
  name: string;
  email: string;
  password: string;
};
