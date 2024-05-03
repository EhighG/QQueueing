package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.Q2HttpHeaderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;

public class Q2ConfigurationLoader {
    private Q2ConfigurationLoader() {}

    static {
        FILE = new File("Q2Config.yml");
        OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
        CONFIGURATION = load();
        setConfiguration();
    }

    private static final File FILE;
    private static final ObjectMapper OBJECT_MAPPER;
    private static final Q2Configuration CONFIGURATION;

    public static Q2Configuration load() {
        if (CONFIGURATION != null) return CONFIGURATION;

        try {
            return OBJECT_MAPPER.readValue(FILE, Q2Configuration.class);
        } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
    }

    private static void setConfiguration() {
        assert CONFIGURATION != null;

        Q2Context.setDefaultHeader(CONFIGURATION.getDefaultHeader());
        Q2Context.setMonitorHeader(CONFIGURATION.getMonitorHeader());
    }
}
