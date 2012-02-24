import java.math.MathContext

class Pair {
   def short_spy
   def opening_long_price
   def opening_short_price
   
   def entry_signal() { long_entry_signal() || short_entry_signal() }
   
   def long_entry_signal() {
      def long_spread = Quote.repository.firstRow "select * from long_spread_quotes order by time_stamp desc limit 1"
      if( !long_spread ) { return false }
      
      def average = Quote.repository.firstRow("select avg(spread) as spread from long_spreads")['spread']
      def entry_signal = (average - 0.02).round( new MathContext(2) )
      
      if( long_spread['spread'] <= entry_signal ) {
         short_spy = false
         opening_long_price = long_spread['spy_ask']
         opening_short_price = long_spread['ivv_bid']
      }
   }
   
   // TODO: rename to long IVV
   def short_entry_signal() {
      def short_spread = Quote.repository.firstRow "select * from short_spread_quotes order by time_stamp desc limit 1"
      if( !short_spread ) { return false }
      
      def average = Quote.repository.firstRow("select avg(spread) as spread from short_spreads")['spread']
      def entry_signal = (average + 0.02).round( new MathContext(2) )
      
      if( short_spread['spread'] >= entry_signal ) {
         short_spy = true
         opening_long_price = short_spread['ivv_ask']
         opening_short_price = short_spread['spy_bid']
      }
   }
}
