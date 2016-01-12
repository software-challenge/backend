package com.rra;
import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

class Logger {

  static void log(String message, boolean error)
  {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    PrintStream target = error ? System.err : System.out;
    target.printf("[%s] %s%n", dateFormat.format(date), message);
  }

  static void log(String message)
  {
    log(message, false);
  }

  static void logError(String message)
  {
    log(message, true);
  }

}
