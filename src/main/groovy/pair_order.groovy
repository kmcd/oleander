import java.math.MathContext
import com.ib.client.Order
import Gateway

class PairOrder {
   def gateway
   def position
   
   PairOrder(ib_gateway) {
      gateway = ib_gateway
   }
   
   def enter(new_position) {
      position = new_position
      
      gateway.place_order(long_contract(), long_order())
      
      // If buy order succesful
      // OCA order:
      //    SHORT IVV
      //    LMT -1 SPY (stop loss @ -7.27)
      gateway.place_order(short_contract(), short_order())
      
         // If sell order fails: wait OR market ? nbbo +/-
   }
   
   def exit(spy_price, ivv_price) {
      def cover_price = position.short_spy ? spy_price : ivv_price
      def sell_price = position.short_spy ? ivv_price : spy_price
      
      gateway.place_order(long_contract(), sell_order(sell_price) )
      gateway.place_order(short_contract(), cover_order(cover_price) )
      
      position.available = false
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
      position.short_spy ? new Stock("IVV").contract : new Stock("SPY").contract
   }
   
   def short_contract() {
      position.short_spy ? new Stock("SPY").contract : new Stock("IVV").contract
   }
}
