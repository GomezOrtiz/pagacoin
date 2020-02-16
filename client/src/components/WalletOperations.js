import React, {Component} from "react"
import { Link } from "react-router-dom"
import { Typeahead } from 'react-bootstrap-typeahead';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faWallet } from '@fortawesome/free-solid-svg-icons'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { faDollarSign } from '@fortawesome/free-solid-svg-icons'
import { faCopy } from '@fortawesome/free-regular-svg-icons'
import UserService from "../services/UserService"
import WalletService from "../services/WalletService"

class WalletOperations extends Component {
    constructor(props) {
        super(props)
        this.state = {
            users: [],
            wallets: [],
            wallet: undefined,
            selectedUser: "",
            receiverId: "",
            amount: 0,
            transaction: undefined
        }
        this.userService = new UserService()
        this.walletService = new WalletService()
    }

    getAllUsers = () => {
        this.userService.getAllUsers()
        .then(data => {
            data = data.filter(user => user.id !== this.state.wallet.owner.id)
            this.setState({users: data})
        })
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    getWalletById = id => {
        this.walletService.getWalletById(id)
        .then(data => this.setState({wallet: data}))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    getWalletsByOwner = ownerId => {
        this.walletService.getWalletsByOwner(ownerId)
        .then(data => this.setState({wallets: data}))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    handleChange = e => {
        const {name, value} = e.target
        this.setState({[name]: value})
    }

    handleSubmit = e => {
        e.preventDefault()
        this.walletService.transferAmount(this.state.wallet.id, this.state.receiverId, this.state.amount)
        .then(data => this.props.setNewNotification(`La transacción ${data.id} se ha realizado con éxito`, "green"))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }
    
    setSelected = selected => {
        this.setState({ selectedUser: selected })
        let selectedUser = this.state.users.filter(user => {
            let fullName = `${user.name} ${user.surname} ${user.secondSurname}`
            return fullName === selected[0]
        })  

        if(selectedUser[0]) {
            this.walletService.getWalletsByOwner(selectedUser[0].id)
            .then(data => this.setState({ wallets: data }))
            .catch(err => this.props.setNewNotification(err.response.data, "red"))
        }
    }

    selectWallet = (e, walletId) => {
        e.preventDefault()
        this.setState({receiverId: walletId})
    }

    componentDidMount() {
        const { match: { params } } = this.props
        this.getWalletById(params.id)
        this.getAllUsers()
    }

    render() {
        return (
            <main className="container py-4">
                {
                this.state.wallet &&
                <div>
                    <div className="card bdr-main">
                        <div className="card-header bdr-main">
                            <h1 className="h2 txt-dark my-3">Detalle de cartera</h1>
                        </div>
                        <div className="card-body bdr-main">
                            <ul className="list-group">
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2"><FontAwesomeIcon icon={faUser} className="txt-dark" />&nbsp;&nbsp;Cartera</span>
                                    <span>{this.state.wallet.id}</span>
                                </li>
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2"><FontAwesomeIcon icon={faDollarSign} className="txt-dark" />&nbsp;&nbsp;Saldo</span>
                                    <span>{this.state.wallet.balance}</span>
                                </li>
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2"><FontAwesomeIcon icon={faWallet} className="txt-dark" />&nbsp;&nbsp;Nombre del propietario</span>
                                    <span>{`${this.state.wallet.owner.name} ${this.state.wallet.owner.surname} ${this.state.wallet.owner.secondSurname}`}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div className="card bdr-main mt-3">
                        <div className="card-header bdr-main">
                                <h1 className="h2 txt-dark my-3">Nueva transacción</h1>
                        </div>
                        <div className="card-body bdr-main">
                            <Typeahead
                                {...this.state.selectedUser}
                                id="user-autocomplete"
                                onChange={selected => this.setSelected(selected)}
                                options={this.state.users.map(user => `${user.name} ${user.surname} ${user.secondSurname}`)}
                                placeholder="Elige un usuario..."
                            />
                            <div className="container my-3 ml-3">
                            {this.state.wallets &&
                                this.state.wallets.map((wallet, idx) => <Link to="/" key={idx} className="row" onClick={e => this.selectWallet(e, wallet.id)}><span className="txt-contrast">{wallet.id}</span>&nbsp;&nbsp;<FontAwesomeIcon icon={faCopy} className="txt-dark" /></Link>)
                            }
                            </div>
                            <form onSubmit={(e) => this.handleSubmit(e)}>
                            <div className="form-group row">
                                <label className="col-sm-4 col-form-label" htmlFor="receiverId">Id de la cartera de destino</label>
                                <div className="col-sm-4"><input className="form-control" type="text" name="receiverId" value={this.state.receiverId} onChange={e => this.handleChange(e)} ></input></div>
                            </div>
                            <div className="form-group row">
                                <label className="col-sm-4 col-form-label" htmlFor="amount">Cantidad que se desea transferir</label>
                                <div className="col-sm-2"><input className="form-control" type="number" name="amount" value={this.state.amount} onChange={e => this.handleChange(e)}></input></div>
                            </div>
                                <button className="btn btn-main mt-3" type="submit">Transferir fondos</button>
                            </form>
                        </div>
                    </div>
                </div>
                }
            </main>
        )
    }
}

export default WalletOperations