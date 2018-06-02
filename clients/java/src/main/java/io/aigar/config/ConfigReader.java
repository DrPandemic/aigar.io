package io.aigar.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader {
    private static final String ConfigFileName = "/player.json";

    private static final Logger logger = Logger.getLogger(ConfigReader.class.getName());

    public Config readConfig() throws IOException {
        try {
            InputStream data = this.getClass().getResourceAsStream(ConfigFileName);

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(data, Config.class);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load the config from '{}'", ConfigFileName);
            throw e;
        }
    }
}
