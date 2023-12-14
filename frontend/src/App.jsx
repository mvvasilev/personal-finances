import { Routes, Route } from 'react-router-dom';
import HomePage from "@/app/pages/HomePage"
import RootLayout from '@/app/Layout';
import TransactionsPage from './app/pages/TransactionsPage';

function App() {
  return (
    <>
      <RootLayout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
        </Routes>
      </RootLayout>
    </>
  )
}

export default App
