import redis.clients.jedis.*
import groovy.json.*

// Saved in Redis as a sorted set:
// ["SPY", 242343 (UNIX epoch)] -> "time_stamp:'2012-12-31 09:30:00' close:124.44", "ticker":'SPY'

class Quote {
   static repository = new Jedis("localhost")
   static request_ids = [:]
   static timestamp_format = "yyyy-MM-dd HH:mm:ss"
   
   static find_all(args) {
      def from = parse_date(args['from']).time
      def to = parse_date(args['to']).time
      def json = new JsonSlurper()
      
      args['tickers'].collect {
         repository.zrangeByScore(it, from, to).collect { json.parseText(it) }
      }.flatten().sort { it['time_stamp'] }
   }
   
   static create(reqId, time_stamp, open, high, low, close, volume, wap, count) {
      create ticker_for(reqId) , time_stamp, close
   }
   
   static create(quote, time_stamp, price) {
      def ts = parse_date(time_stamp)
      def json = new groovy.json.JsonBuilder()
      json time_stamp:ts.format(timestamp_format), "${quote}":price
      
      repository.zadd quote, ts.time, json.toString()
   }
   
   static count(ticker) { repository.zcard ticker }
   
   static request_id(quote_type) {
      def id = quote_type.hashCode()
      request_ids[id] = quote_type
      id
   }
   
   static ticker_for(id)         { request_ids[id] }
   static parse_date(time_stamp) { Date.parse(timestamp_format, time_stamp) }
}
