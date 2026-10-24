import { Car, ChartNoAxesColumn, Database, LogOut, ParkingSquare, Settings2, Ticket, UserRound } from 'lucide-react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import client from '../../api/client'

const navigation = [
  { to: '/', label: 'Overview', icon: ChartNoAxesColumn, end: true },
  { to: '/records', label: 'Data registry', icon: Database },
  { to: '/vehicles', label: 'Vehicles', icon: Car },
  { to: '/slots', label: 'Parking slots', icon: ParkingSquare },
  { to: '/tickets', label: 'Tickets', icon: Ticket },
  { to: '/checkin', label: 'Check-in', icon: Settings2 },
  { to: '/admin', label: 'Users & roles', icon: UserRound },
]

const Layout = () => {
  const navigate = useNavigate()
  const user = localStorage.getItem('user') || 'operator'
  const initials = user.slice(0, 2).toUpperCase()

  const logout = async () => {
    try {
      await client.post('/auth/logout')
    } catch {
      // Clear the local session even if the server token has already expired.
    }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <NavLink to="/" className="brand-lockup">
          <span className="brand-mark">SP</span>
          <span>
            <strong>Smart Parking</strong>
            <small>Control desk</small>
          </span>
        </NavLink>
        <nav className="primary-nav" aria-label="Main navigation">
          <span className="primary-nav__label">Workspace</span>
          {navigation.map(({ to, label, icon: Icon, end }) => (
            <NavLink to={to} end={end} key={to}>
              <Icon size={17} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="avatar">{initials}</span>
          <div className="sidebar-user">
            <strong>{user}</strong>
            <span>Authenticated session</span>
          </div>
          <button className="logout-button" type="button" onClick={() => void logout()} aria-label="Log out" title="Log out">
            <LogOut size={15} />
          </button>
        </div>
      </aside>
      <main className="app-main"><Outlet /></main>
    </div>
  )
}

export default Layout
