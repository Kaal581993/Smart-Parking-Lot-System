import { useState } from 'react'
import client from '../api/client'

const OwnerRegistration = () => {
  const [form, setForm] = useState({ username: '', password: '', ownerId: '' })
  const [msg, setMsg] = useState('')
  const [error, setError] = useState(false)

  const submit = async () => {
    try {
      await client.post('/auth/register-owner', form)
      setMsg('Owner account registered successfully')
      setError(false)
      setForm({ username: '', password: '', ownerId: '' })
    } catch (requestError: any) {
      setMsg(requestError.response?.data?.error || requestError.response?.data?.message || 'Registration failed')
      setError(true)
    }
  }

  return (
    <div className="legacy-page">
      <div className="page-intro">
        <div>
          <p className="eyebrow">Access / owner account</p>
          <h1>Register owner</h1>
          <p>Create a login for an existing owner identity. Owner data itself is managed from the data registry.</p>
        </div>
      </div>
      <section className="legacy-card narrow-card">
        <div className="legacy-form">
          <label>Username<input value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} placeholder="owner1" /></label>
          <label>Password<input type="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} /></label>
          <label>Owner Aadhaar<input value={form.ownerId} onChange={(event) => setForm({ ...form, ownerId: event.target.value })} placeholder="12 digit Aadhaar number" /></label>
          <button className="button button--primary" type="button" onClick={() => void submit()}>Register owner</button>
        </div>
        {msg ? <div className={error ? 'form-error' : 'notice notice--success'}>{msg}</div> : null}
      </section>
    </div>
  )
}

export default OwnerRegistration
