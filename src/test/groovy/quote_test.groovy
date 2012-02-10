import groovy.util.GroovyTestCase

class QuoteTest extends GroovyTestCase {
   def price = 123.4567
   def id(type) { type.hashCode() }
   
   void setUp() {
      Quote.repository.select 1
      Quote.repository.flushDB()
   }
   
   void test_request_id() {
      assert Quote.request_id('spy', 'bid') == id('spy bid')
      assert Quote.request_id('spy', 'ask') == id('spy ask')
      assert Quote.request_id('ivv', 'bid') == id('ivv bid')
      assert Quote.request_id('ivv', 'ask') == id('ivv ask')
   }  
   
   void test_quote_for_request_id() {
      assert Quote.quote_for(id('spy bid'), price) == [bid:price]
      assert Quote.quote_for(id('spy ask'), price) == [ask:price]
      assert Quote.quote_for(id('ivv bid'), price) == [bid:price]
      assert Quote.quote_for(id('ivv ask'), price) == [ask:price]
   }  
   
   void test_ticker_for_request_id() {
      assert Quote.ticker_for(id('spy bid')) == 'spy'
      assert Quote.ticker_for(id('spy ask')) == 'spy'
      assert Quote.ticker_for(id('ivv bid')) == 'ivv'
      assert Quote.ticker_for(id('ivv ask')) == 'ivv'
   }  
   
   void test_unique_time_and_ticker() {
      Quote.create bid:price, ask:price, 'SPY', "2011-11-14 09:30:00"
      Quote.create bid:price, ask:price, 'SPY', "2011-11-14 09:30:00"
      
      assert Quote.count("SPY") == 1
   }  
   
   void test_fetch_quotes_by_date_range_for_ticker_pair() {
      Quote.create bid:price, ask:price, 'SPY', "2011-11-14 09:30:00"
      Quote.create bid:price, ask:price, 'IVV', "2011-11-14 09:30:00"
      Quote.create bid:price, ask:price, 'SPY', "2011-11-14 09:31:00"
      Quote.create bid:price, ask:price, 'IVV', "2011-11-14 09:31:00"
      
      // quotes = Quote.find_all( tickers:[SPY, IVV], from:"2011-11-14 09:30:00", 
         // to: => "2011-11-14 09:31:00" )
         // 
      // assert quotes.size() == 4
   }
}
