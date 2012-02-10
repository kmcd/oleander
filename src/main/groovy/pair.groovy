class Pair {
   def spread(bids,asks) {
      if( bids.size() != asks.size() ) { return }
      if( bids.size() < 2 )            { return 0.0 }
      
      def spreads = [bids, asks].transpose().collect { it.first() - it.last() }
      
      spreads.last() - average(spreads)
   }
   
   def average(spreads) { (spreads.sum() / spreads.size()) }
}
