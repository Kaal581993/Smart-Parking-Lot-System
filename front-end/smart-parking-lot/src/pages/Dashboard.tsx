import { useState } from 'react'
import client from '../api/client'

const Dashboard = () => {
  const [data, setData] = useState<any>(null)
  const [msg, setMsg] = useState('')

  const load = async (endpoint: string) => {
    try {
      const res = await client.get(endpoint)
      setData(res.data)
      setMsg('')
    } catch (e: any) {
      setMsg(e.response?.data?.error || 'Load failed')
    }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Dashboard</h2>
      <div className="flex gap-4 mb-4">
        <button className="bg-white p-4 rounded shadow" onClick={() => load('/parking_slots?available=true')}>Available Slots</button>
        <button className="bg-white p-4 rounded shadow" onClick={() => load('/parking_slots/scan-overstay')}>Overstayed</button>
      </div>
      {msg && <p className="text-red-500">{msg}</p>}
      {data && <pre className="bg-white p-4 rounded shadow overflow-auto">{JSON.stringify(data, null, 2)}</pre>}
    </div>
  )
}


export default Dashboard
