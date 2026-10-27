import { useCallback, useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { isAxiosError } from 'axios'
import client from '../api/client'

import { SessionContext } from './session'
import type { ResourceKey, Session, UserProfile } from './session'
type Action = 'read' | 'create' | 'update' | 'delete'

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [profileLoading, setProfileLoading] = useState(true)
  const [profileError, setProfileError] = useState('')
  const [avatar, setAvatar] = useState({ key: '', url: '' })
  const avatarKey = profile?.hasProfileImage ? `${profile.id}:${profile.imageUpdatedAt || ''}` : ''
  const avatarUrl = avatar.key === avatarKey ? avatar.url : ''

  const refreshProfile = useCallback(() => client.get<UserProfile>('/auth/profile')
    .then((response) => {
      setProfile(response.data)
      setProfileError('')
    }).catch(() => {
      setProfileError('Unable to load your profile. Please try again.')
    }).finally(() => {
      setProfileLoading(false)
    }), [])

  const refresh = useCallback(() => client.get<Session>('/auth/me')
    .then((response) => {
      setSession(response.data)
      setError('')
    }).catch((failure: unknown) => {
      setSession(null)
      setError(isAxiosError(failure) && failure.response?.status === 401
        ? 'Sign in again to continue.' : 'Unable to load your access permissions. Please try again.')
    }).finally(() => {
      setLoading(false)
    }), [])

  useEffect(() => { void refresh() }, [refresh])

  useEffect(() => {
    if (session?.username) void refreshProfile()
  }, [session?.username, refreshProfile])

  useEffect(() => {
    const controller = new AbortController()
    let objectUrl = ''
    if (avatarKey) {
      void client.get<Blob>('/auth/profile/image', { responseType: 'blob', signal: controller.signal })
        .then(({ data }) => {
          if (!controller.signal.aborted) {
            objectUrl = URL.createObjectURL(data)
            setAvatar({ key: avatarKey, url: objectUrl })
          }
        }).catch(() => { /* Keep the initials when the image cannot be loaded. */ })
    }
    return () => {
      controller.abort()
      if (objectUrl) URL.revokeObjectURL(objectUrl)
    }
  }, [avatarKey])

  const can = useCallback((resource: ResourceKey, action: Action) =>
    session?.permissions[resource]?.[action] === true, [session])
  const canAccess = useCallback((resource: ResourceKey) =>
    Object.values(session?.permissions[resource] ?? {}).some(Boolean), [session])
  const hasAuthority = useCallback((...authorities: string[]) =>
    session?.authorities.includes('ROLE_ADMIN') === true
      || authorities.some((authority) => session?.authorities.includes(authority)), [session])

  return <SessionContext.Provider value={{ session, loading, error, refresh, can, canAccess, hasAuthority,
    profile, profileLoading, profileError, avatarUrl, refreshProfile }}>{children}</SessionContext.Provider>
}
