import groovy.sql.Sql
import groovy.sql.DataSet
import groovy.util.logging.Log

@Log
class Quote {
   static repository = Sql.newInstance 'jdbc:postgresql://localhost:5432/hawk_moth'
   static request_ids = [:]
   static date_format = 'yyyy-MM-dd HH:mm:ss:S'
   
   static current_bid(ticker) { 
      repository.firstRow("select price from ${Sql.expand(ticker)}_bid order by time_stamp desc limit 1")['price'] 
   }
   
   static current_ask(ticker) { 
      // TODO: dry up with bid
      repository.firstRow("select price from ${Sql.expand(ticker)}_ask order by time_stamp desc limit 1")['price']
   }
   
   static create(reqId, time_stamp, open, high, low, close, volume, wap, count) {
      create ticker_for(reqId) , time_stamp, close
   }
   
   static create(quote, time_stamp, price) {
      try {
         repository.dataSet(quote).add( price:price,
         time_stamp:parse_date(time_stamp).toTimestamp())
      } catch(e) { log.info(e.toString()) }
   }
   
   static create(int reqId, tick_type, price) {
      def ticker = ticker_for(reqId)
      def time_stamp = new Date().format(date_format)
      def type = ''
      
      switch (tick_type) {
         case 1 : type = 'bid'; break
         case 2 : type = 'ask'; break
      }
      
      create "${ticker}${type}", time_stamp, price
   }
   
   static request_id(symbol, type='') {
      def quote_type = [symbol, type].collect { it.toLowerCase() }.join('_')
      def id = quote_type.hashCode()
      request_ids[id] = quote_type
      id
   }
   
   static ticker_for(id)         { request_ids[id] }
   static parse_date(time_stamp) { Date.parse(date_format, time_stamp) }
   
   static delete_all() {
      [ 'spy_bid', 'spy_ask', 'ivv_bid', 'ivv_ask', 'long_spreads', 'short_spreads' ].each { 
         Quote.repository.execute "delete from ${Sql.expand(it)}"
      }
   }
}
