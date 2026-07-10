import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";

import Dashboard from "./pages/Dashboard";
import Deposit from "./pages/Deposit";
import Register from "./pages/Register";
import TransactionHistory from "./pages/TransactionHistory";
import Transfer from "./pages/Transfer";
import Withdraw from "./pages/Withdraw";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/register" element={<Register />} />

        <Route
          path="/*"
          element={
            <div className="app-layout">
              <Sidebar />

              <div className="main-content">
                <Navbar />

                <main className="page-content">
                  <Routes>
                    <Route
                      path="/"
                      element={<Navigate to="/dashboard" replace />}
                    />

                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/deposit" element={<Deposit />} />
                    <Route path="/withdraw" element={<Withdraw />} />
                    <Route path="/transfer" element={<Transfer />} />
                    <Route path="/history" element={<TransactionHistory />} />
                  </Routes>
                </main>
              </div>
            </div>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;