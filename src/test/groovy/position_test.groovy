import groovy.util.GroovyTestCase

class PositionTest extends GroovyTestCase {
   def position
   
   void setUp() {
      position = new Position(spy:133.57, ivv:134.07, open:"2011-07-08 11:09:00")
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
      assert position.profit(spy:133.57, ivv:134.07) < position.funding_available
   }
   
   void test_no_fractional_shares() {
      assertEquals(0, position.long_shares() % 1)
      assertEquals(0, position.short_shares() % 1)
   }
}
