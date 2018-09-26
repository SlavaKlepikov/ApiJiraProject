package utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.Pojo;

public class JiraPojoHelper {
    public static String extractPOJO(Pojo pojo) {
    ObjectMapper mapper = new ObjectMapper();
    String result = null;
    try {
        result = mapper.writeValueAsString(pojo);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
    return result;
    }
}
