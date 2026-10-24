import { useState } from 'react'
import client from '../api/client'

type CheckInForm = {
  vehicleId: string
  slotId: string
  ticketId: string
  payTicketId: string
  amount: number
}

const CheckIn = () => {
  const [form, setForm] = useState<CheckInForm>({ vehicleId: '', slotId: '', ticketId: '', payTicketId: '', amount: 0 })
  const [data, setData] = useState<unknown>(null)
  const [msg, setMsg] = useState('')

  const update = (field: keyof CheckInForm, value: string | number) => setForm((current) => ({ ...current, [field]: value }))
  const request = async (action: () => Promise<unknown>, success: string) => {
    try {
      setData(await action())
      setMsg(success)
    } catch (error: any) {
      setMsg(error.response?.data?.error || error.response?.data?.message || 'Request failed')
    }
  }

  return (
    <div className="legacy-page">
      <div className="page-intro">
        <div>
          <p className="eyebrow">Operations / live flow</p>
          <h1>Ticket actions</h1>
          <p>Check vehicles in, complete a ticket, or record a payment against an active stay.</p>
        </div>
      </div>
      <div className="operation-grid">
        <section className="legacy-card">
          <p className="eyebrow">01 / arrival</p>
          <h2>Check in vehicle</h2>
          <div className="legacy-form">
            <label>Vehicle ID<input value={form.vehicleId} onChange={(event) => update('vehicleId', event.target.value)} placeholder="Vehicle ID" /></label>
            <label>Slot ID <span className="muted-copy">optional</span><input value={form.slotId} onChange={(event) => update('slotId', event.target.value)} placeholder="Automatic allocation if empty" /></label>
            <button className="button button--primary" type="button" onClick={() => void request(() => client.post('/parking_tickets/check-in', { vehicleId: form.vehicleId, slotId: form.slotId || undefined }), 'Vehicle checked in')}>Check in</button>
          </div>
        </section>
        <section className="legacy-card">
          <p className="eyebrow">02 / departure</p>
          <h2>Check out ticket</h2>
          <div className="legacy-form">
            <label>Ticket ID<input value={form.ticketId} onChange={(event) => update('ticketId', event.target.value)} placeholder="Ticket ID" /></label>
            <button className="button button--ghost" type="button" onClick={() => void request(() => client.post(`/parking_tickets/${form.ticketId}/check-out`), 'Ticket checked out')}>Check out</button>
          </div>
        </section>
        <section className="legacy-card">
          <p className="eyebrow">03 / settlement</p>
          <h2>Record payment</h2>
          <div className="legacy-form">
            <label>Ticket ID<input value={form.payTicketId} onChange={(event) => update('payTicketId', event.target.value)} placeholder="Ticket ID" /></label>
            <label>Amount<input type="number" min="0" value={form.amount} onChange={(event) => update('amount', Number(event.target.value))} placeholder="0.00" /></label>
            <button className="button button--ghost" type="button" onClick={() => void request(() => client.post(`/parking_tickets/${form.payTicketId}/pay`, { amount: form.amount }), 'Payment recorded')}>Record payment</button>
          </div>
        </section>
      </div>
      {msg ? <div className="notice notice--success">{msg}</div> : null}
      {data ? <pre className="json-panel">{JSON.stringify(data, null, 2)}</pre> : null}
    </div>
  )
}

export default CheckIn
