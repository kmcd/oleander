import groovy.util.GroovyTestCase

class QuoteTest extends GroovyTestCase {
   def price = 123.4567
   
   void setUp() {      
      Quote.repository.select 1
      Quote.repository.flushDB()
      Quote.request_ids = [:]
   }
   
   void test_request_id() {
      assert Quote.request_id('spy_bid') == 'spy_bid'.hashCode()
   }  
   
   void test_ticker_for_request_id() {
      assert Quote.ticker_for('spy_bid'.hashCode()) == null
      Quote.request_id('spy_bid')
      assert Quote.ticker_for('spy_bid'.hashCode()) == 'spy_bid'
   }
                               
   void test_unique_time_and_ticker() {
      Quote.create 'spy_bid', "2011-11-14 09:30:00", price
      Quote.create 'spy_bid', "2011-11-14 09:30:00", price
                                                 
      assert Quote.count("spy_bid") == 1
   }  
   
   void test_fetch_quotes_by_date_range_for_ticker_pair() {
      Quote.create 'spy_bid', "2011-11-14 09:30:00", price
      Quote.create 'spy_ask', "2011-11-14 09:30:00", price
      Quote.create 'ivv_bid', "2011-11-14 09:30:00", price
      Quote.create 'ivv_ask', "2011-11-14 09:30:00", price
      Quote.create 'spy_bid', "2011-11-14 09:32:00", price  
      
      assertEquals(
         [ 
            [spy_bid:price, time_stamp:"2011-11-14 09:30:00"],
            [spy_ask:price, time_stamp:"2011-11-14 09:30:00"],
            [ivv_bid:price, time_stamp:"2011-11-14 09:30:00"],
            [ivv_ask:price, time_stamp:"2011-11-14 09:30:00"]
         ],                                                
         Quote.find_all(from:"2011-11-14 09:30:00", to:"2011-11-14 09:31:00", 
            tickers:['spy_bid', 'spy_ask', 'ivv_bid', 'ivv_ask'])
      )
   }
}
