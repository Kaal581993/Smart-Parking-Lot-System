import { useState } from 'react'
import client from '../api/client'

const Login = ({ onLogin }: { onLogin: (u: string) => void }) => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [msg, setMsg] = useState('')

  const submit = async () => {
    try {
      const res = await client.post('/auth/login', { username, password })
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', username)
      onLogin(username)
    } catch (e: any) {
      setMsg(e.response?.data?.error || 'Login failed')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-black">
      <div className="bg-gray-900 p-8 rounded shadow w-96 border-2 border-yellow-500">
        <h1 className="text-2xl font-bold mb-4 text-yellow-500">Smart Parking Lot</h1>
        <input className="w-full border border-yellow-500 p-2 mb-2 bg-black text-yellow-500" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
        <input className="w-full border border-yellow-500 p-2 mb-4 bg-black text-yellow-500" type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
        <button className="w-full bg-yellow-500 text-black text-black p-2 rounded" onClick={submit}>Login</button>
        {msg && <p className="mt-2 text-red-500">{msg}</p>}
      </div>
    </div>
  )
}


export default Login
