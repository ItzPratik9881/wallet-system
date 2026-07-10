import { useLocation } from "react-router-dom";
import "../styles/Navbar.css";

const pageTitles = {
  "/dashboard": "Dashboard",
  "/deposit": "Deposit Money",
  "/withdraw": "Withdraw Money",
  "/transfer": "Transfer Money",
  "/history": "Transaction History",
};

function Navbar() {
  const location = useLocation();

  const title = pageTitles[location.pathname] || "Payment Ledger";

  return (
    <header className="navbar">
      <div>
        <p className="navbar-label">PAYMENT LEDGER & WALLET SYSTEM</p>
        <h1>{title}</h1>
      </div>

      <div className="navbar-user">
        <div className="user-avatar">U</div>

        <div>
          <strong>Wallet User</strong>
          <span>Secure Account</span>
        </div>
      </div>
    </header>
  );
}

export default Navbar;