import com.ib.client.Contract
import com.ib.client.ContractDetails

class Stock {
   Stock(symbol) {
      def contract = new Contract()
      contract.m_symbol = symbol
      contract.m_secType = "STK"
      contract.m_exchange = "SMART"
      contract.m_currency = "USD"
   }
}

// rec = new Receiver()
// rec.connectToTWS()
// rec.client_socket.reqRealTimeBars 2, contract, 5, 'TRADES', false
