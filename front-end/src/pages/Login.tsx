import { useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import client from '../api/client'

type AuthMode = 'login' | 'register'

const emptyRegistration = {
  username: '',
  password: '',
  confirmPassword: '',
  ownerId: '',
}

const Login = () => {
  const [mode, setMode] = useState<AuthMode>('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [registration, setRegistration] = useState(emptyRegistration)
  const [msg, setMsg] = useState('')
  const [msgTone, setMsgTone] = useState<'error' | 'success'>('error')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    setMsg('')
    try {
      if (mode === 'register') {
        if (registration.password !== registration.confirmPassword) {
          setMsg('Passwords do not match')
          setMsgTone('error')
          return
        }

        await client.post('/auth/register-owner', {
          username: registration.username.trim(),
          password: registration.password,
          ownerId: registration.ownerId.trim() || null,
        })
        setUsername(registration.username.trim())
        setPassword('')
        setRegistration(emptyRegistration)
        setMode('login')
        setMsg('APP_USER created successfully. You can sign in now.')
        setMsgTone('success')
        return
      }

      const response = await client.post('/auth/login', { username, password })
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', username)
      navigate('/')
    } catch (error: any) {
      setMsg(error.response?.data?.error || error.response?.data?.message || 'Login failed')
      setMsgTone('error')
    } finally {
      setLoading(false)
    }
  }

  const changeMode = (nextMode: AuthMode) => {
    setMode(nextMode)
    setMsg('')
    setMsgTone('error')
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
        <p className="eyebrow">{mode === 'login' ? 'Secure access' : 'New account'}</p>
        <h1>{mode === 'login' ? 'Welcome back.' : 'Create your account.'}</h1>
        <p>
          {mode === 'login'
            ? 'Sign in to manage the lot, owner records, fleet, and access data.'
            : 'Register a new APP_USER account. Public registration creates an OWNER account.'}
        </p>
        <form className="login-form" onSubmit={submit}>
          {mode === 'register' ? (
            <>
              <label>
                Username
                <input
                  value={registration.username}
                  onChange={(event) => setRegistration({ ...registration, username: event.target.value })}
                  autoComplete="username"
                  required
                />
              </label>
              <label>
                Password
                <input
                  type="password"
                  value={registration.password}
                  onChange={(event) => setRegistration({ ...registration, password: event.target.value })}
                  autoComplete="new-password"
                  minLength={6}
                  required
                />
              </label>
              <label>
                Confirm password
                <input
                  type="password"
                  value={registration.confirmPassword}
                  onChange={(event) => setRegistration({ ...registration, confirmPassword: event.target.value })}
                  autoComplete="new-password"
                  minLength={6}
                  required
                />
              </label>
              <label>
                Owner Aadhaar <span className="login-form__optional">optional</span>
                <input
                  value={registration.ownerId}
                  onChange={(event) => setRegistration({ ...registration, ownerId: event.target.value })}
                  inputMode="numeric"
                  pattern="[0-9]{12}"
                  placeholder="Link an existing owner record"
                />
                <small className="login-hint">Use the 12 digit Aadhaar number when this account belongs to an existing owner.</small>
              </label>
            </>
          ) : (
            <>
              <label>
                Username
                <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required />
              </label>
              <label>
                Password
                <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required />
              </label>
            </>
          )}
          {msg ? <div className={msgTone === 'success' ? 'login-success' : 'login-error'} role="alert">{msg}</div> : null}
          <button className="button button--primary" type="submit" disabled={loading}>
            {loading ? (mode === 'login' ? 'Signing in…' : 'Creating account…') : mode === 'login' ? 'Sign in' : 'Register as APP_USER'}
          </button>
        </form>
        <div className="login-switch">
          <span>{mode === 'login' ? 'Need an account?' : 'Already have an account?'}</span>
          <button className="login-link" type="button" onClick={() => changeMode(mode === 'login' ? 'register' : 'login')}>
            {mode === 'login' ? 'Register as new APP_USER' : 'Return to sign in'}
          </button>
        </div>
      </section>
    </div>
  )
}

export default Login
