import React, { Component } from 'react'
import UserService from "../services/UserService"
import UserWallet from "./UserWallet"

class UserDetail extends Component {
    constructor(props) {
        super(props)
        this.state = {
            user: undefined
        }
        this.service = new UserService()
    }

    getUserById = id => {
        this.service.getUserById(id)
        .then(data => this.setState({user: data}))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    componentDidMount() {
        const { match: { params } } = this.props
        this.getUserById(params.id)
    }

    render() {
        return (
            <main className="container py-4">
                {
                this.state.user &&
                <div className="card bdr-main">
                    <div className="card-header bdr-main">
                        <h1 className="h2 txt-dark my-3">Detalle de usuario</h1>
                    </div>
                    <div className="card-body bdr-main">
                        <ul className="list-group">
                            <li className="list-group-item">
                                <span className="font-weight-bold mr-2">Nombre</span>
                                <span>{this.state.user.name}</span>
                            </li>
                            <li className="list-group-item">
                                <span className="font-weight-bold mr-2">Apellido</span>
                                <span>{`${this.state.user.surname} ${this.state.user.secondSurname}`}</span>
                            </li>
                            <li className="list-group-item">
                                <span className="font-weight-bold mr-2">Email</span>
                                <span>{this.state.user.email}</span>
                            </li>
                        </ul>
                    </div>
                </div>
                }
                { this.state.user && !this.state.user.wallets &&
                    <div className="alert alert-main my-4">`No hay carteras para el usuario ${this.state.user.name} ${this.state.user.surname}`</div>			
                }
                { this.state.user && this.state.user.wallets &&
                    <table className="table table-bordered table-striped my-4">
                        <thead className="bg-second text-light">
                            <tr>
                                <th>Cartera (ID)</th>
                                <th>Saldo</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                this.state.user.wallets.map((wallet, idx) => {
                                    return <UserWallet key={idx} wallet={wallet}></UserWallet>
                                })
                            }
                        </tbody>
                    </table>
                }
            </main>
        )
    }
}

export default UserDetail
