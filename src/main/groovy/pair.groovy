class Pair {
   def entry_signal() { 
      long_entry_signal() || short_entry_signal() 
   }
   
   def long_entry_signal() {
      def long_spread = Quote.repository.firstRow "select spread from long_spreads order by time_stamp desc limit 1"
      if( !long_spread ) { return false }
      
      def long_entry = Quote.repository.firstRow("select long_entry()")['long_entry']
      long_spread['spread'] >= long_entry
   }
   
   def short_entry_signal() {
      def short_spread = Quote.repository.firstRow "select spread from short_spreads order by time_stamp desc limit 1"
      if( !short_spread ) { return false }
      
      def short_entry = Quote.repository.firstRow("select short_entry()")['short_entry']
      short_spread['spread'] >= short_entry
   }
}
