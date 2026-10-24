import { useState } from 'react'
import client from '../api/client'

const CheckIn = () => {
  const [form, setForm] = useState<any>({})
  const [data, setData] = useState<any>(null)
  const [msg, setMsg] = useState('')

  const checkIn = async () => {
    try {
      const res = await client.post('/parking_tickets/check-in', form)
      setMsg('Checked in'); setData(res.data)
    } catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }
  const checkOut = async () => {
    try {
      const res = await client.post(`/parking_tickets/${form.ticketId}/check-out`)
      setMsg('Checked out'); setData(res.data)
    } catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }
  const pay = async () => {
    try {
      const res = await client.post(`/parking_tickets/${form.payTicketId}/pay`, { amount: form.amount })
      setMsg('Paid'); setData(res.data)
    } catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Check-In</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex flex-wrap gap-2">
        <input className="border p-2" placeholder="Vehicle ID" onChange={e => setForm({ ...form, vehicleId: e.target.value })} />
        <input className="border p-2" placeholder="Slot ID (optional)" onChange={e => setForm({ ...form, slotId: e.target.value })} />
        <button className="bg-yellow-500 text-black text-black p-2 rounded" onClick={checkIn}>Check In</button>
      </div>
      <h2 className="text-xl font-bold mb-4">Check-Out</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex gap-2">
        <input className="border p-2" placeholder="Ticket ID" onChange={e => setForm({ ...form, ticketId: e.target.value })} />
        <button className="bg-green-600 text-black p-2 rounded" onClick={checkOut}>Check Out</button>
      </div>
      <h2 className="text-xl font-bold mb-4">Payment</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex gap-2">
        <input className="border p-2" placeholder="Ticket ID" onChange={e => setForm({ ...form, payTicketId: e.target.value })} />
        <input className="border p-2" type="number" placeholder="Amount" onChange={e => setForm({ ...form, amount: Number(e.target.value) })} />
        <button className="bg-yellow-600 text-black p-2 rounded" onClick={pay}>Pay</button>
      </div>
      {msg && <p className="text-red-500">{msg}</p>}
      {data && <pre className="bg-white p-4 rounded shadow overflow-auto">{JSON.stringify(data, null, 2)}</pre>}
    </div>
  )
}


export default CheckIn
