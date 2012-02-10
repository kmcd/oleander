import Gateway
import Position
import Pair

def gateway = new Gateway()
def spy = new Stock("SPY", gateway)
def ivv = new Stock("IVV", gateway)
def position = new Position(open:false)
def pair_order = new PairOrder(gateway)
def pair = new Pair(tickers:['SPY','IVV'])

gateway.connect()
gateway.fetch_todays_quotes spy, 'BID'
gateway.fetch_todays_quotes ivv, 'BID'

gateway.real_time_bars spy, 'BID'
gateway.real_time_bars ivv, 'BID'
gateway.real_time_bars spy, 'ASK'
gateway.real_time_bars ivv, 'ASK'

sleep(5000) // Wait for real time quotes

while(true) {
   if ( Market.closed() ) { continue }

   if ( position.open ) {
      if (position.profit(spy:spy.ask(), ivv:ivv.ask()) >= 10.0) {
         pair_order.exit(spy.bid(), ivv.bid())
      }
      if ( Market.closing_minute() ) { pair_order.exit(position, spy_price, ivv_price) }
   }

   if ( ! Market.closing_minute() ) {
      spread = pair.spread(gateway.quotes)
      println "[SPREAD] ${new Date().format('yyyy-MM-dd HH:mm:ss')} spy:${spy.bid()} ivv:${ivv.bid()} ${spread}"

      if( spread <= -0.03 ||  spread >= 0.03 )  {
         position = new Position(spy:spy.ask(), ivv:ivv.ask(), open:true, short_spy: spread >= 0.03)
         pair_order.enter(position)
      }
   }
   
   sleep(2000)
}
