import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import Transactions from './pages/Transactions';
import Merchants from './pages/Merchants';
import api from './services/api';

function App() {
  const [userInfo, setUserInfo] = useState({ name: '', email: '', roles: [] });

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await api.get('/api/v1/user/info');
        setUserInfo(response.data);
      } catch (error) {
        console.error('Error fetching user info:', error);
      }
    };

    fetchUserInfo();
  }, []);

  return (
    <Router>
      <Layout userInfo={userInfo}>
        <Routes>
          <Route path="/transactions" element={<Transactions />} />
          {userInfo.roles.includes('ROLE_ADMIN') && (
            <Route path="/merchants" element={<Merchants />} />
          )}
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
