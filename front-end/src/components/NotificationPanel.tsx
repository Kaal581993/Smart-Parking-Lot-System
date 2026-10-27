import { useCallback, useEffect, useState } from 'react'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { apiBaseUrl } from '../api/client'

interface Notification {
  type: 'payment' | 'expiry' | 'slot'
  data: any
  timestamp: Date
}

const NotificationPanel = () => {
  const [notifications, setNotifications] = useState<Notification[]>([])

  const addNotification = useCallback((type: 'payment' | 'expiry' | 'slot', data: any) => {
    setNotifications(prev => [{ type, data, timestamp: new Date() }, ...prev].slice(0, 10))
  }, [])

  useEffect(() => {
    const socket = new SockJS(`${apiBaseUrl}/ws`)
    const client = Stomp.over(socket)
    
    client.connect({}, () => {
      
      client.subscribe('/topic/payments', (message: any) => {
        const data = JSON.parse(message.body)
        addNotification('payment', data)
      })
      
      client.subscribe('/topic/expiry', (message: any) => {
        const data = JSON.parse(message.body)
        addNotification('expiry', data)
      })
      
      client.subscribe('/topic/slots', (message: any) => {
        const data = JSON.parse(message.body)
        addNotification('slot', data)
      })
    })

    return () => {
      if (client && client.connected) {
        client.disconnect()
      }
    }
  }, [addNotification])

  const getNotificationText = (n: Notification) => {
    switch (n.type) {
      case 'payment':
        return `Payment completed for ticket ${n.data.ticketId}. Amount: ₹${n.data.totalFee}`
      case 'expiry':
        return `Ticket ${n.data.ticketId} expiring in ${n.data.remainingMinutes} minutes. Vehicle: ${n.data.vehicleNumber}`
      case 'slot':
        return `Slot status updated`
      default:
        return 'Unknown notification'
    }
  }

  return (
    <div className="fixed top-20 right-4 w-80 bg-gray-900 border border-yellow-500 rounded shadow-lg z-50">
      <div className="bg-yellow-500 text-black p-2 font-bold">Notifications</div>
      <div className="max-h-96 overflow-y-auto">
        {notifications.length === 0 ? (
          <p className="p-4 text-gray-400">No notifications</p>
        ) : (
          notifications.map((n, i) => (
            <div key={i} className="p-3 border-b border-gray-700">
              <p className="text-sm text-white">{getNotificationText(n)}</p>
              <p className="text-xs text-gray-500 mt-1">{n.timestamp.toLocaleTimeString()}</p>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default NotificationPanel
