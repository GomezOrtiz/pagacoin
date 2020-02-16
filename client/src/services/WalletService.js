import axios from "axios"

class WalletService {
    constructor() {
        let service = axios.create({
            baseURL: `${process.env.REACT_APP_URL}`
        })
        this.service = service;
    }

    getWalletById = id => {
        return this.service.get(`/wallets/${id}`)
        .then(response => response.data)
    }

    transferAmount = (senderId, receiverId, amount) => {
        const transactionRequest = {senderId, receiverId, amount}
        return this.service.post("/transactions/new", transactionRequest)
        .then(response => response.data)
    }
}

export default WalletService 