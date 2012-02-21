import com.ib.client.Contract
import com.ib.client.ContractDetails

class Stock {
   def contract = new Contract()
   
   Stock(symbol) {
      contract.m_symbol = symbol
      contract.m_secType = "STK"
      contract.m_exchange = "SMART"
      contract.m_primaryExch  = "ARCA"
      contract.m_currency = "USD"
   }
   
   def bid() { Quote.current_bid(symbol()) }
   def ask() { Quote.current_ask(symbol()) }
   
   def symbol() { contract.m_symbol }
}
