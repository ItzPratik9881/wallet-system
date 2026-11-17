import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request Interceptor: Attach JWT Token & Idempotency Key
api.interceptors.request.use(
  (config) => {
    const isPublicAuthRoute =
      config.url?.includes("/auth/") ||
      config.url?.includes("/users/register");

    const token = localStorage.getItem("token");
    if (token && !isPublicAuthRoute) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Attach Idempotency-Key for mutating financial requests if not already set
    if (["post", "put", "patch"].includes(config.method?.toLowerCase()) && !config.headers["Idempotency-Key"]) {
      // Generate a client-side UUIDv4 idempotency key
      const idempotencyKey = "idemp-" + Math.random().toString(36).substring(2, 15) + Date.now().toString(36);
      config.headers["Idempotency-Key"] = idempotencyKey;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle Token Expiration & Unified Envelope
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear expired / invalid credentials and redirect to login
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("walletUserEmail");
      if (window.location.pathname !== "/login" && window.location.pathname !== "/register") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
