package org.avaje.glue.properties;

import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesLoaderTest {

  @Test
  public void load() {

    String[] args = {"-Ptest-dummy.yml","junk"};

    Properties props = PropertiesLoader.load(args);

    assertThat(props.getProperty("application.one")).isEqualTo("bar");
    assertThat(props.getProperty("dummy.yml.foo")).isEqualTo("bar");

  }
}
