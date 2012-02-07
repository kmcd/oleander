import com.ib.client.Order
import Gateway

class PairOrder {
   def gateway
   def position
   
   PairOrder(gateway) {
      gateway = gateway
   }
   
   def enter(position) {
      position = position
      gateway.place_order(short_contract(), short_order())
      gateway.place_order(long_contract(), long_order())
      gateway.cancel_last_two_orders_unless_both_filled()
   }
   
   def exit(spy_price, ivv_price) {
      cover_price = position.short_spy ? spy_price : ivv_price
      sell_price = position.short_spy ? ivv_price : spy_price
      
      gateway.place_order(short_contract(), short_order(cover_price) )
      gateway.place_order(long_contract(), long_order(sell_price) )
      gateway.cancel_last_two_orders_unless_both_filled()
      
      position.open = false
   }
   
   def order(type, quantity, price) {
      def order = new Order()
      order.m_action = type
      order.m_totalQuantity = quantity
      order.m_orderType = 'LMT'
      order.m_tif = 'IOC'
      order.allOrNone = 1
      order.m_lmtPrice = price
      return order
   }
   
   def long_order() {
      order('BUY', position.long_shares(), position.opening_long_price() )
   }
   
   def short_order() {
      order('SSHORT', position.short_shares(), position.opening_short_price() )
   }
   
   def sell_order(position, price) {
      order('SELL', position.long_shares(), price )
   }
   
   def cover_order() {
      order('SELL', position.short_shares(), price )
   }
   
   def long_contract() {
      position.short_spy ? new Stock("IVV").contract : new Stock("SPY").contract
   }
   
   def short_contract() {
      position.short_spy ? new Stock("SPY").contract : new Stock("IVV").contract
   }
}
