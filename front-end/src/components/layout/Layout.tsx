import { Car, ChartNoAxesColumn, Database, LogOut, ParkingSquare, Settings, Settings2, Ticket, UserRound } from 'lucide-react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import client from '../../api/client'
import { useSession } from '../../auth/session'
import type { ResourceKey } from '../../auth/session'
import UserAvatar from '../UserAvatar'

const navigation = [
  { to: '/', label: 'Overview', icon: ChartNoAxesColumn, end: true },
  { to: '/records', label: 'Data registry', icon: Database },
  { to: '/vehicles', label: 'Vehicles', icon: Car },
  { to: '/slots', label: 'Parking slots', icon: ParkingSquare },
  { to: '/tickets', label: 'Tickets', icon: Ticket },
  { to: '/checkin', label: 'Check-in', icon: Settings2 },
  { to: '/admin', label: 'Users & roles', icon: UserRound },
  { to: '/profile', label: 'Profile settings', icon: Settings },
]

const Layout = () => {
  const { session, profile, loading, error, refresh, canAccess, hasAuthority } = useSession()
  const navigate = useNavigate()
  const user = profile?.displayName || session?.username || 'operator'
  const routeResources: Record<string, ResourceKey[]> = {
    '/vehicles': ['vehicles'], '/slots': ['parkingSlots'], '/tickets': ['parkingTickets'],
    '/admin': ['users', 'roles', 'privileges'],
    '/records': ['owners', 'ownerAadhaar', 'users', 'roles', 'privileges', 'addresses', 'licenses', 'vehicleTypes', 'vehicles', 'parkingSlots', 'parkingTickets'],
  }
  const visibleNavigation = navigation.filter(({ to }) => to === '/' || to === '/profile'
    || (to === '/checkin' ? hasAuthority('CHECK_IN_TICKETS', 'CREATE_PARKING_TICKETS', 'CHECK_OUT_TICKETS', 'UPDATE_PARKING_TICKETS', 'PAY_TICKETS', 'MANAGE_PARKING_TICKETS')
      : routeResources[to]?.some(canAccess)))

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

  if (loading) return <div className="legacy-page"><p>Loading your access permissions…</p></div>
  if (!session) return <div className="legacy-page"><p role="alert">{error}</p><button type="button" onClick={() => void refresh()}>Try again</button></div>

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <div className="sidebar-brand-row"><NavLink to="/" className="brand-lockup">
          <span className="brand-mark">SP</span>
          <span>
            <strong>Smart Parking</strong>
            <small>Control desk</small>
          </span>
        </NavLink>
        <NavLink to="/profile" className="mobile-profile-link" aria-label="Open profile settings"><UserAvatar /></NavLink></div>
        <nav className="primary-nav" aria-label="Main navigation">
          <span className="primary-nav__label">Workspace</span>
          {visibleNavigation.map(({ to, label, icon: Icon, end }) => (
            <NavLink to={to} end={end} key={to}>
              <Icon size={17} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <NavLink to="/profile" className="sidebar-profile-link" aria-label="Open profile settings">
          <UserAvatar />
          <div className="sidebar-user">
            <strong>{user}</strong>
            <span>{session.authorities.find((authority) => authority.startsWith('ROLE_'))?.slice(5) || 'Authenticated session'}</span>
          </div>
          </NavLink>
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
