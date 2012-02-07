// TODO: unit test
class Market {
   public static open() {
      def now = new Date()
      
      if ( !(1..5).contains(now.day) ) { return false }
      if ( now.hours <= 14 && now.minutes < 30 )  { return false }
      if ( now.hours >= 21 && now.minutes >= 00 ) { return false }
      
      return true
   }
   
   public static closing_minute() {
      def now = new Date()
      now.hours == 20  && now.minutes == 59
   }
   
   public static closed() { ! open() }
}
