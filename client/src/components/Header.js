import React from 'react'
import {Navbar} from "react-bootstrap"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCoins } from '@fortawesome/free-solid-svg-icons'

function Header() {
  return (
    <Navbar variant="dark" className="bg-main">
        <Navbar.Brand href="/"><FontAwesomeIcon icon={faCoins} className="txt-light" />&nbsp;&nbsp;PagaCoin</Navbar.Brand>
    </Navbar>
  )
}

export default Header
