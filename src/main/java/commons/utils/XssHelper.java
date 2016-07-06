package commons.utils;

import java.util.*;

public class XssHelper {
  public static String escape(String unsafe) {
    if (unsafe == null) return null;

    boolean f = false;
    char safe[] = new char[unsafe.length()];
    for (int i = 0; i < safe.length; ++i) {
      char c = unsafe.charAt(i);
      switch (c) {
      case '<':  c = '＜'; f = true; break;
      case '>':  c = '＞'; f = true; break;
      case '"':  c = '＂'; f = true; break;
      case '\'': c = '＇'; f = true; break;
      case '(' : c = '（'; f = true; break;
      case ')':  c = '）'; f = true; break;
      }
      safe[i] = c;
    }

    return f ? new String(safe) : unsafe;
  }

}
