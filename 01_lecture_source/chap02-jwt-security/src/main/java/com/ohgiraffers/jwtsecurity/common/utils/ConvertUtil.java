package com.ohgiraffers.jwtsecurity.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ConvertUtil {

    public static Object convertObjectToJsonObject(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser();
        String converJsonString;
        Object convertObj;

        try {
            converJsonString = mapper.writeValueAsString(obj);
            convertObj = parser.parse(converJsonString);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return convertObj;
    }
}
