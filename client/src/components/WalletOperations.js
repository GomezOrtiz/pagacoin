import React, {Component} from "react"
import WalletService from "../services/WalletService"

class WalletOperations extends Component {
    constructor(props) {
        super(props)
        this.state = {
            wallet: undefined,
            receiverId: "",
            amount: 0,
            transaction: undefined
        }
        this.service = new WalletService()
    }

    getWalletById = id => {
        this.service.getWalletById(id)
        .then(data => this.setState({wallet: data}))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    handleChange = e => {
        const {name, value} = e.target
        this.setState({[name]: value})
    }

    handleSubmit = e => {
        e.preventDefault()
        this.service.transferAmount(this.state.wallet.id, this.state.receiverId, this.state.amount)
        .then(data => this.props.setNewNotification(`La transacción ${data.id} se ha realizado con éxito`, "green"))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    componentDidMount() {
        const { match: { params } } = this.props
        this.getWalletById(params.id)
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
                                    <span className="font-weight-bold mr-2">Cartera</span>
                                    <span>{this.state.wallet.id}</span>
                                </li>
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2">Saldo</span>
                                    <span>{this.state.wallet.balance}</span>
                                </li>
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2">Nombre del propietario</span>
                                    <span>{this.state.wallet.owner.name}</span>
                                </li>
                                <li className="list-group-item">
                                    <span className="font-weight-bold mr-2">Apellidos</span>
                                    <span>{`${this.state.wallet.owner.surname} ${this.state.wallet.owner.secondSurname}`}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div className="card bdr-main mt-3">
                        <div className="card-header bdr-main">
                                <h1 className="h2 txt-dark my-3">Nueva transacción</h1>
                        </div>
                        <div className="card-body bdr-main">
                            <form onSubmit={(e) => this.handleSubmit(e)}>
                            <div className="form-group row">
                                <label className="col-sm-4 col-form-label" htmlFor="receiverId">Id de la cartera de destino</label>
                                <div className="col-sm-4"><input className="form-control" type="text" name="receiverId" value={this.state.receiver} onChange={e => this.handleChange(e)} ></input></div>
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