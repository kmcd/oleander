gateway = new Gateway()
spy = new Stock("spy", gateway)
ivv = new Stock("ivv", gateway)

gateway.connect()
Quote.delete_all()

gateway.streaming_quotes spy
gateway.streaming_quotes ivv

position = new Position(open:false)
pair_order = new PairOrder(gateway)

// Paper trade
// while(true) {
   // if ( position.open ) {
      // spy_bid = position.spy_profit_target(ivv.ask(), 5.0)
      // pair_order.exit(spy.bid(), ivv.ask() -0.01)
   // } 
   // else {
      // bids_asks = Quote.current_bids_asks()
      // spy_ask = spy.ask() - 0.01 - pair.spread(Quote.current_bids_asks())
      // ivv_bid = ivv.bid() + ( 0.01 + pair.spread(bids_asks) )
      // 
      // position = new Position(spy:spy.ask()-0.01, ivv:ivv.bid()+0.01)
      // pair_order.enter(new Position(spy:spy.ask(), ivv:ivv.bid()))
   // }
// }
