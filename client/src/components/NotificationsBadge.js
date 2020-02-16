import React from "react"

function NotificationsBadge({notification}) {
    return (
        <>
        {notification &&
            <section className={`alert alert-${notification.type}`}>{notification.message}</section>
        }
        </>
    )
}

export default NotificationsBadge