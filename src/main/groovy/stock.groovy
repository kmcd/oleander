import com.ib.client.Contract
import com.ib.client.ContractDetails

class Stock {
   def contract = new Contract()
   def gateway
   
   Stock(symbol, gateway) {
      contract.m_symbol = symbol
      contract.m_secType = "STK"
      contract.m_exchange = "SMART"
      contract.m_primaryExch  = "ISLAND"
      contract.m_currency = "USD"
      gateway = gateway
   }
   
   def symbol() { contract.m_symbol }
   
   def bid_price() {
      // TODO: dry up collect to closure
      ( latest_price('bid').collect { if(it.first() =~ ~'$ticker') it.last() } - null ).first()
   }
   
   def ask_price() {
      ( latest_price('ask').collect { if(it.first() =~ ~'$ticker') it.last() } - null ).first()
   }
   
   def latest_price(quote_type) {
      gateway.quotes.sort { a,b -> date_parse(a.key) <=> date_parse(b.key) }.collect { 
         [it.key, it.value[quote_type] ]
      }[-2..-1]
   }
   
   def date_parse(date) { Date.parse("yyyy-MM-dd HH:mm:ss", date) }
}
