package org.avaje.glue.properties;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyTest {

  @Test
  public void get_when_exists() {
    assertThat(Property.get("nums.one")).isEqualTo("1");
  }

  @Test(expected = IllegalStateException.class)
  public void get_when_notExists() {
    assertThat(Property.get("junk")).isEqualTo("1");
  }

  @Test
  public void getWithDefault() {
    assertThat(Property.get("nums.one", "notThis")).isEqualTo("1");
  }

  @Test
  public void bool() {
    assertThat(Property.getBool("flag.active", false)).isEqualTo(true);
    assertThat(Property.getBool("flag.notThere", false)).isEqualTo(false);
    assertThat(Property.getBool("flag.notThere", true)).isEqualTo(true);
  }

  @Test
  public void getInt() {
    assertThat(Property.getInt("nums.one", 42)).isEqualTo(1);
    assertThat(Property.getInt("nums.two", 42)).isEqualTo(2);
    assertThat(Property.getInt("nums.notThere", 42)).isEqualTo(42);
  }


  @Test(expected = NumberFormatException.class)
  public void getInt_when_invalidNumber() {
    assertThat(Property.getInt("flag.active", 42)).isEqualTo(1);
  }

  @Test(expected = NumberFormatException.class)
  public void getLong_when_invalidNumber() {
    assertThat(Property.getLong("flag.active", 42)).isEqualTo(1);
  }

  @Test(expected = IllegalStateException.class)
  public void getBool_when_invalidNumber() {
    assertThat(Property.getBool("nums.one", true)).isEqualTo(false);
  }

  @Test
  public void getLong() {
    assertThat(Property.getLong("nums.one", 42)).isEqualTo(1);
    assertThat(Property.getLong("nums.two", 42)).isEqualTo(2);
    assertThat(Property.getLong("nums.notThere", 42)).isEqualTo(42);
  }
}
