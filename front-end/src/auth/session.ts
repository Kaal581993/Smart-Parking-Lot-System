import { createContext, useContext } from 'react'

export type ResourceKey = 'owners' | 'ownerAadhaar' | 'users' | 'roles' | 'privileges' | 'addresses'
  | 'licenses' | 'vehicleTypes' | 'vehicles' | 'parkingSlots' | 'parkingTickets'
type Action = 'read' | 'create' | 'update' | 'delete'
export type Session = {
  username: string
  authorities: string[]
  permissions: Record<ResourceKey, Record<Action, boolean>>
}

export type UserProfile = {
  id: number
  username: string
  displayName: string
  role: string
  ownerId: string | null
  hasProfileImage: boolean
  imageUpdatedAt: string | null
}

type SessionState = {
  profile: UserProfile | null
  profileLoading: boolean
  profileError: string
  avatarUrl: string
  refreshProfile: () => Promise<void>
  session: Session | null
  loading: boolean
  error: string
  refresh: () => Promise<void>
  can: (resource: ResourceKey, action: Action) => boolean
  hasAuthority: (...authorities: string[]) => boolean
  canAccess: (resource: ResourceKey) => boolean
}

export const SessionContext = createContext<SessionState | null>(null)

export function useSession() {
  const context = useContext(SessionContext)
  if (!context) throw new Error('useSession must be used within SessionProvider')
  return context
}
