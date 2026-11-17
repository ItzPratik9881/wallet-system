import { FaWallet } from "react-icons/fa";

const Header = ({ dashboard }) => {
    const isStatusActive = String(dashboard?.walletStatus).toUpperCase() === "ACTIVE";

    return (
        <div className="dashboard-header">
            <div>
                <span className="dashboard-tag">
                    PAYMENT LEDGER SYSTEM
                </span>
                <h1>
                    {dashboard.greeting}, {dashboard.userName} 👋
                </h1>
                <p>
                    Welcome back. Here's your wallet overview.
                </p>
            </div>

            <div className="wallet-status">
                <div className="wallet-status-icon-box">
                    <FaWallet className="wallet-icon" />
                </div>
                <div className="wallet-status-details">
                    <span className="wallet-status-label">Wallet Status</span>
                    <div className="wallet-status-badge">
                        <span className={`status-live-dot ${isStatusActive ? "active" : "inactive"}`}></span>
                        <h3>{dashboard.walletStatus}</h3>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Header;
