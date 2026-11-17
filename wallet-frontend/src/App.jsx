import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Dashboard from "./components/Dashboard/Dashboard";

import Register from "./pages/Register";
import Login from "./pages/Login";

import Deposit from "./pages/Deposit";
import Withdraw from "./pages/Withdraw";
import Transfer from "./pages/Transfer";
import TransactionHistory from "./pages/TransactionHistory";

import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>

      <Routes>

        {/* Public Routes */}

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        {/* Protected Routes */}

        <Route
          path="/*"
          element={
            <ProtectedRoute>

              <div className="app-layout">

                <Sidebar />

                <div className="main-content">

                  <Navbar />

                  <main className="page-content">

                    <Routes>

                      <Route
                        path="/"
                        element={
                          <Navigate
                            to="/dashboard"
                            replace
                          />
                        }
                      />

                      <Route
                        path="/dashboard"
                        element={<Dashboard />}
                      />

                      <Route
                        path="/deposit"
                        element={<Deposit />}
                      />

                      <Route
                        path="/withdraw"
                        element={<Withdraw />}
                      />

                      <Route
                        path="/transfer"
                        element={<Transfer />}
                      />

                      <Route
                        path="/history"
                        element={<TransactionHistory />}
                      />

                    </Routes>

                  </main>

                </div>

              </div>

            </ProtectedRoute>
          }
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;