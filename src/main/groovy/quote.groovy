import redis.clients.jedis.*
import groovy.json.*

// Saved in Redis as a sorted set:
// ["SPY", 242343 (UNIX epoch)] -> "time_stamp:'2012-12-31 09:30:00' close:124.44"

class Quote {
   static repository = new Jedis("localhost")
   static request_ids = [:]
   
   static find_all(args) {
   }
   
   static create(reqId, time_stamp, open, high, low, close, volume, wap, count) {
      create(quote_for(reqId, close), ticker_for(reqId), time_stamp)
   }
   
   static create(quotes, ticker, time_stamp) {
      def format = "yyyy-MM-dd HH:mm:ss"
      def ts = Date.parse(format, time_stamp)
      def quote = new groovy.json.JsonBuilder()
      quote time_stamp:ts.format(format), bid:quotes['bid'], ask:quotes['ask']
      
      repository.zadd ticker, ts.time, quote.toString()
   }
   
   static count(ticker) { repository.zcard ticker }
   
   static request_id(symbol, type) {
      def quote_type = [symbol, type].collect { it.toLowerCase() }.join(' ')
      def id = quote_type.hashCode()
      request_ids[id] = quote_type
      id
   }
   
   static quote_for(id, price)   { [ (request_ids[id][-3..-1]):price ] }
   static ticker_for(id)         { request_ids[id][0..2] }
}
