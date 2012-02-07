import Gateway
import Position
import Pair

gateway = new Gateway()
spy = new Stock("SPY", gateway)
ivv = new Stock("IVV", gateway)
position = new Position(open:false)
pair_order = new PairOrder(gateway)
pair = new Pair(tickers:['SPY','IVV'])

gateway.connect()
gateway.fetch_todays_quotes spy, 'BID'
gateway.fetch_todays_quotes ivv, 'BID'

gateway.real_time_bars spy, 'BID'
gateway.real_time_bars ivv, 'BID'
gateway.real_time_bars spy, 'ASK'
gateway.real_time_bars ivv, 'ASK'

sleep(10) // Wait for real time quotes

while(true) {
   if ( Market.closed() ) { continue }

   if ( position.open ) {
      if (position.profit(spy:spy.ask_price(), ivv:spy.ask_price()) >= 10.0) {
         pair_order.exit(spy.ask_price(), ivv.ask_price())
      }               
      if ( Market.closing_minute() ) { exit(position, spy_price, ivv_price) }
   }

   if ( ! Market.closing_minute() ) {
      spread = pair.spread(gateway.quotes)

      if( spread <= -0.03 || spread >= 0.03 )  {
         position = new Position(SPY:spy.ask_price(), IVV:ivv.ask_price(), open:true)
         pair_order.enter(position)
      }
   }
   
   sleep(2)
}
