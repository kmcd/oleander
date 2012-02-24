gateway = new Gateway(client_id:2)
gateway.connect()

position = new Position()
pair = new Pair() 

while(true) {
   if ( position.available && pair.entry_signal() ) {
      position = new Position( pair )
      pair_order = new PairOrder( gateway )
      pair_order.enter( position )
   }
   
   if ( position.profitable() ) { pair_order.exit() }
}
