import React from 'react'
import UserRow from "./UserRow"

function UserTable({users}) {
    return (
        <table className="table table-striped">
            <thead className="bg-second text-light">
                <tr>
                    <th>Nombre</th>
                    <th>Apellidos</th>
                    <th>Fecha de nacimiento</th>
                    <th>Email</th>
                    <th>Teléfono</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                    {
                        users && 
                        users.map((user,idx) => <UserRow user={user} key={idx}></UserRow>)
                    }
            </tbody>
        </table>
    )
}

export default UserTable