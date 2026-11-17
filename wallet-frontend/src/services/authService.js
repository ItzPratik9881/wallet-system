import api from "./api";

export const login = async (loginRequest) => {
  const response = await api.post("/auth/login", loginRequest);
  // Handle standardized ApiResponse envelope or direct payload
  const payload = response.data?.data || response.data;
  return payload;
};

export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("walletUserEmail");
};

export const saveUser = (data) => {
  if (data?.token) {
    localStorage.setItem("token", data.token);
  }
  if (data?.email) {
    localStorage.setItem("walletUserEmail", data.email);
  }
  localStorage.setItem("user", JSON.stringify(data));
};

export const getUser = () => {
  const user = localStorage.getItem("user");
  return user ? JSON.parse(user) : null;
};

export const isLoggedIn = () => {
  return localStorage.getItem("token") != null;
};
