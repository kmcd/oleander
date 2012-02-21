import groovy.util.GroovyTestCase
import groovy.sql.Sql

class StockTest extends GroovyTestCase {
   def spy
   
   void setUp() {
      // TODO: dry up with quote test
      Quote.repository = Sql.newInstance 'jdbc:postgresql://localhost:5432/hawk_moth_test'
      Quote.delete_all()
      Quote.request_ids = [:]
      spy = new Stock('spy')
   }
   
   void test_fetch_current_bid() {
      Quote.create 'spy_bid', "2011-11-14 09:30:00:00", 123.45
      assert spy.bid() == 123.45
      
      Quote.create 'spy_bid', "2011-11-14 09:30:01:00", 543.21
      assert spy.bid() == 543.21
   }
   
   void test_fetch_current_ask() {
      Quote.create 'spy_ask', "2011-11-14 09:30:00:00", 123.45
      assert spy.ask() == 123.45
      
      Quote.create 'spy_ask', "2011-11-14 09:30:01:00", 543.21
      assert spy.ask() == 543.21
   }
}
