gateway = new Gateway()
spy = new Stock("spy", gateway)
ivv = new Stock("ivv", gateway)
position = new Position(open:false)
pair_order = new PairOrder(gateway)
pair = new Pair()

gateway.connect()

// TODO: refactor to fetch_todays_quotes 'spy', 'ivv'
gateway.fetch_todays_quotes spy, 'bid'
gateway.fetch_todays_quotes ivv, 'bid'
gateway.fetch_todays_quotes spy, 'ask'
gateway.fetch_todays_quotes ivv, 'ask'

// TODO: refactor to fetch_real_time_bars 'spy', 'ivv'
gateway.real_time_bars spy, 'bid'
gateway.real_time_bars ivv, 'bid'
gateway.real_time_bars spy, 'ask'
gateway.real_time_bars ivv, 'ask'

// Place a synchronised pair order

// position = new Position(spy:spy.ask(), ivv:ivv.bid(), open:true)
// gateway.client_socket.reqIds(1)
// contract = new Stock("spy").contract
// order = pair_order.order("BUY", position.long_shares(), position.opening_long_price())
// gateway.client_socket.placeOrder(gateway.next_order_id, contract, order)
// gateway.client_socket.reqIds(1)
// contract = new Stock("ivv").contract
// order = pair_order.order("SELL", position.short_shares(), position.opening_short_price())
// gateway.client_socket.placeOrder(gateway.next_order_id, contract, order)
// 
// position = new Position(spy:spy.ask(), ivv:ivv.bid(), open:true, short_spy:true)
// gateway.client_socket.reqIds(1)
// contract = new Stock("spy").contract
// order = pair_order.order("BUY", position.short_shares(), position.opening_short_price())
// gateway.client_socket.placeOrder(gateway.next_order_id, contract, order)
// gateway.client_socket.reqIds(1)
// contract = new Stock("ivv").contract
// order = pair_order.order("SELL", position.long_shares(), position.opening_long_price())
// gateway.client_socket.placeOrder(gateway.next_order_id, contract, order)

// pair_order.enter(position)
// pair_order.exit(spy.bid(), ivv.bid())

// Paper trade
while(true) {
   if ( position.open ) {
      spy_ask = position.profit_target(ivv.bid(), 10.0)
      pair_order.exit(spy_ask, ivv.bid())
   } 
   else {
      spy_bid = spy.bid() - 0.03 - Pair.spread(Quote.current_bids_asks())
      position = new Position(spy:spy_bid, ivv:ivv.ask())
      
      // Ping for spy liquidity; short ivv marketable limit order
      // If ivv cover fails: wait OR market ? nbbo +/-
      pair_order.enter(position)
   }
}
