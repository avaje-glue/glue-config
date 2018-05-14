package org.avaje.glue.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Loads the configuration from known/expected locations.
 * <p>
 * Defines the loading order of resources and files.
 * </p>
 */
class Loader {

	private static final Logger log = LoggerFactory.getLogger(Loader.class);

	enum Source {
	  RESOURCE,
    FILE
  }

  private final LoadContext loadContext = new LoadContext();

  private YamlLoader yamlLoader;

	Loader() {
    String skipYaml = System.getProperty("skipYaml");
    if (!"true".equals(skipYaml)) {
      try {
        Class<?> exists = Class.forName("org.yaml.snakeyaml.Yaml");
        if (exists != null) {
          yamlLoader = new YamlLoader(loadContext);
        }
      } catch (Exception e) {
        // ignored
      }
    }
  }

  /**
   * Load the configuration with the expected ordering.
   *
   * @param args Command line arguments where if it starts with -P then is expected to be a properties file.
   */
	void load(String[] args) {

    loadMain(Source.RESOURCE);
    // external file configuration overrides the resources configuration
    loadMain(Source.FILE);

		loadViaSystemProperty();
		loadViaIndirection();

		// test configuration (if found) overrides main configuration
    // we should only find these resources when running tests
		loadTest();

		loadFromArgs(args);
	}

  private void loadFromArgs(String[] args) {
    if (args != null) {
      for (String arg : args) {
        if (arg.startsWith("-P")) {
          String relativePath = arg.substring(2);
          loadWithExtensionCheck(relativePath);
        }
      }
    }
  }

  /**
   * Load test configuration.
   */
  private void loadTest() {
    loadProperties("application-test.properties", Source.RESOURCE);
    loadYaml("application-test.yml", Source.RESOURCE);
  }

  /**
   * Load configuration defined by a <em>load.properties</em> entry in properties file.
   */
  private void loadViaIndirection() {

    String location = loadContext.indirectLocation();
    if (location != null) {
      location = PropertyEval.eval(location);
      loadWithExtensionCheck(location);
    }
  }

  /**
   * Load the main configuration for the given source.
   */
  private void loadMain(Source source) {
    loadYaml("application.yml", source);
    loadProperties("application.properties", source);
  }

  private void loadViaSystemProperty() {
	  String fileName = System.getenv("PROPS_FILE");
    if (fileName == null) {
      fileName = System.getProperty("props.file");
      if (fileName != null) {
        loadWithExtensionCheck(fileName);
      }
    }
  }

  void loadWithExtensionCheck(String fileName) {
    if (fileName.endsWith("yml")) {
      loadYaml(fileName, Source.FILE);
    } else if (fileName.endsWith("properties")) {
      loadProperties(fileName, Source.FILE);
    } else {
      throw new IllegalArgumentException("Expecting only yml or properties file but got [" + fileName + "]");
    }
  }

  /**
   * Evaluate all the configuration entries and return as properties.
   */
  Properties eval() {
		return loadContext.eval();
	}

	void loadYaml(String resourcePath, Source source) {
		if (yamlLoader != null) {
		  try {
			  yamlLoader.load(resource(resourcePath, source));
      } catch (Exception e) {
        log.warn("Failed to read yml from:" + resourcePath, e);
      }
		}
	}

	void loadProperties(String resourcePath, Source source) {
		InputStream is = resource(resourcePath, source);
		if (is != null) {
			loadProperties(is);
		}
	}

	private InputStream resource(String resourcePath, Source source) {
		return loadContext.resource(resourcePath, source);
	}

	private void loadProperties(InputStream is) {

		if (is != null) {
			try {
				Properties properties = new Properties();
				properties.load(is);
				put(properties);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load properties?", e);
			} finally {
				close(is);
			}
		}
	}

	private void put(Properties properties) {
		Enumeration<?> enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String property = properties.getProperty(key);
			loadContext.put(key, property);
		}
	}

	private void close(InputStream is) {
		try {
			is.close();
		} catch (IOException e) {
			log.warn("Error closing input stream for properties", e);
		}
	}
}
