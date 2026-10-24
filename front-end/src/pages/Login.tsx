import { useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import client from '../api/client'

const Login = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [msg, setMsg] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    setMsg('')
    try {
      const response = await client.post('/auth/login', { username, password })
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', username)
      navigate('/')
    } catch (error: any) {
      setMsg(error.response?.data?.error || error.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <section className="login-card">
        <div className="brand-lockup" aria-label="Smart Parking">
          <span className="brand-mark">SP</span>
          <span>
            <strong>Smart Parking</strong>
            <small>Control desk</small>
          </span>
        </div>
        <p className="eyebrow">Secure access</p>
        <h1>Welcome back.</h1>
        <p>Sign in to manage the lot, owner records, fleet, and access data.</p>
        <form className="login-form" onSubmit={submit}>
          <label>
            Username
            <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required />
          </label>
          <label>
            Password
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required />
          </label>
          {msg ? <div className="login-error">{msg}</div> : null}
          <button className="button button--primary" type="submit" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </section>
    </div>
  )
}

export default Login
