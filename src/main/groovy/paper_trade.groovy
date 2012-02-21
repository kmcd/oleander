gateway = new Gateway()
spy = new Stock("spy")
ivv = new Stock("ivv")
position = new Position()
pair_order = new PairOrder(gateway)

gateway.connect()
Quote.delete_all()
gateway.real_time_quotes spy
gateway.real_time_quotes ivv

while(true) {
   if ( position.profitable() ) { pair_order.exit( position ) } 
   
   if ( position.available && pair.entry_signal() ) {
      position = new Position(pair)
      pair_order.enter( position )
   }
}
