import React, {Component} from 'react'
import {Switch, Route} from "react-router-dom"
import './App.css'
import Header from "./components/Header"
import UserList from "./components/UserList"
import UserDetail from "./components/UserDetail"
import WalletOperations from "./components/WalletOperations"
import NotificationsBadge from "./components/NotificationsBadge"

class App extends Component {
  constructor() {
    super()
    this.state = {
      notification: {
        message: "",
        type: ""
      }
    }
  }

  setNewNotification = (message, type) => {
    let newNotification = {message, type}
    this.setState({ notification: newNotification })
    window.scrollTo(0, 0)
  }

  render() {
    return (
      <div className="App">
        <Header></Header>
        <NotificationsBadge notification={this.state.notification}></NotificationsBadge>
        <main className="main-container">
          <Switch>
            <Route exact path='/' render={props => <UserList {...props} setNewNotification={this.setNewNotification}></UserList>} /> 
            <Route exact path='/usuarios' render={props => <UserList {...props} setNewNotification={this.setNewNotification}></UserList>} />
            <Route exact path='/usuarios/:id' render={props => <UserDetail {...props} setNewNotification={this.setNewNotification}></UserDetail>} />    
            <Route exact path='/carteras/:id' render={props => <WalletOperations {...props} setNewNotification={this.setNewNotification}></WalletOperations>} />                                          
          </Switch>
        </main>
      </div>
    )
  }
}

export default App
