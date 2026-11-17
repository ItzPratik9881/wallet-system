import api from "./api";

export const getDashboard = async (email) => {
  const url = email ? `/dashboard/${encodeURIComponent(email)}` : "/dashboard";
  const response = await api.get(url);
  // Unwrap standardized ApiResponse envelope
  return response.data?.data || response.data;
};
