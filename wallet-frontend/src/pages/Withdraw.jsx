import { useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import "../styles/Withdraw.css";

function Withdraw() {
  const { user } = useAuth();
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!amount || Number(amount) <= 0) {
      setError("Please enter a valid withdrawal amount greater than 0");
      return;
    }

    setLoading(true);
    setMessage("");
    setError("");

    try {
      const response = await api.post("/wallet/withdraw", {
        amount: amount,
      });

      const successMsg = response.data?.message || "Amount withdrawn successfully.";
      setMessage(successMsg);
      setAmount("");
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Withdrawal failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="withdraw-container">
      <div className="withdraw-card">
        <h2>Withdraw Funds</h2>
        <p className="subtitle">Withdraw money securely from your available balance</p>

        {user && (
          <div className="user-badge" style={{ marginBottom: "1.2rem", padding: "0.5rem 0.8rem", background: "rgba(99, 102, 241, 0.1)", borderRadius: "8px", fontSize: "0.9rem" }}>
            <span>Account: <strong>{user.email || user.fullName}</strong></span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <label>Amount</label>
          <input
            type="number"
            name="amount"
            placeholder="Enter amount (e.g. 50.00)"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            min="0.01"
            step="0.01"
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Processing Withdrawal..." : "Withdraw"}
          </button>
        </form>

        {message && <p className="success-msg" style={{ color: "#10B981", marginTop: "1rem", fontWeight: 500 }}>{message}</p>}
        {error && <p className="error-msg" style={{ color: "#EF4444", marginTop: "1rem", fontWeight: 500 }}>{error}</p>}
      </div>
    </div>
  );
}

export default Withdraw;
