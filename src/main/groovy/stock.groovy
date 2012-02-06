import com.ib.client.Contract
import com.ib.client.ContractDetails

class Stock {
   def contract
   
   Stock(symbol) {
      contract = new Contract()
      contract.m_symbol = symbol
      contract.m_secType = "STK"
      contract.m_exchange = "SMART"
      contract.m_currency = "USD"
   }
}
