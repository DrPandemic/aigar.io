package io.aigar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;


public class ConfigReader {
    private static final String ConfigFileName = "/player.json";

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    public Config readConfig() throws IOException {
        try {
            InputStream data = this.getClass().getResourceAsStream(ConfigFileName);

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(data, Config.class);
        } catch (Exception e) {
            logger.error("Could not load the config from '{}'", ConfigFileName);
            throw e;
        }
    }
}
