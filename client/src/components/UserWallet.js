import React from 'react'
import {Link} from "react-router-dom"

function UserWallet({wallet}) {
    return (
        <tr>
            <td>{wallet.id}</td>
            <td>{wallet.balance}</td>
            <Link to={`/carteras/${wallet.id}`} className="txt-white"><button className="btn btn-green d-block mx-auto mt-1">Operaciones</button></Link>
        </tr>
    )
}

export default UserWallet