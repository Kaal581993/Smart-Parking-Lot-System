import { useEffect, useRef, useState } from 'react'
import type { ChangeEvent, FormEvent } from 'react'
import { isAxiosError } from 'axios'
import { Camera, Check, Eye, EyeOff, KeyRound, LoaderCircle, ShieldCheck, Trash2, UserRound } from 'lucide-react'
import client from '../api/client'
import { useSession } from '../auth/session'
import UserAvatar from '../components/UserAvatar'
import './ProfileSettings.css'

type Notice = { tone: 'success' | 'error'; message: string } | null
const emptyPasswords = { currentPassword: '', newPassword: '', confirmPassword: '' }
function errorMessage(error: unknown) {
  return isAxiosError(error) && typeof error.response?.data?.error === 'string'
    ? error.response.data.error : 'The request could not be completed. Please try again.'
}
function Feedback({ notice }: { notice: Notice }) {
  return notice ? <p className={`profile-notice profile-notice--${notice.tone}`} role={notice.tone === 'error' ? 'alert' : 'status'}>{notice.message}</p> : null
}

export default function ProfileSettings() {
  const { profile, profileLoading, profileError, refreshProfile } = useSession()
  const [displayNameDraft, setDisplayName] = useState<string | null>(null)
  const displayName = displayNameDraft ?? profile?.displayName ?? ''
  const [file, setFile] = useState<File | null>(null)
  const [preview, setPreview] = useState('')
  const [passwords, setPasswords] = useState(emptyPasswords)
  const [showPasswords, setShowPasswords] = useState(false)
  const [saving, setSaving] = useState<'profile' | 'photo' | 'password' | null>(null)
  const [profileNotice, setProfileNotice] = useState<Notice>(null)
  const [photoNotice, setPhotoNotice] = useState<Notice>(null)
  const [passwordNotice, setPasswordNotice] = useState<Notice>(null)
  const fileInput = useRef<HTMLInputElement>(null)
  const previewUrl = useRef('')

  useEffect(() => () => { if (previewUrl.current) URL.revokeObjectURL(previewUrl.current) }, [])

  const clearSelection = () => {
    if (previewUrl.current) URL.revokeObjectURL(previewUrl.current)
    previewUrl.current = ''
    setFile(null)
    setPreview('')
    if (fileInput.current) fileInput.current.value = ''
  }

  const choosePhoto = (event: ChangeEvent<HTMLInputElement>) => {
    const selected = event.target.files?.[0]
    if (!selected) return
    setPhotoNotice(null)
    if (!['image/jpeg', 'image/png'].includes(selected.type) || selected.size > 2 * 1024 * 1024) {
      setPhotoNotice({ tone: 'error', message: 'Choose a JPG or PNG image up to 2 MB.' })
      event.target.value = ''
      return
    }
    if (previewUrl.current) URL.revokeObjectURL(previewUrl.current)
    previewUrl.current = URL.createObjectURL(selected)
    setPreview(previewUrl.current)
    setFile(selected)
  }

  const savePhoto = async () => {
    if (!file || saving) return
    setSaving('photo'); setPhotoNotice(null)
    try {
      const data = new FormData()
      data.append('image', file)
      await client.put('/auth/profile/image', data)
      await refreshProfile()
      clearSelection()
      setPhotoNotice({ tone: 'success', message: 'Your profile photo has been updated.' })
    } catch (error) { setPhotoNotice({ tone: 'error', message: errorMessage(error) }) }
    finally { setSaving(null) }
  }

  const removePhoto = async () => {
    if (saving) return
    setSaving('photo'); setPhotoNotice(null)
    try {
      if (profile?.hasProfileImage) await client.delete('/auth/profile/image')
      await refreshProfile()
      clearSelection()
      setPhotoNotice({ tone: 'success', message: 'Your profile photo has been removed.' })
    } catch (error) { setPhotoNotice({ tone: 'error', message: errorMessage(error) }) }
    finally { setSaving(null) }
  }

  const saveProfile = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (saving) return
    setSaving('profile'); setProfileNotice(null)
    try {
      await client.put('/auth/profile', { displayName: displayName.trim() })
      await refreshProfile()
      setDisplayName(null)
      setProfileNotice({ tone: 'success', message: 'Your profile has been saved.' })
    } catch (error) { setProfileNotice({ tone: 'error', message: errorMessage(error) }) }
    finally { setSaving(null) }
  }

  const changePassword = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (saving) return
    setPasswordNotice(null)
    if (passwords.newPassword !== passwords.confirmPassword) {
      setPasswordNotice({ tone: 'error', message: 'New passwords do not match.' }); return
    }
    setSaving('password')
    try {
      await client.put('/auth/profile/password', passwords)
      setPasswords(emptyPasswords)
      setPasswordNotice({ tone: 'success', message: 'Password updated. Your other sessions have been signed out.' })
    } catch (error) { setPasswordNotice({ tone: 'error', message: errorMessage(error) }) }
    finally { setSaving(null) }
  }

  if (!profile) return <div className="legacy-page">
    {profileLoading ? <p><LoaderCircle size={18} className="spin" /> Loading your profile…</p>
      : <><p role="alert">{profileError}</p><button className="button button--ghost" type="button" onClick={() => void refreshProfile()}>Try again</button></>}
  </div>

  return <div className="profile-page">
    <header className="page-intro"><div><div className="eyebrow">Your account</div><h1>Profile settings</h1><p>Manage your profile photo, display name, and password.</p></div></header>
    {profileError ? <p className="profile-notice profile-notice--error" role="alert">{profileError}</p> : null}
    <div className="profile-grid">
      <section className="profile-panel profile-photo-panel" aria-labelledby="photo-heading">
        <div className="profile-photo-preview">{preview ? <img src={preview} alt="Selected profile photo preview" /> : <UserAvatar className="avatar--profile" />}</div>
        <h2 id="photo-heading">Profile photo</h2>
        <p>JPG or PNG, up to 2 MB.</p>
        <input ref={fileInput} type="file" accept="image/jpeg,image/png" aria-label="Choose profile image" hidden onChange={choosePhoto} disabled={!!saving} />
        <button className="button button--ghost" type="button" onClick={() => fileInput.current?.click()} disabled={!!saving}><Camera size={16} /> Choose photo</button>
        {file ? <><span className="profile-file-name">{file.name}</span><button className="button button--primary" type="button" onClick={() => void savePhoto()} disabled={!!saving}>{saving === 'photo' ? <LoaderCircle size={15} className="spin" /> : <Check size={15} />} Save photo</button></> : null}
        {profile.hasProfileImage || file ? <button className="profile-text-button" type="button" onClick={() => void removePhoto()} disabled={!!saving}><Trash2 size={14} /> Remove photo</button> : null}
        <Feedback notice={photoNotice} />
        <div className="profile-photo-account"><strong>{profile.displayName}</strong><span>@{profile.username}</span><span className="profile-role">{profile.role || 'Member'}</span></div>
      </section>
      <div className="profile-forms">
        <section className="profile-panel" aria-labelledby="details-heading">
          <div className="profile-panel-heading"><UserRound size={19} /><div><h2 id="details-heading">Personal details</h2><p>Choose how your name appears in Smart Parking.</p></div></div>
          <form onSubmit={(event) => void saveProfile(event)}>
            <div className="profile-fields">
              <div className="form-field"><label htmlFor="profile-display-name">Display name</label><input id="profile-display-name" autoComplete="name" value={displayName} onChange={(event) => setDisplayName(event.target.value)} maxLength={80} required disabled={!!saving} /></div>
              <div className="form-field"><label htmlFor="profile-username">Username</label><input id="profile-username" value={profile.username} readOnly aria-describedby="profile-username-help" /><small id="profile-username-help">Your username is used to sign in.</small></div>
              <div className="form-field"><label htmlFor="profile-role">Account role</label><input id="profile-role" value={profile.role || 'Member'} readOnly /></div>
            </div>
            <Feedback notice={profileNotice} />
            <div className="profile-form-footer"><span>Only you can update these settings.</span><button className="button button--primary" type="submit" disabled={!!saving || !displayName.trim()}>{saving === 'profile' ? <LoaderCircle size={15} className="spin" /> : <Check size={15} />} Save changes</button></div>
          </form>
        </section>
        <section className="profile-panel" aria-labelledby="password-heading">
          <div className="profile-panel-heading"><KeyRound size={19} /><div><h2 id="password-heading">Change password</h2><p>Use at least 8 characters for your new password.</p></div></div>
          <form onSubmit={(event) => void changePassword(event)}>
            <div className="profile-fields">
              <div className="form-field"><label htmlFor="profile-current-password">Current password</label><input id="profile-current-password" type={showPasswords ? 'text' : 'password'} autoComplete="current-password" value={passwords.currentPassword} onChange={(event) => setPasswords({ ...passwords, currentPassword: event.target.value })} required disabled={!!saving} /></div>
              <div className="profile-password-row">
                <div className="form-field"><label htmlFor="profile-new-password">New password</label><input id="profile-new-password" type={showPasswords ? 'text' : 'password'} autoComplete="new-password" value={passwords.newPassword} onChange={(event) => setPasswords({ ...passwords, newPassword: event.target.value })} minLength={8} maxLength={72} required disabled={!!saving} /></div>
                <div className="form-field"><label htmlFor="profile-confirm-password">Confirm new password</label><input id="profile-confirm-password" type={showPasswords ? 'text' : 'password'} autoComplete="new-password" value={passwords.confirmPassword} onChange={(event) => setPasswords({ ...passwords, confirmPassword: event.target.value })} minLength={8} maxLength={72} required disabled={!!saving} /></div>
              </div>
            </div>
            <button className="profile-text-button" type="button" onClick={() => setShowPasswords(!showPasswords)} aria-pressed={showPasswords}>{showPasswords ? <EyeOff size={15} /> : <Eye size={15} />} {showPasswords ? 'Hide passwords' : 'Show passwords'}</button>
            <Feedback notice={passwordNotice} />
            <div className="profile-form-footer"><span><ShieldCheck size={14} /> Other sessions are signed out after a change.</span><button className="button button--primary" type="submit" disabled={!!saving}>{saving === 'password' ? <LoaderCircle size={15} className="spin" /> : <KeyRound size={15} />} Update password</button></div>
          </form>
        </section>
      </div>
    </div>
  </div>
}
