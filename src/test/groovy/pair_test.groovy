import groovy.util.GroovyTestCase
import groovy.sql.Sql

class PairTest extends GroovyTestCase {
   def pair
   
   void setUp() {
      Quote.repository = Sql.newInstance 'jdbc:postgresql://localhost:5432/hawk_moth_test'
      Quote.delete_all()
      Quote.request_ids = [:]
      
      pair = new Pair()
   }
           
   void test_detect_long_entry_signal() {
      new File("./src/test/resources/long_spreads.csv").splitEachLine(",") {
         def time_stamp = it[0]
         Quote.create 'spy_ask', time_stamp, it[-2] as Float
         Quote.create 'ivv_bid', time_stamp, it[-1] as Float
         
         if(time_stamp =~ ~"2012-02-21 16:50:10") { assert !pair.entry_signal() }
         if(time_stamp =~ ~"2012-02-21 16:54:16") { assert pair.entry_signal() }
      }
   }
   
   void test_detect_short_entry_signal() {
      new File("./src/test/resources/short_spreads.csv").splitEachLine(",") {
         def time_stamp = it[0]
         Quote.create 'spy_bid', time_stamp, it[-2] as Float
         Quote.create 'ivv_ask', time_stamp, it[-1] as Float
         
         if(time_stamp =~ ~"2012-02-21 16:50:10") { assert !pair.entry_signal() }
         if(time_stamp =~ ~"2012-02-21 16:55:47") { assert pair.entry_signal() }
         if(time_stamp =~ ~"2012-02-21 17:00:11") { assert pair.entry_signal() }
      }
   }
}