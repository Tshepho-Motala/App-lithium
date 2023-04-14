import BulkTransactionProviderInterface from "@/core/interface/provider/BulkTransactionProviderInterface";
import {UserInterface} from "@/core/interface/cashier/cashierTransactions";


export default class BulKTransactionProviderMock implements BulkTransactionProviderInterface {
    getParams(): Promise<any> {
        return new Promise((res, rej) => {});
    }
    clearParams():any {
        console.log('Clear Cashier filter ')
    }
    selectedUser: null | UserInterface = null

}