import {
    FaWallet,
    FaEnvelope,
    FaHeartbeat,
    FaCheckCircle
} from "react-icons/fa";

const WalletInfo = ({ dashboard }) => {

    return (

        <div className="info-card">

            <h2>Wallet Information</h2>

            <div className="info-row">

                <span>
                    <FaWallet /> Wallet ID
                </span>

                <strong>
                    {dashboard.walletId}
                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaEnvelope /> Email
                </span>

                <strong>
                    {dashboard.email}
                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaCheckCircle /> Status
                </span>

                <strong>
                    {dashboard.walletStatus}
                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaHeartbeat /> Health
                </span>

                <strong>
                    {dashboard.walletHealth}
                </strong>

            </div>

        </div>

    );

};

export default WalletInfo;