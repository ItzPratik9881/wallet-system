import { NavLink } from "react-router-dom";
import "../styles/Sidebar.css";

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-icon">₹</div>

        <div>
          <h2>PayLedger</h2>
          <span>Wallet System</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className="nav-item">
          <span className="nav-icon">⌂</span>
          Dashboard
        </NavLink>

        <NavLink to="/deposit" className="nav-item">
          <span className="nav-icon">＋</span>
          Deposit
        </NavLink>

        <NavLink to="/withdraw" className="nav-item">
          <span className="nav-icon">−</span>
          Withdraw
        </NavLink>

        <NavLink to="/transfer" className="nav-item">
          <span className="nav-icon">⇄</span>
          Transfer
        </NavLink>

        <NavLink to="/history" className="nav-item">
          <span className="nav-icon">≡</span>
          Transaction History
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <p>Payment Ledger</p>
        <span>Secure wallet management</span>
      </div>
    </aside>
  );
}

export default Sidebar;