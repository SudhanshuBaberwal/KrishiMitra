import {
  AuthResponse,
  LoginInput,
  RegisterInput,
  User,
  ApiResponse,
} from "@/types/auth";
import api from "../client";

export async function loginUser(input: LoginInput) {
  const response = await api.post<AuthResponse>(
    "/api/auth/login",
    input
  );

  return response.data.data;
}

export async function registerUser(input: RegisterInput) {
  const response = await api.post<AuthResponse>(
    "/api/auth/register",
    input
  );

  return response.data.data;
}

export async function getCurrentUser() {
  const response = await api.get<ApiResponse<User>>("/api/auth/me");
  return response.data.data;
}

export async function logoutUser() {
  const response = await api.post("/api/auth/logout");
  return response.data;
}
