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
   
   def bid() { quote('bid') }
   def ask() { quote('ask') }
   
   def quote(type) {
      def quote_type = "${symbol()}_$type"
      
      def quotes = Quote.today(quote_type)
      if( quotes.isEmpty() ) return
      quotes.last()[quote_type]
   }
   
   def symbol() { contract.m_symbol }
   def date_parse(date) { Date.parse("yyyy-MM-dd HH:mm:ss", date) }
}
