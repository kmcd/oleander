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
   
   public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
      def time_stamp = (new Date((time) * 1000)).format('yyyy-MM-dd HH:mm:ss')
      
      Quote.create(reqId, time_stamp, open, high, low, close, volume, wap, count)
   }
   
   public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
      if ( date =~ ~'finished' ) return
      def time_stamp = Date.parse("yyyyMMdd  HH:mm:ss", date).format('yyyy-MM-dd HH:mm:ss')
      
      Quote.create(reqId, time_stamp, open, high, low, close, volume, WAP, count)
   }
   
   public void nextValidId(int orderId) { next_order_id = orderId }
   
   def real_time_bars(stock, type) {
      client_socket.reqRealTimeBars(
         Quote.request_id(stock.symbol(), type), stock.contract, 5, type, true )
   }
   
   def place_order(contract, order) {
      client_socket.reqIds(1)
      client_socket.placeOrder(next_order_id, contract, order)
      
      log.info("[ORDER] type:${order.m_action} symbol:${contract.m_symbol} quantity:${order.m_totalQuantity} price:${order.m_lmtPrice}")
   }
   
   def fetch_todays_quotes(stock,type) {
      def today = new Date().format("yyyyMMdd HH:mm:ss z") as String
      
      client_socket.reqHistoricalData( Quote.request_id(stock.symbol(), type),
         stock.contract, today, "1 D", "30 secs", type, 1, 1 )
   }
}
