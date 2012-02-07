import com.ib.client.Contract
import com.ib.client.ContractDetails
import com.ib.client.EClientSocket
import com.ib.client.EWrapper
import com.ib.client.EWrapperMsgGenerator
import com.ib.client.Execution
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.client.UnderComp
import java.util.concurrent.ConcurrentHashMap
import groovy.util.logging.Log

@Log
class Gateway extends IbGateway {
   def quotes = new ConcurrentHashMap()
   def orders = new ConcurrentHashMap()
   def port = 7496
   def client_id = 1
   def client_socket = new EClientSocket(this)
   def requested_real_time_bars = 0
   
   def connect() { client_socket.eConnect('localhost', port, client_id) }
   def disconnect() { if (client_socket.isConnected()) client_socket.eDisconnect() }
   
   public void orderStatus( int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
      orders[orderId] = status.trim.toLowerCase() == 'filled'
   }
   
   public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
      def time_stamp = (new Date((time) * 1000)).format('yyyy-MM-dd HH:mm:ss')
      
      switch(reqId) {
         case 1: quotes["${time_stamp} SPY"] = [bid:close] ; break
         case 2: quotes["${time_stamp} IVV"] = [bid:close] ; break
         case 3: quotes["${time_stamp} SPY"] = [ask:close] ; break
         case 4: quotes["${time_stamp} IVV"] = [ask:close] ; break
      }
   }
   
   public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
      if ( date =~ ~'finished' ) return
         
      def time_stamp = Date.parse("yyyyMMdd  HH:mm:ss", date).format('yyyy-MM-dd HH:mm:ss')
      def bar_volume = volume as long
      
      switch(reqId) {
         case 1: quotes["${time_stamp} SPY"] = [bid:close] ; break
         case 2: quotes["${time_stamp} IVV"] = [bid:close] ; break
      }  // TODO: dry up wrt realtimeBar()
   }
   
   def real_time_bars(stock, type) {
      def request_id
      
      switch(stock.symbol()) {
         case "SPY":  request_id = type == "BID" ? 1 : 3 ; break
         case "IVV":  request_id = type == "BID" ? 2 : 4 ; break
      }  // TODO: dry up wrt realtimeBar()
       
      client_socket.reqRealTimeBars request_id, stock.contract, 5, type, true
   }
   
   def place_order(contract,order) {
      client_socket.placeOrder(nextValidId(), contract, order)
      log.info("[ORDER] type:${order.m_action} symbol:${contract.m_symbol} quantity:${order.m_totalQuantity} price:${order.m_lmtPrice}")
   }
   
   def cancel_last_two_orders_unless_both_filled() {
      // Ensure enough time (eg sleep(2)) for orders to fill
      
      if ( ! last_two_orders_both_filled() ) {
         client_socket.cancelOrder( last_two_orders[0] )
         client_socket.cancelOrder( last_two_orders[1] )
         
         log.info("[ORDER] type:cancel ids:${last_two_orders}")
      }
   }
   
   def fetch_todays_quotes(stock,type) {
      def today = new Date().format("yyyyMMdd HH:mm:ss z") as String
      def request_id = stock.symbol() == 'SPY' ? 1 : 2
      
      client_socket.reqHistoricalData(request_id, stock.contract, today, "1 D", "30 secs", type, 1, 1)
   }
   
   def last_two_orders() {
      def orders = orders.keySet().toArray()
      orders[-1..-2]
   }
   
   def last_two_orders_both_filled() {
      last_two_orders.every { it }
   }
}
