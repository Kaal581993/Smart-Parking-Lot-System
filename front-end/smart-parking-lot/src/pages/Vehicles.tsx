import { useState } from 'react'
import client from '../api/client'

const Vehicles = () => {
  const [form, setForm] = useState<any>({})
  const [data, setData] = useState<any>(null)
  const [msg, setMsg] = useState('')

  const submit = async () => {
    try {
      const res = await client.post('/vehicles', form)
      setMsg('Created')
      setData(res.data)
    } catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }
  const load = async () => {
    try { const res = await client.get('/vehicles'); setData(res.data) }
    catch (e: any) { setMsg(e.response?.data?.error || 'Failed') }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Vehicles</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex flex-wrap gap-2">
        <input className="border p-2" placeholder="Number Plate" onChange={e => setForm({ ...form, numberPlate: e.target.value })} />
        <input className="border p-2" placeholder="Vehicle Type ID" onChange={e => setForm({ ...form, vehicleType: e.target.value })} />
        <input className="border p-2" placeholder="Brand" onChange={e => setForm({ ...form, vehicleBrand: e.target.value })} />
        <input className="border p-2" placeholder="Model" onChange={e => setForm({ ...form, vehicleModel: e.target.value })} />
        <button className="bg-yellow-500 text-black text-black p-2 rounded" onClick={submit}>Add</button>
      </div>
      <button className="bg-gray-200 p-2 rounded mb-4" onClick={load}>Load</button>
      {msg && <p className="text-red-500">{msg}</p>}
      {data && <pre className="bg-white p-4 rounded shadow overflow-auto">{JSON.stringify(data, null, 2)}</pre>}
    </div>
  )
}


export default Vehicles
