import { useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../services/api";
import "../styles/Register.css";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
    phoneNumber: "",
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
      const response = await api.post("/users/register", formData);

      localStorage.setItem("walletUserEmail", formData.email);
      localStorage.setItem("walletUserName", formData.fullName);

      const successMessage =
        typeof response.data === "string"
          ? response.data
          : response.data?.message || "User registered successfully!";

      setMessage(successMessage);

      setTimeout(() => {
        navigate("/dashboard");
      }, 1000);
    } catch (err) {
      const responseData = err.response?.data;

      let errorMessage = "Registration failed. Please try again.";

      if (typeof responseData === "string") {
        errorMessage = responseData;
      } else if (responseData?.message) {
        errorMessage = responseData.message;
      } else if (responseData && typeof responseData === "object") {
        errorMessage = Object.values(responseData).join(" ");
      }

      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-container">

        <section className="register-brand-section">
          <div className="register-brand-content">

            <div className="register-brand-icon">₹</div>

            <h1>PayLedger</h1>

            <p className="register-brand-description">
              A secure payment ledger and wallet management system built with
              Spring Boot and React.
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

        <section className="register-form-section">
          <div className="register-form-wrapper">

            <div className="register-header">
              <span className="register-label">CREATE YOUR WALLET</span>

              <h2>Create Account</h2>

              <p>
                Enter your details to create your PayLedger wallet.
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

            <form className="register-form" onSubmit={handleSubmit}>

              <div className="register-form-group">
                <label htmlFor="fullName">Full Name</label>

                <input
                  id="fullName"
                  type="text"
                  name="fullName"
                  placeholder="Enter your full name"
                  value={formData.fullName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="register-form-group">
                <label htmlFor="email">Email Address</label>

                <input
                  id="email"
                  type="email"
                  name="email"
                  placeholder="Enter your email address"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="register-form-group">
                <label htmlFor="phoneNumber">Phone Number</label>

                <input
                  id="phoneNumber"
                  type="tel"
                  name="phoneNumber"
                  placeholder="Enter your phone number"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="register-form-group">
                <label htmlFor="password">Password</label>

                <input
                  id="password"
                  type="password"
                  name="password"
                  placeholder="Create a password"
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
                {loading ? "Creating Account..." : "Create Account"}
              </button>

            </form>

            <p className="register-footer-text">
              Secure wallet management powered by Spring Boot
            </p>

          </div>
        </section>

      </div>
    </div>
  );
}

export default Register;