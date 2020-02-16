import React, { Component } from 'react'
import UserService from "../services/UserService"
import UserTable from "./UserTable"
import Pagination from "./Pagination"

class UserList extends Component {
    constructor(props) {
        super(props)
        this.state = {
            users: [],
            pageOfItems: []
        }
        this.services = new UserService()
    }

    getAllUsers = () => {
        this.services.getAllUsers()
        .then(data => this.setState({users: data}))
        .catch(err => this.props.setNewNotification(err.response.data, "red"))
    }

    onChangePage = pageOfItems => {
        this.setState({ pageOfItems: pageOfItems });
    }

    componentDidMount() {
        this.getAllUsers()
    }

    render() {
        return (
            <main className="container">
                <div className="card my-5 bdr-main">
                    <div className="card-header bdr-main">
                        <div className="row">
                            <div className="col-sm-8"><h1 className="h2 txt-dark my-3">Usuarios</h1></div>
                        </div>
                    </div>
                    <div className="card-body">
                        <UserTable users={this.state.pageOfItems}></UserTable>
                        <Pagination items={this.state.users} onChangePage={this.onChangePage} />
                    </div>
                </div>
            </main>
        )
    }
}

export default UserList