import { useState } from 'react'
import client from '../api/client'

const Tickets = () => {
  const [ticketId, setTicketId] = useState('')
  const [data, setData] = useState<any>(null)
  const [msg, setMsg] = useState('')

  const load = async () => {
    try {
      const res = await client.get(`/parking_tickets/${ticketId}`)
      setData(res.data)
    } catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Tickets</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex gap-2">
        <input className="border p-2" placeholder="Ticket ID" value={ticketId} onChange={e => setTicketId(e.target.value)} />
        <button className="bg-gray-200 p-2 rounded" onClick={load}>Load</button>
      </div>
      {msg && <p className="text-red-500">{msg}</p>}
      {data && <pre className="bg-white p-4 rounded shadow overflow-auto">{JSON.stringify(data, null, 2)}</pre>}
    </div>
  )
}


export default Tickets
