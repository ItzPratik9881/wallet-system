import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import SummaryCard from "../components/SummaryCard";
import TransactionCard from "../components/TransactionCard";
import "../styles/TransactionHistory.css";

function TransactionHistory() {
  const { user } = useAuth();
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadHistory = async (pageNumber = 0) => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get(
        `/wallet/history?page=${pageNumber}&size=10&sortBy=createdAt&direction=DESC`
      );
      const payload = response.data?.data || response.data;

      if (payload && Array.isArray(payload.content)) {
        // Standard PagedResponse
        setTransactions(payload.content);
        setTotalPages(payload.totalPages || 1);
        setTotalElements(payload.totalElements || payload.content.length);
        setPage(payload.page || 0);
      } else if (Array.isArray(payload)) {
        // Flat list fallback
        setTransactions(payload);
        setTotalPages(1);
        setTotalElements(payload.length);
        setPage(0);
      } else {
        setTransactions([]);
      }
    } catch (err) {
      console.warn("Paginated history request failed, attempting fallback:", err);
      const fallbackEmail = user?.email || localStorage.getItem("walletUserEmail");

      if (fallbackEmail) {
        try {
          const fallbackRes = await api.get(
            `/wallet/history/${encodeURIComponent(fallbackEmail)}`
          );
          const fallbackPayload = fallbackRes.data?.data || fallbackRes.data;

          if (Array.isArray(fallbackPayload)) {
            setTransactions(fallbackPayload);
            setTotalPages(1);
            setTotalElements(fallbackPayload.length);
            setPage(0);
            return;
          }
        } catch (fallbackErr) {
          console.error("Fallback history request also failed:", fallbackErr);
        }
      }

      setTransactions([]);
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Unable to load transaction history."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory(0);
  }, [user]);

  const deposits = transactions.filter((t) => t.type === "DEPOSIT").length;
  const withdraws = transactions.filter((t) => t.type === "WITHDRAW").length;
  const transfers = transactions.filter((t) => t.type === "TRANSFER").length;

  return (
    <div className="history-page">
      <div className="history-title">
        <h1>Transaction History & Ledger</h1>
        <p>Complete audit log of all financial events recorded in your wallet</p>
        {user && (
          <div style={{ marginTop: "0.5rem", color: "#6B7280", fontSize: "0.95rem" }}>
            Viewing records for: <strong>{user.email || user.fullName}</strong>
          </div>
        )}
      </div>

      {loading && <div className="loading">Loading Transactions...</div>}
      {error && <div className="error">{error}</div>}

      {!loading && transactions.length > 0 && (
        <>
          <div className="summary-grid">
            <SummaryCard
              title="Page Records"
              value={transactions.length}
              color="blue"
            />
            <SummaryCard
              title="Total Records"
              value={totalElements}
              color="blue"
            />
            <SummaryCard
              title="Deposits"
              value={deposits}
              color="green"
            />
            <SummaryCard
              title="Withdrawals"
              value={withdraws}
              color="red"
            />
            <SummaryCard
              title="Transfers"
              value={transfers}
              color="orange"
            />
          </div>

          <div
            className="recent-title"
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <h2>Activity Feed</h2>
            <span style={{ fontSize: "0.9rem", color: "#6B7280" }}>
              Page {page + 1} of {Math.max(1, totalPages)}
            </span>
          </div>

          {transactions.map((tx) => (
            <TransactionCard
              key={tx.transactionId || tx.id}
              tx={tx}
              transaction={tx}
            />
          ))}

          {/* Pagination Controls */}
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: "1rem",
              marginTop: "2rem",
              marginBottom: "2rem",
            }}
          >
            <button
              onClick={() => loadHistory(page - 1)}
              disabled={page === 0 || loading}
              style={{
                padding: "0.6rem 1.2rem",
                borderRadius: "8px",
                border: "1px solid #D1D5DB",
                background: page === 0 ? "#F3F4F6" : "#FFFFFF",
                cursor: page === 0 ? "not-allowed" : "pointer",
                fontWeight: 500,
              }}
            >
              Previous
            </button>

            <span style={{ fontWeight: 600, color: "#374151" }}>
              {page + 1} / {Math.max(1, totalPages)}
            </span>

            <button
              onClick={() => loadHistory(page + 1)}
              disabled={page >= totalPages - 1 || loading}
              style={{
                padding: "0.6rem 1.2rem",
                borderRadius: "8px",
                border: "1px solid #D1D5DB",
                background: page >= totalPages - 1 ? "#F3F4F6" : "#FFFFFF",
                cursor: page >= totalPages - 1 ? "not-allowed" : "pointer",
                fontWeight: 500,
              }}
            >
              Next
            </button>
          </div>
        </>
      )}

      {!loading && transactions.length === 0 && !error && (
        <div
          style={{
            textAlign: "center",
            padding: "3rem",
            background: "#FFFFFF",
            borderRadius: "12px",
            border: "1px solid #E5E7EB",
            marginTop: "2rem",
          }}
        >
          <p style={{ fontSize: "1.1rem", color: "#6B7280" }}>
            No transactions found for this wallet yet.
          </p>
        </div>
      )}
    </div>
  );
}

export default TransactionHistory;
