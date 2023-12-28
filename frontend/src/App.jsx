import { Routes, Route } from 'react-router-dom';
import HomePage from "@/app/pages/HomePage"
import RootLayout from '@/app/Layout';
import StatementsPage from './app/pages/StatementsPage.jsx';
import TransactionsPage from "@/app/pages/TransactionsPage.jsx";

function App() {
  return (
    <>
      <RootLayout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/statements" element={<StatementsPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
        </Routes>
      </RootLayout>
    </>
  )
}

export default App
