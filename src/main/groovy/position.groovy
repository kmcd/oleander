import java.math.*

class Position {
   def short_spy = false
   def spy
   def ivv
   def open
   def funding_available = 100000.00
   
   def spy_profit_target(ivv_ask, target) {
      def spy_bid = opening_long_price() - 10.0
      while( profit(spy:spy_bid, ivv:ivv_ask) < target ) { spy_bid += 0.01 }
      spy_bid
   }
   
   def profit(tickers) {
      def net_profit
      
      if(short_spy) {
         net_profit = short_at(tickers['spy']) + long_at(tickers['ivv'])
      }
      else {
         net_profit = long_at(tickers['spy']) + short_at(tickers['ivv'])
      }
      
      net_profit - commissons()
   }
   
   def long_at(price) {
      (long_shares() * price) - opening_long_portfolio()
   }
   
   def short_at(price) { 
      opening_short_portfolio() - (short_shares() * price)
   }
   
   def opening_long_price() {
      short_spy ? ivv : spy
   }
   
   def opening_short_price() {
      short_spy ? spy : ivv
   }
   
   def opening_long_portfolio() {
      long_shares() * opening_long_price()
   }
   
   def opening_short_portfolio() {
      short_shares() * opening_short_price()
   }
   
   def pair_fund() { 
      (funding_available * 0.99) / 2 
   }
   
   def long_shares() {
      round(pair_fund() / opening_long_price())
   }
   
   def short_shares() {
      round(pair_fund() / opening_short_price())
   }
   
   def round(decimal) {
      (int)((double)(decimal)).round(0)
   }
   
   def commissons() {
      2 * (long_shares() + short_shares()) * 0.005
   }
}
