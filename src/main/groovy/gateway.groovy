import groovy.util.logging.Log

@Log
class Gateway extends IbGateway {
   def orders = [:]
   def port = 7496
   def client_id = 1
   def next_order_id
   
   def connect() { client_socket.eConnect('localhost', port, client_id) }
   def disconnect() { if (client_socket.isConnected()) client_socket.eDisconnect() }
   
   public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
      orders[orderId] = status
   }
   
   public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
      if ( date =~ ~'finished' ) return
      def time_stamp = Date.parse("yyyyMMdd HH:mm:ss", date).format('yyyy-MM-dd HH:mm:ss:S')
      
      Quote.create(reqId, time_stamp, open, high, low, close, volume, WAP, count)
   }
   
   public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
      if (canAutoExecute) { Quote.create(tickerId, field, price) }
   }
   
   public void nextValidId(int orderId) { next_order_id = orderId }
   
   def real_time_quotes(stock) {
      client_socket.reqMktData( Quote.request_id(stock.symbol()), stock.contract, '', false )
   }
   
   def place_order(contract, order) {
      client_socket.reqIds(client_id)
      wait_for_response(100)
      client_socket.placeOrder(next_order_id, contract, order)
      
      log.info("[ORDER] type:${order.m_action} symbol:${contract.m_symbol} quantity:${order.m_totalQuantity} price:${order.m_lmtPrice}")
      
      next_order_id
   }
   
   def wait_for_response(milliseconds=250) { sleep(milliseconds) }
   def order_filled(order_id) { orders[order_id] =~ '(?i)filled' }
   def order_cancelled(order_id) { orders[order_id] =~ '(?i)cancelled' }
   def cancel(order_id) { client_socket.cancelOrder(order_id) }
}
