package com.qqueueing.main.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * JSON 정수만 Integer로 읽는다.
 * Jackson 기본 동작은 1.5를 1로, "80"을 80으로 바꿔 받는다. 이 역직렬화기는 둘 다 거부하고, int 범위를 넘는 정수도 거부한다.
 * 거부하면 스프링이 HttpMessageNotReadableException을 던진다. JSON null은 이 역직렬화기를 거치지 않고 null이 된다.
 */
public class StrictIntegerDeserializer extends StdDeserializer<Integer> {

    public StrictIntegerDeserializer() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return (Integer) ctxt.handleUnexpectedToken(Integer.class, p);
        }
        // int 범위를 넘으면 InputCoercionException을 던진다
        return p.getIntValue();
    }
}
