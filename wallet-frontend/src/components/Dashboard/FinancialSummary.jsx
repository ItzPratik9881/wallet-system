import {
    FaMoneyBillWave,
    FaArrowCircleDown,
    FaArrowCircleUp,
    FaExchangeAlt
} from "react-icons/fa";

const FinancialSummary = ({ dashboard }) => {

    return (

        <div className="info-card">

            <h2>Financial Summary</h2>

            <div className="info-row">

                <span>
                    <FaArrowCircleDown /> Total Received
                </span>

                <strong>

                    ₹ {dashboard.totalMoneyReceived}

                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaArrowCircleUp /> Total Sent
                </span>

                <strong>

                    ₹ {dashboard.totalMoneySent}

                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaExchangeAlt /> Transactions
                </span>

                <strong>

                    {dashboard.totalTransactions}

                </strong>

            </div>

            <div className="info-row">

                <span>
                    <FaMoneyBillWave /> Current Balance
                </span>

                <strong>

                    ₹ {dashboard.balance}

                </strong>

            </div>

        </div>

    );

};

export default FinancialSummary;