import groovy.util.GroovyTestCase
import groovy.mock.interceptor.StubFor

class MarketTest extends GroovyTestCase {
   def marketStub = new StubFor(Market)
   
   def assert_closed(time_stamp) {
      // marketStub.demand.now { Date.parse("yyyy-MM-dd HH:mm", time_stamp) }
      // marketStub.use { assert Market.closed() }
   }
   
   def assert_open(time_stamp) {
      // marketStub.demand.now { Date.parse("yyyy-MM-dd HH:mm", time_stamp) }
      // marketStub.use { assert Market.open() }
   }
   
   void test_open_on_week_days() {
      (6..10).each { week_day ->
         assert_open "2012-02-${week_day} 14:30"
      }
   }
   
   void test_closed_on_week_ends() {
      (11..13).each { week_end ->
         assert_closed "2012-02-${week_end} 14:30"
      }
   }
   
   void test_opening_time() {
      assert_closed "2012-02-06 14:29"
      assert_open "2012-02-06 14:30"
      assert_open "2012-02-06 14:31"
   }
   
   void test_closing_time() {
      assert_open "2012-02-06 20:59"
      assert_closed "2012-02-06 21:00"
      assert_closed "2012-02-06 21:01"
   }
   
   void test_closing_minute() {
      assert_open "2012-02-06 20:58"
      // marketStub.use { assert !Market.closing_minute() }
      
      assert_open "2012-02-06 20:59"
      // marketStub.use { assert Market.closing_minute() }
      
      assert_closed "2012-02-06 21:00"
      // marketStub.use { assert !Market.closing_minute() }
   }
}