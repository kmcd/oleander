import com.ib.client.Contract
import com.ib.client.ContractDetails

class Stock {
   def contract = new Contract()
   def gateway
   
   Stock(symbol, ib_gateway=null) {
      gateway = ib_gateway
      contract.m_symbol = symbol
      contract.m_secType = "STK"
      contract.m_exchange = "SMART"
      contract.m_primaryExch  = "ARCA"
      contract.m_currency = "USD"
   }
   
   def bid() { latest_price('bid') }
   def ask() { latest_price('ask') }
   def symbol() { contract.m_symbol }
   
   def latest_price(quote_type) {
      (quotes().
         sort { a,b -> date_parse(a.key) <=> date_parse(b.key) }.
         collect { it.value[quote_type] as Float } 
         - null 
      ).last()
   }
   
   def quotes() {
      gateway.quotes.findAll { it.key =~ ~"${symbol()}" }
   }
   
   def date_parse(date) { Date.parse("yyyy-MM-dd HH:mm:ss", date) }
}
