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
   
   static today(ticker) {
      def date = new Date().format "yyyy-MM-dd"
      find_all from:"$date 14:30:00", to:"$date 21:00:00", tickers:[ticker]
   }
   
   static todays_bids_asks() {
      def spy = today('spy_bid')
      def ivv = today('ivv_ask')
      def bids = []
      def asks = []
      
      spy.each { bid -> 
         def ask = ivv.find {  it['time_stamp'] == bid['time_stamp'] }
         if(ask) {
            bids << bid['spy_bid']
            asks << ask['ivv_ask']
         }
      }
      [bids, asks]
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
   
   static request_id(symbol, type) {
      def quote_type = [symbol, type].collect { it.toLowerCase() }.join('_')
      def id = quote_type.hashCode()
      request_ids[id] = quote_type
      id
   }
   
   static ticker_for(id)         { request_ids[id] }
   static parse_date(time_stamp) { Date.parse(timestamp_format, time_stamp) }
}
