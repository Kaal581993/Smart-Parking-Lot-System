import { useSession } from '../auth/session'

export default function UserAvatar({ className = '' }: { className?: string }) {
  const { profile, session, avatarUrl } = useSession()
  const name = profile?.displayName || session?.username || 'User'
  const words = name.trim().split(/\s+/)
  const initials = (words.length > 1 ? words[0][0] + words[words.length - 1][0] : name.slice(0, 2)).toUpperCase()

  return <span className={`avatar ${className}`}>
    {avatarUrl ? <img src={avatarUrl} alt={`${name}'s profile`} /> : <span aria-label={`${name}'s initials`}>{initials}</span>}
  </span>
}
