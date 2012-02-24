import java.math.MathContext
import com.ib.client.Order
import Gateway

class PairOrder {
   def gateway
   def position
   def long_order_id
   def short_order_id
   
   PairOrder(ib_gateway) { gateway = ib_gateway }
   
   def enter(new_position) {
      position = new_position
      if( position.opening_prices_changed() ) { return false }
      
      long_entry_order()
      short_entry_order()
      
      while( !orders_complete() ) {
         gateway.wait_for_response()
         if( position.opening_prices_changed() )   { cancel_both_orders() }
      }
      
      position.available = !filled(long_order_id) && !filled(short_order_id)
   }
   
   def exit() {
      if( position.exit_prices_changed() ) { return false }
      
      long_exit_order()
      short_exit_order()
      
      while( !orders_complete() ) {
         gateway.wait_for_response()
         if( position.exit_prices_changed() )   { cancel_both_orders() }
      }
      
      position.available = filled(long_order_id) && filled(short_order_id)
   }
   
   def orders_complete() {
      ( filled(long_order_id) && filled(short_order_id) ) ||
      ( cancelled(long_order_id) && cancelled(short_order_id) ) ||
      legged_position_recovered()
   }
   
   def legged_position_recovered() {
      if( !legged() ) { return false }
      
      if( exposed_long() ) { 
         long_exit_order('MKT')
         while( !filled(long_order_id)) { gateway.wait_for_response() }
      }
      
      if( exposed_short() ) { 
         short_exit_order('MKT')
         while( !filled(short_order_id)) { gateway.wait_for_response() }
      }
      
      true
   }
   
   def legged() { exposed_long() || exposed_short() }
   
   def cancel_both_orders() {
      gateway.cancel(long_order_id)
      gateway.cancel(short_order_id)
   }
   
   def exposed_long()      { filled(long_order_id) && cancelled(short_order_id) }
   def exposed_short()     { filled(short_order_id) && cancelled(long_order_id) }
   def filled(order_id)    { gateway.order_filled(order_id) }
   def cancelled(order_id) { gateway.order_cancelled(order_id) }
   
   // TODO: move to Order factory
   def long_entry_order() {
      long_order_id = gateway.place_order(long_contract(), 
         order('BUY', position.long_shares(), position.opening_long_price() ))
   }
   
   def long_exit_order(type='LMT') {
      long_order_id = gateway.place_order(long_contract(), 
         order('SELL', position.long_shares(), position.sell_price, type))
   }
   
   def short_entry_order() {
      short_order_id = gateway.place_order(short_contract(), 
         order('SELL', position.short_shares(), position.opening_short_price()))
   }
   
   def short_exit_order(type='LMT') {
      short_order_id = gateway.place_order(short_contract(), 
         order('BUY', position.short_shares(), position.cover_price, type))
   }
   
   def order(action, quantity, price, type='LMT', duration='IOC') {
      def order = new Order()
      order.m_action = action
      order.m_totalQuantity = quantity
      order.m_orderType = type
      order.m_tif = duration
      order.m_allOrNone = 1
      
      if( type == 'LMT') {
         order.m_lmtPrice = ( price as BigDecimal ).round( new MathContext(5) )
      }
      
      return order
   }
   
   def long_contract() {
      position.short_spy ? new Stock("ivv").contract : new Stock("spy").contract
   }
   
   def short_contract() {
      position.short_spy ? new Stock("spy").contract : new Stock("ivv").contract
   }
}
