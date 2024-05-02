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
        load();
    }

    private static final File FILE;
    private static final ObjectMapper OBJECT_MAPPER;

    public static void load() {
        if (Q2Context.getQ2HttpHeaderProperties() == null) {
            try {
                Q2Context.setQ2HttpHeaderProperties(OBJECT_MAPPER.readValue(FILE, Q2HttpHeaderProperties.class));
            } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        }
    }
}
