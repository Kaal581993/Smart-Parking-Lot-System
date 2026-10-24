import { useState } from 'react'
import client from '../api/client'

const OwnerRegistration = () => {
  const [form, setForm] = useState({ username: '', password: '', ownerId: '' })
  const [msg, setMsg] = useState('')

  const submit = async () => {
    try {
      await client.post('/auth/register-owner', form)
      setMsg('Owner registered successfully')
      setForm({ username: '', password: '', ownerId: '' })
    } catch (e: any) {
      setMsg(e.response?.data?.error || 'Registration failed')
    }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Owner Registration</h2>
      <div className="bg-white p-4 rounded shadow mb-4 flex flex-wrap gap-2">
        <input className="border p-2" placeholder="Username" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} />
        <input className="border p-2" type="password" placeholder="Password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} />
        <input className="border p-2" placeholder="Owner ID (Aadhaar)" value={form.ownerId} onChange={e => setForm({ ...form, ownerId: e.target.value })} />
        <button className="bg-yellow-500 text-black p-2 rounded" onClick={submit}>Register Owner</button>
      </div>
      {msg && <p className={msg.includes('success') ? 'text-green-500' : 'text-red-500'}>{msg}</p>}
    </div>
  )
}

export default OwnerRegistration
