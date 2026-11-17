import { useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
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

  const { user } = useAuth();

  const title = pageTitles[location.pathname] || "Payment Ledger";

  const userName = user?.fullName || "Wallet User";

  const avatarLetter = userName.charAt(0).toUpperCase();

  return (
    <header className="navbar">
      <div>
        <p className="navbar-label">PAYMENT LEDGER & WALLET SYSTEM</p>
        <h1>{title}</h1>
      </div>

      <div className="navbar-user">
        <div className="user-avatar">
          {avatarLetter}
        </div>

        <div>
          <strong>{userName}</strong>
          <span>Secure Account</span>
        </div>
      </div>
    </header>
  );
}

export default Navbar;