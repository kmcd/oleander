import groovy.util.GroovyTestCase
import groovy.mock.interceptor.StubFor
import groovy.sql.Sql

class PositionTest extends GroovyTestCase {
   def position
   
   void setUp() {
      Quote.repository = Sql.newInstance 'jdbc:postgresql://localhost:5432/hawk_moth_test'
      Quote.delete_all()
      Quote.request_ids = [:]
      position = new Position(spy:133.57, ivv:134.07)
   }
   
   void test_calculate_current_profit() {
      assertEquals( -7.39, position.profit(spy:133.57, ivv:134.07), 0.1)
      assertEquals( 9.19, position.profit(spy:133.565, ivv:134.02), 0.1)
      assertEquals( 11.03, position.profit(spy:133.56, ivv:134.01), 0.1)
   }
   
   void test_dollar_neutral() {
      assert (position.long_at(133.57) - position.short_at(134.07)) < 133.57
      assert (position.long_at(133.57) - position.short_at(134.07)) < 134.07
   }
   
   void test_never_exceeed_total_funds() {
      assert (position.long_at(133.57) + position.short_at(134.07)) < position.funding_available
      assert (position.long_at(133.57) + position.short_at(134.07)) < position.funding_available
   }
   
   void test_no_fractional_shares() {
      assertEquals(0, position.long_shares() % 1)
      assertEquals(0, position.short_shares() % 1)
   }
   
   void test_spy_profitability() {
      Quote.create 'spy_ask', "2011-11-14 09:30:00:00", 133.57
      Quote.create 'ivv_bid', "2011-11-14 09:30:00:00", 134.07
      
      assert position.profitable() == false
      
      Quote.create 'spy_ask', "2011-11-14 09:30:01:00", 133.565
      Quote.create 'ivv_bid', "2011-11-14 09:30:01:00", 134.02
      position.available = false
      
      assert position.profitable() == true
   }
   
   void test_spy_profitable_only_when_available() {
      Quote.create 'spy_ask', "2011-11-14 09:30:01:00", 133.565
      Quote.create 'ivv_bid', "2011-11-14 09:30:01:00", 134.02
      
      position.available = true
      assert position.profitable() == false
      
      position.available = false
      assert position.profitable() == true
   }
}
