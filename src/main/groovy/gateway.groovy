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
      wait_for_next_request_id_from_gateway()
      client_socket.placeOrder(next_order_id, contract, order)
      next_order_id
   }
   
   def wait_for_next_request_id_from_gateway() { sleep(100) }
   
   def order_filled(order_id) {
      orders[order_id] =~ '(?i)filled'
   }
}
