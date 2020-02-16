import axios from "axios"

class UserService {
    constructor() {
        let service = axios.create({
            baseURL: `${process.env.REACT_APP_URL}`
        })
        this.service = service;
    }

    getAllUsers = () => {
        return this.service.get("/users/all")
        .then(response => response.data)
    }

    getUserById = id => {
        return this.service.get(`/users/${id}`)
        .then(response => response.data)
    }
}

export default UserService 