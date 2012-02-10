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
