import { useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import "../styles/Transfer.css";

function Transfer() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    receiverEmail: "",
    amount: "",
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.receiverEmail.trim()) {
      setError("Please provide a valid recipient email.");
      return;
    }

    if (!formData.amount || Number(formData.amount) <= 0) {
      setError("Please enter a valid transfer amount greater than 0.");
      return;
    }

    if (user?.email && user.email.toLowerCase() === formData.receiverEmail.trim().toLowerCase()) {
      setError("You cannot transfer money to your own account.");
      return;
    }

    setLoading(true);
    setMessage("");
    setError("");

    try {
      const response = await api.post("/wallet/transfer", {
        receiverEmail: formData.receiverEmail.trim(),
        amount: formData.amount,
      });

      const successMsg = response.data?.message || "Amount transferred successfully.";
      setMessage(successMsg);

      setFormData({
        receiverEmail: "",
        amount: "",
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Transfer failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="transfer-container">
      <div className="transfer-card">
        <h2>Transfer Money</h2>
        <p className="subtitle">Instant and secure peer-to-peer wallet transfer</p>

        {user && (
          <div className="user-badge" style={{ marginBottom: "1.2rem", padding: "0.5rem 0.8rem", background: "rgba(99, 102, 241, 0.1)", borderRadius: "8px", fontSize: "0.9rem" }}>
            <span>From (Sender): <strong>{user.email || user.fullName}</strong></span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <label>Recipient Email</label>
          <input
            type="email"
            name="receiverEmail"
            value={formData.receiverEmail}
            onChange={handleChange}
            placeholder="Enter recipient's registered email"
            required
          />

          <label>Amount</label>
          <input
            type="number"
            name="amount"
            value={formData.amount}
            onChange={handleChange}
            placeholder="Enter amount to transfer"
            min="0.01"
            step="0.01"
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Processing Transfer..." : "Transfer Now"}
          </button>
        </form>

        {message && <p className="success-msg" style={{ color: "#10B981", marginTop: "1rem", fontWeight: 500 }}>{message}</p>}
        {error && <p className="error-msg" style={{ color: "#EF4444", marginTop: "1rem", fontWeight: 500 }}>{error}</p>}
      </div>
    </div>
  );
}

export default Transfer;
