import { useState } from 'react'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Vehicles from './pages/Vehicles'
import Slots from './pages/Slots'
import CheckIn from './pages/CheckIn'
import Tickets from './pages/Tickets'
import OwnerRegistration from './pages/OwnerRegistration'
import NotificationPanel from './components/NotificationPanel'
import './App.css'
import './index.css'

type View = 'login' | 'dashboard' | 'vehicles' | 'slots' | 'tickets' | 'checkin' | 'owner-registration'

const App = () => {
  const [view, setView] = useState<View>(localStorage.getItem('token') ? 'dashboard' : 'login')
  const [user, setUser] = useState(localStorage.getItem('user') || '')

  const onLogin = (u: string) => { setUser(u); setView('dashboard') }
  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser('')
    setView('login')
  }

  if (view === 'login') return <Login onLogin={onLogin} />

  const pages: any = {
    login: <Login onLogin={onLogin} />,
    dashboard: <Dashboard />,
    vehicles: <Vehicles />,
    slots: <Slots />,
    tickets: <Tickets />,
    checkin: <CheckIn />,
    'owner-registration': <OwnerRegistration />,
  }

  return (
    <div className="min-h-screen bg-black">
      <nav className="bg-yellow-500 text-black text-black p-4 flex gap-4">
        <span className="font-bold">Smart Parking</span>
        <button onClick={() => setView('dashboard')}>Dashboard</button>
        <button onClick={() => setView('vehicles')}>Vehicles</button>
        <button onClick={() => setView('slots')}>Slots</button>
        <button onClick={() => setView('tickets')}>Tickets</button>
        <button onClick={() => setView('checkin')}>Check-In</button>
        <button onClick={() => setView('owner-registration')}>Owner Registration</button>
        <span className="ml-auto">{user}</span>
        <button onClick={logout}>Logout</button>
      </nav>
      <div className="p-6">{pages[view]}</div>
      {<NotificationPanel />}
    </div>
  )
}


export default App
