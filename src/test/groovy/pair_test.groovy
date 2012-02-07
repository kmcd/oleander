import groovy.util.GroovyTestCase

class PairTest extends GroovyTestCase {
   def pair
   def quotes = [:]
   def today = String.format('%tF', new Date())
   
   def quote(time,ticker, price) {
      quotes["$today $time $ticker"] = ["bid":price]
   }
   
   void setUp() {
      pair = new Pair(tickers:['SPY','IVV'])
   }
   
   void test_spread_of_zero_on_market_open() {
      quote "09:30:00", "SPY", 133.97
      quote "09:30:00", "IVV", 134.4036
    
      assert pair.spread(quotes) == 0.0
   }
   
   void test_calculate_difference_between_current_spread_and_moving_average() {
      quote "09:30:00", "SPY", 133.97
      quote "09:30:00", "IVV", 134.4036
      quote "09:31:00", "SPY", 134.01
      quote "09:31:00", "IVV", 134.46
      
      assertEquals(-0.0082, pair.spread(quotes), 0.0001)
   }
   
   void test_calculate_moving_average_from_market_open() {
      quote "09:30:00", "SPY", 133.97
      quote "09:30:00", "IVV", 134.4036
      quote "09:31:00", "SPY", 134.01	
      quote "09:31:00", "IVV", 134.46
      quote "09:32:00", "SPY", 133.98
      quote "09:32:00", "IVV", 134.451
      
      assertEquals(-0.0195, pair.spread(quotes), 0.0001)
   }
   
   void test_only_use_current_day_for_spread_calculations() {
      quotes["2011-11-14 16:00:00 SPY"] = ["bid":133.97	]
      quotes["2011-11-14 16:00:00 IVV"] = ["bid":134.4036	]
      quotes["2011-11-15 09:30:00 SPY"] = ["bid":134.01	]
      quotes["2011-11-15 09:30:00 IVV"] = ["bid":134.46	]
      
      assertEquals(0.0, pair.spread(quotes))
   }
   
   void test_ignore_missing_bars() {
      quote "09:30:00", "SPY", 133.97
      quote "09:30:00", "IVV", 134.4036
      quote "09:31:00", "SPY", 134.01
      quote "09:32:00", "SPY", 134.01	
      quote "09:32:00", "IVV", 134.46
      
      assertEquals(-0.0082, pair.spread(quotes), 0.0001)
   }
   
   void test_ignore_erroneous_bars() {
      quote "09:30:00", "SPY", 133.97
      quote "09:39:00", "IVV", 134.4036
      quote "09:31:00", "SPY", 134.01	
      quote "09:38:00", "IVV", 134.46
      quote "09:32:00", "SPY", 133.98
      quote "09:37:00", "IVV", 134.451
      
      assertEquals(0.0, pair.spread(quotes))
   }
   
   void test_detect_a_high_low_spread() {
      new File("./src/test/resources/fixtures.csv").splitEachLine(",") {
         def time_stamp = it[0]
         quote time_stamp, "SPY", it[-2]
         quote time_stamp, "IVV", it[-1]
         
         if(time_stamp =~ ~"10:59:00") { assertEquals( -0.0319, pair.spread(quotes), 0.0001) }
         if(time_stamp =~ ~"11:09:00") { assertEquals( -0.0319, pair.spread(quotes), 0.0001) }
         if(time_stamp =~ ~"11:40:00") { assertEquals( 0.0359, pair.spread(quotes), 0.0001) }
      }
   }
}