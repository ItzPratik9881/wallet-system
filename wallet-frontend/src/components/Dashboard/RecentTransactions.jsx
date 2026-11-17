const RecentTransactions = ({ dashboard }) => {

    const formatDate = (date) => {

        return new Date(date).toLocaleString("en-IN", {

            day: "2-digit",

            month: "short",

            year: "numeric",

            hour: "2-digit",

            minute: "2-digit"

        });

    };

    return (

        <div className="recent-card">

            <h2>

                Recent Transactions

            </h2>

            <table className="transaction-table">

                <thead>

                    <tr>

                        <th>Type</th>

                        <th>Amount</th>

                        <th>Status</th>

                        <th>Sender</th>

                        <th>Receiver</th>

                        <th>Date</th>

                    </tr>

                </thead>

                <tbody>

                    {

                        dashboard.recentTransactions.length > 0 ?

                        dashboard.recentTransactions.map((transaction,index)=>(

                            <tr key={index}>

                                <td>

                                    {transaction.type}

                                </td>

                                <td>

                                    ₹ {transaction.amount}

                                </td>

                                <td>

                                    <span className={`status ${transaction.status.toLowerCase()}`}>

                                        {transaction.status}

                                    </span>

                                </td>

                                <td>

                                    {transaction.senderWalletId ?? "-"}

                                </td>

                                <td>

                                    {transaction.receiverWalletId ?? "-"}

                                </td>

                                <td>

                                    {formatDate(transaction.createdAt)}

                                </td>

                            </tr>

                        ))

                        :

                        <tr>

                            <td colSpan="6">

                                No Transactions Found

                            </td>

                        </tr>

                    }

                </tbody>

            </table>

        </div>

    );

};

export default RecentTransactions;