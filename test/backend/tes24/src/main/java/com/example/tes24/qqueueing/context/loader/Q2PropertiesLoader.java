package com.example.tes24.qqueueing.context.loader;

import com.example.tes24.qqueueing.exception.Q2PropertiesLoadException;
import com.example.tes24.qqueueing.properties.Q2HttpHeaderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;

public final class Q2PropertiesLoader {
    private Q2HttpHeaderProperties q2HttpHeaderProperties;

    public Q2HttpHeaderProperties getLoadedProperties() {
        if (q2HttpHeaderProperties == null) {
            try {
                q2HttpHeaderProperties = new ObjectMapper(new YAMLFactory()).readValue(new File("Q2Config.yml"), Q2HttpHeaderProperties.class);
            } catch (IOException e) {
                throw new Q2PropertiesLoadException("Loading Q2HttpHeaderProperties failed.", e);
            }
        }

        return q2HttpHeaderProperties;
    }
}
