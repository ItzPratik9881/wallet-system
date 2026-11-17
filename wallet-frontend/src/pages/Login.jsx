import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../context/AuthContext";
import "../styles/Register.css";

function Login() {
  const navigate = useNavigate();

  const { login } = useAuth();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      await login(formData);

      setMessage("Login Successful!");

      setTimeout(() => {
        navigate("/dashboard");
      }, 800);

    } catch (err) {
      const responseData = err.response?.data;

      let errorMessage = "Invalid Email or Password.";

      if (typeof responseData === "string") {
        errorMessage = responseData;
      } else if (responseData?.message) {
        errorMessage = responseData.message;
      }

      setError(errorMessage);

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-container">

        {/* Left Section */}

        <section className="register-brand-section">

          <div className="register-brand-content">

            <div className="register-brand-icon">₹</div>

            <h1>PayLedger</h1>

            <p className="register-brand-description">
              A secure payment ledger and wallet management system built with
              Spring Boot, React and JWT Authentication.
            </p>

            <div className="register-features">

              <div className="register-feature">
                <span className="register-check">✓</span>
                <span>Secure wallet management</span>
              </div>

              <div className="register-feature">
                <span className="register-check">✓</span>
                <span>Deposit and withdraw funds</span>
              </div>

              <div className="register-feature">
                <span className="register-check">✓</span>
                <span>Transfer money between wallets</span>
              </div>

              <div className="register-feature">
                <span className="register-check">✓</span>
                <span>Complete transaction history</span>
              </div>

            </div>

          </div>

        </section>

        {/* Right Section */}

        <section className="register-form-section">

          <div className="register-form-wrapper">

            <div className="register-header">

              <span className="register-label">
                WELCOME BACK
              </span>

              <h2>Login</h2>

              <p>
                Login to your PayLedger wallet.
              </p>

            </div>

            {message && (
              <div className="register-alert register-alert-success">
                <span>✓</span>
                <p>{message}</p>
              </div>
            )}

            {error && (
              <div className="register-alert register-alert-error">
                <span>!</span>
                <p>{error}</p>
              </div>
            )}

            <form
              className="register-form"
              onSubmit={handleSubmit}
            >

              <div className="register-form-group">

                <label>Email Address</label>

                <input
                  type="email"
                  name="email"
                  placeholder="Enter your email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />

              </div>

              <div className="register-form-group">

                <label>Password</label>

                <input
                  type="password"
                  name="password"
                  placeholder="Enter your password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                />

              </div>

              <button
                type="submit"
                className="register-button"
                disabled={loading}
              >
                {loading ? "Signing In..." : "Login"}
              </button>

            </form>

            <p className="register-footer-text">

              Don't have an account?{" "}

              <Link
                to="/register"
                style={{
                  color: "#2563eb",
                  textDecoration: "none",
                  fontWeight: "600"
                }}
              >
                Create Account
              </Link>

            </p>

          </div>

        </section>

      </div>
    </div>
  );
}

export default Login;