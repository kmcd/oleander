gateway = new Gateway()
spy = new Stock("spy", gateway)
ivv = new Stock("ivv", gateway)

gateway.connect()
Quote.delete_all()

gateway.real_time_quotes spy
gateway.real_time_quotes ivv

position = new Position(open:false)
pair_order = new PairOrder(gateway)

// Paper trade
while(true) {
   if ( position.profitable() ) {
      pair_order.exit( position )
   } 
   else {
      // if entry signal
      pair_order.enter( position )
   }
}
