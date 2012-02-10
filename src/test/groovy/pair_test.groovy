import groovy.util.GroovyTestCase

class PairTest extends GroovyTestCase {
   def pair
   def bids
   def asks
   def today = String.format('%tF', new Date())
   def bid(price) { bids << price }
   def ask(price) { asks << price }
   
   void setUp() {
      pair = new Pair()
      bids = []
      asks = []
   }
   
   void test_spread_of_zero_on_market_open() {
      bid 133.97
      ask 134.4036
    
      assert pair.spread(bids,asks) == 0.0
   }
   
   void test_calculate_difference_between_current_spread_and_moving_average() {
      bid 133.97
      ask 134.4036
      bid 134.01
      ask 134.46
      
      assertEquals(-0.0082, pair.spread(bids,asks), 0.0001)
   }
           
   void test_detect_a_high_low_spread() {
      new File("./src/test/resources/fixtures.csv").splitEachLine(",") {  
         def time_stamp = it[0]
         bid  it[-2] as Float
         ask  it[-1] as Float
         
         if(time_stamp =~ ~"10:59:00") { assertEquals( -0.0319, pair.spread(bids,asks), 0.0001) }
         if(time_stamp =~ ~"11:09:00") { assertEquals( -0.0319, pair.spread(bids,asks), 0.0001) }
         if(time_stamp =~ ~"11:40:00") { assertEquals( 0.0359, pair.spread(bids,asks), 0.0001) }
      }
   }
}