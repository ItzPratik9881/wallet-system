import { useEffect, useState } from "react";
import { getDashboard } from "../../services/dashboardService";
import { useAuth } from "../../context/AuthContext";

import "./Dashboard.css";

import Header from "./Header";
import AnalyticsCards from "./AnalyticsCards";
import ChartSection from "./ChartSection";
import WalletInfo from "./WalletInfo";
import FinancialSummary from "./FinancialSummary";
import RecentTransactions from "./RecentTransactions";

const Dashboard = () => {

    const { user } = useAuth();

    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        if (!user?.email) {
            setLoading(false);
            return;
        }

        const fetchDashboard = async () => {

            try {

                const data = await getDashboard(user.email);

                setDashboard(data);

            } catch (error) {

                console.error(error);

            } finally {

                setLoading(false);

            }

        };

        fetchDashboard();

    }, [user]);

    if (loading) {

        return (
            <div className="loader">
                Loading Dashboard...
            </div>
        );

    }

    if (!dashboard) {

        return (
            <div className="loader">
                Unable to load Dashboard
            </div>
        );

    }

    return (

        <div className="dashboard-container">

            <Header dashboard={dashboard} />

            <AnalyticsCards dashboard={dashboard} />

            <div className="middle-section">

                <ChartSection dashboard={dashboard} />

                <div className="right-section">

                    <WalletInfo dashboard={dashboard} />

                    <FinancialSummary dashboard={dashboard} />

                </div>

            </div>

            <RecentTransactions dashboard={dashboard} />

        </div>

    );

};

export default Dashboard;