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
      }.flatten().inject([:]) { quotes, quote ->
         def key = [ quote['time_stamp'], quote['ticker'] ].join(' ')
         quotes[ key ] = [ bid:quote['bid'], ask:quote['ask'] ]
         quotes
      }.sort { it.key }
   }
   
   static create(reqId, time_stamp, open, high, low, close, volume, wap, count) {
      create(quote_for(reqId, close), ticker_for(reqId), time_stamp)
   }
   
   static create(quotes, ticker, time_stamp) {
      def ts = parse_date(time_stamp)
      def json = new groovy.json.JsonBuilder()
      def quote = [ time_stamp:ts.format(timestamp_format), ticker:ticker ]
      ['bid', 'ask'].each { if(quotes[it]) quote.put(it,quotes[it]) }
      
      json quote
      repository.zadd ticker, ts.time, json.toString()
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
   static parse_date(time_stamp) { Date.parse(timestamp_format, time_stamp) }
}
