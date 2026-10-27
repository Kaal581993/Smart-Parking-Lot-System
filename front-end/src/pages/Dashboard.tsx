import { Activity, ArrowUpRight, Car, Database, ParkingSquare, Ticket } from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import client from '../api/client'
import { useSession } from '../auth/session'

const Dashboard = () => {
  const { session, can, canAccess, hasAuthority } = useSession()
  const [data, setData] = useState<unknown>(null)
  const [msg, setMsg] = useState('')
  const navigate = useNavigate()

  const load = async (endpoint: string) => {
    try {
      const response = await client.get(endpoint)
      setData(response.data)
      setMsg('')
    } catch (error: any) {
      setMsg(error.response?.data?.error || error.response?.data?.message || 'Load failed')
    }
  }

  return (
    <div className="legacy-page">
      <div className="page-intro">
        <div>
          <p className="eyebrow">Operations / overview</p>
          <h1>Good to see you.</h1>
          <p>Use the registry to manage the records behind the lot, or jump into a live operational view.</p>
        </div>
        {Object.values(session?.permissions ?? {}).some((actions) => Object.values(actions).some(Boolean)) ? <button className="button button--primary" type="button" onClick={() => navigate('/records')}><Database size={16} /> Open data registry</button> : null}
      </div>
      <div className="overview-grid">
        {canAccess('vehicles') ? <button className="overview-card" type="button" onClick={() => navigate('/vehicles')}>
          <Car size={18} />
          <span><strong>Fleet</strong><small>Vehicles and vehicle types</small></span>
          <ArrowUpRight size={15} />
        </button> : null}
        {canAccess('parkingSlots') ? <button className="overview-card" type="button" onClick={() => navigate('/slots')}>
          <ParkingSquare size={18} />
          <span><strong>Capacity</strong><small>Slots, floors, and availability</small></span>
          <ArrowUpRight size={15} />
        </button> : null}
        {canAccess('parkingTickets') ? <button className="overview-card" type="button" onClick={() => navigate('/tickets')}>
          <Ticket size={18} />
          <span><strong>Ticket lifecycle</strong><small>Parking tickets and payments</small></span>
          <ArrowUpRight size={15} />
        </button> : null}
        {(['users', 'roles', 'privileges'] as const).some(canAccess) ? <button className="overview-card" type="button" onClick={() => navigate('/admin')}>
          <Activity size={18} />
          <span><strong>Access</strong><small>Users, roles, and privileges</small></span>
          <ArrowUpRight size={15} />
        </button> : null}
      </div>
      <div className="legacy-card">
        <div className="section-heading"><div><p className="eyebrow">Live checks</p><h2>Parking signals</h2></div></div>
        <div className="quick-actions">
          {can('parkingSlots', 'read') ? <button className="button button--ghost" type="button" onClick={() => void load('/parking_slots?available=true')}>Load available slots</button> : null}
          {hasAuthority('SCAN_OVERSTAY', 'READ_PARKING_TICKETS', 'READ_PARKING_SLOTS', 'READ_SLOTS', 'MANAGE_SLOTS') ? <button className="button button--ghost" type="button" onClick={() => void load('/parking_slots/scan-overstay')}>Scan overstays</button> : null}
        </div>
        {msg ? <div className="form-error">{msg}</div> : null}
        {data ? <pre className="json-panel">{JSON.stringify(data, null, 2)}</pre> : <p className="muted-copy">Choose a check to inspect current parking signals.</p>}
      </div>
    </div>
  )
}

export default Dashboard
