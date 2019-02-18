/*------------------------------------------------------------------------------------------
	Timer.java
	C	Sep 17 1999
	U	Nov 05 2003 .13

     Sep 17 1999    Initial.
     Dec 18 1999    Include in a general utility package named UTIL.
     Apr 13 2001    Fix some buggy things !!
     Mar 08 2002    Integrated with CVS ::
     Aug 09 2003    Modified to be integrated in gaia's prototype class hierarchy
     Oct 31 2003    Package redefinition
     Nov 05 2003    Add some methods to get useful info
     May 02 2015    Stop import of io.*
     Feb 18 2019    VScode integration
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.log;

import java.io.Serializable;

public class Timer implements Serializable {
    @SuppressWarnings("compatibility:6831337959806484396")
    private static final long serialVersionUID = -6447865008221524623L;

    private static final     String              Version = "Timer V 1.18, May 02 2015 ";

     protected long  timercreationtime;    // Used to measure elapsed time
     protected long  timerlastmeasuredtime;

     //---------------------------------------------------------------
    //---------------------------------------------------------------
    // Constructor
    public Timer() {
          ResetTimer();
     }
     public Timer(long predefined) { // New constructor to set a timer to a predefined value
          ResetTimer();
          timerlastmeasuredtime = predefined;
     }
     //---------------------------------------------------------------
     // returns the nbr of msec elapsed since the timer was created
     //---------------------------------------------------------------
     public long getTimerValue(){
          timerlastmeasuredtime = System.currentTimeMillis();
          return  timerlastmeasuredtime - timercreationtime;
     }
     //---------------------------------------------------------------
     // returns a string in format HH:MM:SS.mmm giving the time since
     // the timer was resetted
     //---------------------------------------------------------------
     public String getTimerString(){
          long elapsed;


          timerlastmeasuredtime = System.currentTimeMillis();
          elapsed = timerlastmeasuredtime - timercreationtime;
          // Finish that : need to convert to a valid String
          long Hours = elapsed / (60 * 60 * 1000);
          elapsed -= Hours * 60 * 60 * 1000;
          long Minutes = elapsed / (60 * 1000);
          elapsed -= Minutes * 60 * 1000;
          long Seconds = elapsed / 1000;
          elapsed -= Seconds * 1000;
          long Milliseconds = elapsed;
          //
          //  Format milliseconds, always using three digits.
          //  The equivalent of a printf("%03d") in C
          //
          getThreeDigits(Milliseconds);
          return  String.valueOf(Hours) + ":" + String.valueOf(Minutes) +
    					":" + String.valueOf(Seconds) + "."  +
                         getThreeDigits(Milliseconds);
     }
     //---------------------------------------------------------------
     // returns the nbr of msec elapsed since the timer was created
     //---------------------------------------------------------------
     public long getTimerAbsoluteValue(){
          return  timerlastmeasuredtime - timercreationtime;
     }
      //---------------------------------------------------------------
      // returns a string in format HH:MM:SS.mmm giving the time since
      // the timer was resetted
      //---------------------------------------------------------------
      public String getTimerAbsoluteString(){
           long elapsed;

    
           elapsed = timerlastmeasuredtime - timercreationtime;
           // Finish that : need to convert to a valid String
           long Hours = elapsed / (60 * 60 * 1000);
           elapsed -= Hours * 60 * 60 * 1000;
           long Minutes = elapsed / (60 * 1000);
           elapsed -= Minutes * 60 * 1000;
           long Seconds = elapsed / 1000;
           elapsed -= Seconds * 1000;
           long Milliseconds = elapsed;
           //
           //  Format milliseconds, always using three digits.
           //  The equivalent of a printf("%03d") in C
           //
           getThreeDigits(Milliseconds);
           return  String.valueOf(Hours) + ":" + String.valueOf(Minutes) +
               ":" + String.valueOf(Seconds) + "."  +
                          getThreeDigits(Milliseconds);
      }
     //---------------------------------------------------------------
     // returns a string in format HH:MM:SS.mmm giving the time since
     // the timer was created
     //---------------------------------------------------------------
     public String getTimerString(long elapsed){

          // Finish that : need to convert to a valid String
          long Hours = elapsed / (60 * 60 * 1000);
          elapsed -= Hours * 60 * 60 * 1000;
          long Minutes = elapsed / (60 * 1000);
          elapsed -= Minutes * 60 * 1000;
          long Seconds = elapsed / 1000;
          elapsed -= Seconds * 1000;
          long Milliseconds = elapsed;
          //
          //  Format milliseconds, always using three digits.
          //  The equivalent of a printf("%03d") in C
          //
          getThreeDigits(Milliseconds);
          return  String.valueOf(Hours) + ":" + String.valueOf(Minutes) +
    					":" + String.valueOf(Seconds) + "."  +
                         getThreeDigits(Milliseconds);
     }
     //---------------------------------------------------------------
     // Resets the timer
     //---------------------------------------------------------------
     public void ResetTimer()
     {
          timercreationtime = System.currentTimeMillis();
          timerlastmeasuredtime = timercreationtime;
     }
     //---------------------------------------------------------------
     // Static method to convert milliseconds to a formatted String
     //---------------------------------------------------------------
     public static String toString(long elapsed) {
          long Hours = elapsed / (60 * 60 * 1000);
          elapsed -= Hours * 60 * 60 * 1000;
          long Minutes = elapsed / (60 * 1000);
          elapsed -= Minutes * 60 * 1000;
          long Seconds = elapsed / 1000;
          elapsed -= Seconds * 1000;
          long Milliseconds = elapsed;
          //
          //  Format milliseconds, always using three digits.
          //  The equivalent of a printf("%03d") in C
          //
          return  String.valueOf(Hours) + ":" + String.valueOf(Minutes) +
    					":" + String.valueOf(Seconds) + "." +
                         getThreeDigits(Milliseconds);
     }
     //---------------------------------------------------------------
     // Format milliseconds
     //---------------------------------------------------------------
     protected static String getThreeDigits(long Milliseconds){
          if(Milliseconds > 99)
               return String.valueOf(Milliseconds);
          else if (Milliseconds > 9)
               return "0" + String.valueOf(Milliseconds);
          else
               return "00" + String.valueOf(Milliseconds);
     }
}
