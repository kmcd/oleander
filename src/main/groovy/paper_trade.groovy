import com.ib.client.Contract
import com.ib.client.ContractDetails
import com.ib.client.EClientSocket
import com.ib.client.EWrapper
import com.ib.client.EWrapperMsgGenerator
import com.ib.client.Execution
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.client.UnderComp
import Gateway
import Position
import Pair

def market_open(date) {
   (Calendar.MONDAY..Calendar.FRIDAY).contains(date.day) &&
   ( date.hours >= 9    && date.minutes >= 30 ) &&
   ( date.hours <= 15   && date.minutes >= 59 )
}

def pair = new Pair(tickers:['SPY','IVV'])
def position = new Position()
def gateway = new Gateway()
def spy = new Stock("SPY")
def ivv = new Stock("IVV")

gateway.connect()
gateway.client_socket.reqRealTimeBars 1, spy.contract, 5, 'BID', true
gateway.client_socket.reqRealTimeBars 2, spy.contract, 5, 'ASK', true
gateway.client_socket.reqRealTimeBars 3, ivv.contract, 5, 'BID', true
gateway.client_socket.reqRealTimeBars 5, ivv.contract, 5, 'ASK', true

while(true) {
   def now = new Date()
   
   if ( market_open(now) ) {
      if( position.open ) {
         def spy_price = latest_ask_price(quotes, "SPY")
         def ivv_price = latest_ask_price(quotes, "IVV")
         
         if (position.profit(spy:spy_price, ivv:ivv_price) >= 10.0) { 
            exit(position, spy_price, ivv_price)
         } 
      }
      else {
         pair.quotes = gateway.quotes
         def spread = pair.spread()
         
         if( spread() <= -0.03 || spread() >= 0.03 )  {
            def spy_price = latest_bid_price(quotes, "SPY")
            def ivv_price = latest_bid_price(quotes, "IVV")
            position = new Position(SPY:spy_price, IVV:ivv_price, open:true)
            enter(position)
         }
      }
   }
   
   sleep(2)
}

def latest_bid_price(ticker) {
   ( latest_bid().collect { if(it.first() =~ ~'$ticker') it.last() } - null ).first()
}

def latest_bid() {
   gateway.quotes.sort { a,b -> date_parse(a.key) <=> date_parse(b.key) }.collect { 
      [it.key, it.value['bid'] ]
   }[-2..-1]
}

def latest_ask_price(ticker) {
   ( latest_ask().collect { if(it.first() =~ ~'$ticker') it.last() } - null ).first()
}

def latest_ask() {
   gateway.quotes.sort { a,b -> date_parse(a.key) <=> date_parse(b.key) }.collect { 
      [it.key, it.value['ask'] ]
   }[-2..-1]
}

def date_parse(date) {
   Date.parse("yyyy-MM-dd HH:mm:ss", date)
}

def order_id = 1

def enter(position) {
   // FOK on current prices
   def spy = new Stock("SPY").contract
   def ivv = new Stock("IVV").contract
   
   def long_order = new Order()
   long_order.m_action = 'BUY'
   long_order.m_totalQuantity = position.long_shares()
   long_order.m_orderType = 'LMT'
   long_order.m_tif = 'IOC'
   long_order.allOrNone = 1
   long_order.m_lmtPrice = position.opening_long_price()
   
   def short_order = new Order();
   short_order.m_action = 'SSHORT';
   short_order.m_totalQuantity = position.short_shares()
   short_order.m_orderType = 'LMT'
   short_order.m_tif = 'IOC'
   short_order.allOrNone = 1
   short_order.m_lmtPrice = position.opening_short_price()
   
   short_contract = position.short_spy ? spy : ivv
   long_contract = position.short_spy ? ivv : spy
   
   client_socket.placeOrder(order_id+=1, short_contract, short_order)
   client_socket.placeOrder(order_id+=1, long_contract, long_order)
   
   // Cancel unless both execute
   sleep(2)
   
   if (!(gateway.orders[ order_id - 1] && gateway.orders[ order_id - 2])) {
      client_socket.cancelOrder( order_id - 1 )
      client_socket.cancelOrder( order_id - 2 )
   }
}

def exit(position, spy_price, ivv_price) {
   // FOK on current prices
   def spy = new Stock("SPY").contract
   def ivv = new Stock("IVV").contract
   
   def long_order = new Order()
   long_order.m_action = 'SELL'
   long_order.m_totalQuantity = position.long_shares()
   long_order.m_orderType = 'LMT'
   long_order.m_tif = 'IOC'
   long_order.allOrNone = 1
   long_order.m_lmtPrice = position.short_spy ? ivv_price : spy_price
   
   def short_order = new Order();
   short_order.m_action = 'SELL';
   short_order.m_totalQuantity = position.short_shares()
   short_order.m_orderType = 'LMT'
   short_order.m_tif = 'IOC'
   short_order.allOrNone = 1
   short_order.m_lmtPrice = position.short_spy ? spy_price : ivv_price
   
   short_contract = position.short_spy ? spy : ivv
   long_contract = position.short_spy ? ivv : spy
   
   client_socket.placeOrder(order_id+=1, short_contract, short_order)
   client_socket.placeOrder(order_id+=1, long_contract, long_order)
   
   // Cancel unless both execute
   sleep(2)
   
   if (!(gateway.orders[ order_id - 1] && gateway.orders[ order_id - 2])) {
      client_socket.cancelOrder( order_id - 1 )
      client_socket.cancelOrder( order_id - 2 )
   }
   
   position.open = false
}
