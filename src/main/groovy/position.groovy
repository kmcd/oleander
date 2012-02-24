import java.math.*

class Position {
   def short_spy = false
   def pair
   def spy
   def ivv
   def cover_price
   def sell_price
   def available = true
   def funding_available = 100000.00
   
   Position() {}
   
   Position(Pair new_pair) {
      pair = new_pair
      
      if( pair.short_spy ) {
         ivv = pair.opening_long_price
         spy = pair.opening_short_price
         short_spy = true
      }
      else {
         spy = pair.opening_long_price
         ivv = pair.opening_short_price
      }
   }
   
   def opening_prices_changed() {
      if(short_spy) {
         spy != Quote.current_bid('spy')  || ivv != Quote.current_ask('ivv') 
      }
      else { 
         spy != Quote.current_ask('spy')  || ivv != Quote.current_bid('ivv') 
      }
   }
   
   def exit_prices_changed() {
      if(short_spy) {
         cover_price != Quote.current_bid('spy')  || sell_price != Quote.current_ask('ivv') 
      }
      else { 
         sell_price != Quote.current_ask('spy')  || cover_price != Quote.current_bid('ivv') 
      }
   }
   
   def profitable(target=3.0) {
      if ( available ) { return false }
      def net_profit
      
      if(short_spy) {
         cover_price = Quote.current_bid('spy')
         sell_price = Quote.current_ask('ivv')
         net_profit = profit(spy:cover_price, ivv:sell_price) 
      }
      else { 
         sell_price = Quote.current_ask('spy')
         cover_price = Quote.current_bid('ivv')
         net_profit = profit(spy:sell_price, ivv:cover_price)
      }
      
      if( net_profit >= target ) { true } 
      else { sell_price = null ; cover_price = null }
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
