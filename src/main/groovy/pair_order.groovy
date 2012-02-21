import java.math.MathContext
import com.ib.client.Order
import Gateway

class PairOrder {
   def gateway
   def position
   
   PairOrder(ib_gateway) { gateway = ib_gateway }
   
   def enter(new_position) {
      if( !new_position.pair.entry_signal() ) { return false }
      
      position = new_position
      long_order_id = long_entry_order()
      short_order_id = short_entry_order()
      
      sleep(500)
      
      if( filled(long_order_id) && !filled(short_order_id) ) { long_exit_order() } 
      if( filled(short_order_id) && !filled(long_order_id) ) { short_exit_order() }
      
      if ( filled(long_order_id) && filled(short_order_id) ) {
         position.available = false
      }
   }
   
   def exit() {
      if( !position.profitable() ) { return false }
      
      long_order_id = long_exit_order()
      short_order_id = short_exit_order()
      
      if( filled(long_order_id) && !filled(short_order_id) ) { short_exit_order() } 
      if( filled(short_order_id) && !filled(long_order_id) ) { long_exit_order() }
      
      position.available = true
   }
   
   def filled(order_id) { gateway.order_filled(short_order_id) }
   
   def long_entry_order() {
      gateway.place_order(long_contract(), long_order())
   }
   
   def short_entry_order() {
      gateway.place_order(short_contract(), short_order())
   }
   
   def long_exit_order() {
      gateway.place_order(long_contract(), sell_order(sell_price()) )
   }
   
   def short_exit_order() {
      gateway.place_order(short_contract(), cover_order(cover_price()) )
   }
   
   def sell_price() {
      position.short_spy ? Quote.current_ask('spy') : Quote.current_ask('ivv')
   }
   
   def cover_price() {
      position.short_spy ? Quote.current_bid('spy') : Quote.current_bid('ivv')
   }
   
   def order(action, quantity, price, type='LMT', duration='IOC') {
      def order = new Order()
      order.m_action = action
      order.m_totalQuantity = quantity
      order.m_orderType = type
      order.m_tif = duration
      order.m_allOrNone = 1
      order.m_lmtPrice = ( price as BigDecimal ).round( new MathContext(5) )
      return order
   }
   
   def long_order() {
      order('BUY', position.long_shares(), position.opening_long_price() )
   }
   
   def short_order() {
      order('SELL', position.short_shares(), position.opening_short_price() )
   }
   
   def sell_order(price) {
      order('SELL', position.long_shares(), price )
   }
   
   def cover_order(price) {
      order('BUY', position.short_shares(), price )
   }
   
   def long_contract() {
      position.short_spy ? new Stock("ivv").contract : new Stock("spy").contract
   }
   
   def short_contract() {
      position.short_spy ? new Stock("spy").contract : new Stock("ivv").contract
   }
}
