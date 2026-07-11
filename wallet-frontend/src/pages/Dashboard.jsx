import { useEffect, useState } from "react";
import api from "../services/api";
import "../styles/Dashboard.css";

function Dashboard() {
  const [balance, setBalance] = useState(null);
  const [email, setEmail] = useState(
    localStorage.getItem("walletUserEmail") || ""
  );
  const [inputEmail, setInputEmail] = useState(email);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchBalance = async (userEmail) => {
    if (!userEmail.trim()) {
      setError("Please enter your registered email.");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const response = await api.get(
        `/wallet/balance/${encodeURIComponent(userEmail.trim())}`
      );

      setBalance(response.data);

      localStorage.setItem("walletUserEmail", userEmail.trim());
      setEmail(userEmail.trim());
    } catch (err) {
      console.error("Balance fetch error:", err);

      setBalance(null);

      setError(
        err.response?.data?.message ||
          "Unable to fetch wallet balance. Please check the email."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (email) {
      fetchBalance(email);
    }
  }, []);

  const handleSubmit = (event) => {
    event.preventDefault();
    fetchBalance(inputEmail);
  };

  return (
    <div className="dashboard-page">
      <div className="dashboard-heading">
        <p>PAYMENT LEDGER & WALLET SYSTEM</p>
        <h1>Dashboard</h1>
      </div>

      <div className="dashboard-grid">
        <div className="balance-card">
          <div className="balance-card-header">
            <div>
              <p>AVAILABLE BALANCE</p>

              <h2>
                {loading
                  ? "Loading..."
                  : balance !== null
                    ? `₹${Number(balance).toFixed(2)}`
                    : "₹0.00"}
              </h2>
            </div>

            <div className="wallet-icon">₹</div>
          </div>

          {email && (
            <p className="wallet-email">
              Wallet linked to: <strong>{email}</strong>
            </p>
          )}
        </div>

        <div className="account-card">
          <p>ACCOUNT STATUS</p>
          <h3>Active</h3>
          <span>Your wallet is ready for transactions.</span>
        </div>
      </div>

      <div className="balance-search-card">
        <h2>View Wallet Balance</h2>
        <p>Enter your registered email address to load your wallet.</p>

        <form onSubmit={handleSubmit} className="balance-form">
          <input
            type="email"
            placeholder="Enter registered email"
            value={inputEmail}
            onChange={(event) => setInputEmail(event.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Loading..." : "Check Balance"}
          </button>
        </form>

        {error && <div className="dashboard-error">{error}</div>}
      </div>
    </div>
  );
}

export default Dashboard;