import { Routes, Route } from 'react-router-dom';
import RootLayout from '@/app/Layout';
import StatisticsPage from "@/app/pages/StatisticsPage.jsx"
import StatementsPage from '@/app/pages/StatementsPage.jsx';
import TransactionsPage from "@/app/pages/TransactionsPage.jsx";
import CategoriesPage from "@/app/pages/CategoriesPage.jsx";

function App() {
  return (
    <>
      <RootLayout>
        <Routes>
          <Route path="/" element={<StatisticsPage />} />
          <Route path="/statements" element={<StatementsPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
          <Route path="/categories" element={<CategoriesPage />} />
        </Routes>
      </RootLayout>
    </>
  )
}

export default App
