class Pair {
   def tickers = []
   def today = String.format('%tF', new Date())
   
   def spread(quotes) {
      spreads_for(today_only(quotes))
   }
   
   def today_only(quotes) {
      quotes.findAll { quote -> quote.key =~ ~"^${today}" }.
         sort { a,b -> parse_date(a.key) <=> parse_date(b.key) }
   }
   
   def spreads_for(current_quotes) {
      if( current_quotes.size() < 2 ) { return 0.0 }
      else {
         def spreads = [ bids_for("SPY", current_quotes), bids_for("IVV", current_quotes) ].
            transpose().collect { it.first() - it.last() }
         
         if(spreads.size() == 0) return 0.0
          
         spreads.last() - average(spreads)
      }
   }
   
   def bids_for(ticker, quotes) {
      quotes.findAll { quote ->
         // Ensure corresponding time stamp
         quote.key =~ ~"${ticker}" && quotes.count { it.key[0..-5] == quote.key[0..-5] } == 2
      }.collect { it.value["bid"] as Float } - null
   }
   
   // TODO: move to shared lib
   def parse_date(time_stamp) { Date.parse("yyyy-MM-dd HH:mm:ss", time_stamp ) }
   def average(spreads) { (spreads.sum() / spreads.size()) }
}
