package org.avaje.glue.properties;

/**
 * Provides helper methods to access the property values.
 */
public class Property {

  /**
   * Return the string property value that must be specified.
   * <p>
   * If the property does not exist this will throw an IllegalStateException.
   * </p>
   */
  public static String get(String key) {
    String val = getInternal(key);
    if (val == null) {
      throw new IllegalStateException("property " + key + " must be specified");
    }
    return val;
  }

  /**
   * Return the string property value given the default value.
   */
  public static String get(String key, String defaultValue) {
    return PropertiesLoader.load().getProperty(key, defaultValue);
  }

  /**
   * Return the boolean property value.
   */
  public static boolean getBool(String key, boolean defaultValue) {
    String val = getInternal(key);
    if (val == null) {
      return defaultValue;
    } else if (val.equalsIgnoreCase("true")) {
      return true;
    } else if (val.equalsIgnoreCase("false")) {
      return false;
    } else {
      throw new IllegalStateException("property value " + val + " for key " + key + " is not a boolean");
    }
  }

  /**
   * Return the integer property value.
   */
  public static int getInt(String key, int defaultValue) {
    String val = getInternal(key);
    return val != null ? Integer.parseInt(val) : defaultValue;
  }

  /**
   * Return the long property value.
   */
  public static long getLong(String key, long defaultValue) {
    String val = getInternal(key);
    return val != null ? Long.parseLong(val) : defaultValue;
  }

  private static String getInternal(String key) {
    return PropertiesLoader.load().getProperty(key);
  }
}
