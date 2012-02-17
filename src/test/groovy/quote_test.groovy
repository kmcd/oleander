import groovy.util.GroovyTestCase
import groovy.sql.Sql

class QuoteTest extends GroovyTestCase {
   def price = 123.4567
   
   void setUp() {      
      Quote.repository = Sql.newInstance 'jdbc:postgresql://localhost:5432/hawk_moth_test'
      Quote.delete_all()
      Quote.request_ids = [:]
   }
   
   void test_request_id() {
      assert Quote.request_id('spy', 'bid') == 'spy_bid'.hashCode()
   }  
   
   void test_ticker_for_request_id() {
      assert Quote.ticker_for('spy_bid'.hashCode()) == null
      Quote.request_id('spy', 'bid')
      assert Quote.ticker_for('spy_bid'.hashCode()) == 'spy_bid'
   }
                               
   void test_fetch_quotes_by_date_range_for_ticker_pair() {
      Quote.create 'spy_bid', "2011-11-14 09:30:00", price
      Quote.create 'spy_ask', "2011-11-14 09:30:00", price
      Quote.create 'ivv_bid', "2011-11-14 09:30:00", price
      Quote.create 'ivv_ask', "2011-11-14 09:30:00", price
   }
}
