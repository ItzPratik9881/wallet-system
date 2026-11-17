import "../styles/TransactionCard.css";

function TransactionCard({ tx, transaction }) {
    // Support both 'tx' and 'transaction' props
    const item = tx || transaction || {};

    const type = item.type || "TRANSACTION";
    const status = item.status || "SUCCESS";
    const amount = item.amount != null ? item.amount : "0.00";
    const txId = item.transactionId || item.id || "-";
    const dateStr = item.createdAt ? new Date(item.createdAt).toLocaleString() : "-";

    const getIcon = () => {
        switch (type) {
            case "DEPOSIT":
                return "🟢";
            case "WITHDRAW":
                return "🔴";
            default:
                return "🔵";
        }
    };

    const getAmountColor = () => {
        switch (type) {
            case "DEPOSIT":
                return "green";
            case "WITHDRAW":
                return "red";
            default:
                return "blue";
        }
    };

    return (
        <div className="transaction-card">
            <div className="transaction-top">
                <div>
                    <h2>
                        {getIcon()} {type}
                    </h2>
                    <span className={`status ${String(status).toLowerCase()}`}>
                        {status}
                    </span>
                </div>
                <h1 className={getAmountColor()}>
                    ₹{amount}
                </h1>
            </div>

            <div className="transaction-body">
                <div>
                    <strong>Transaction ID</strong>
                    <p>#{txId}</p>
                </div>
                <div>
                    <strong>Date</strong>
                    <p>{dateStr}</p>
                </div>
                <div>
                    <strong>Sender Wallet</strong>
                    <p>{item.senderWalletId ?? "-"}</p>
                </div>
                <div>
                    <strong>Receiver Wallet</strong>
                    <p>{item.receiverWalletId ?? "-"}</p>
                </div>
            </div>
        </div>
    );
}

export default TransactionCard;
