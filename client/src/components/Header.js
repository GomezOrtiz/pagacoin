import React from 'react'
import {Navbar, Nav} from "react-bootstrap"

function Header() {
  return (
    <Navbar variant="dark" className="bg-main">
      <Navbar.Brand href="/">PagaCoin</Navbar.Brand>
      <Nav className="mr-auto">
        <Nav.Link href="/usuarios">Usuarios</Nav.Link>
      </Nav>
  </Navbar>
  )
}

export default Header
