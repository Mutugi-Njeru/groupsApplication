package com.jacpower.groupsApp.utility;

import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.*;

import java.util.List;

public class Util {
    public Util(){}

    public static JsonObject buildResponse(ServiceResponder responder) {
        Object message=responder.message();

        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (message instanceof String) {
            builder.add("message", (String) message);
        } else if (message instanceof JsonStructure) {
            builder.add("message", (JsonStructure) message);
        } else if (message instanceof List<?>) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Object obj : (List<?>) message) {
                if (obj instanceof JsonStructure) {
                    arrayBuilder.add((JsonStructure) obj);
                } else {
                    arrayBuilder.add(obj.toString());
                }
            }
            builder.add("message", arrayBuilder.build());
        } else if (message instanceof Integer) {
            builder.add("message", (int) message);
        } else {
            builder.add("message", "Unsupported message type");
        }
        String status = responder.isSuccess() ? "success" : "error";
        return builder
                .add("statusCode", responder.status().value())
                .add("status", status)
                .build();
    }
}
