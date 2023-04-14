import {UserInterface} from "@/core/interface/cashier/cashierTransactions";


export default interface BulkTransactionProviderInterface {
    getParams(): Promise<any>
    clearParams(): Function
    selectedUser: null | UserInterface
}
