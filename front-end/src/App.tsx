import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Vehicles from './pages/Vehicles'
import Slots from './pages/Slots'
import CheckIn from './pages/CheckIn'
import Tickets from './pages/Tickets'
import OwnerRegistration from './pages/OwnerRegistration'
import Admin from './pages/Admin'
import Records from './pages/Records'
import ProfileSettings from './pages/ProfileSettings'
import Layout from './components/layout/Layout'
import { SessionProvider } from './auth/SessionContext'
import './App.css'
import './index.css'

const isAuthed = () => !!localStorage.getItem('token')

const Protected = ({ children }: { children: React.ReactNode }) => {
  return isAuthed() ? <>{children}</> : <Navigate to="/login" replace />
}

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Protected><SessionProvider><Layout /></SessionProvider></Protected>}>
        <Route index element={<Dashboard />} />
        <Route path="vehicles" element={<Vehicles />} />
        <Route path="slots" element={<Slots />} />
        <Route path="tickets" element={<Tickets />} />
        <Route path="checkin" element={<CheckIn />} />
        <Route path="owner-registration" element={<OwnerRegistration />} />
        <Route path="records" element={<Records />} />
        <Route path="admin" element={<Admin />} />
        <Route path="profile" element={<ProfileSettings />} />
      </Route>
      <Route path="*" element={<Navigate to={isAuthed() ? '/' : '/login'} replace />} />
    </Routes>
  </BrowserRouter>
)

export default App
