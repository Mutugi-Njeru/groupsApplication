package com.jacpower.groupsApp.utility;

import com.jacpower.groupsApp.records.ServiceResponder;
import jakarta.json.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static JsonArray convertListToJsonArray(List<JsonObject> list)
    {
        var jsonArrayBuilder = Json.createArrayBuilder();

        for (JsonObject jsonObject : list)
        {
            jsonArrayBuilder.add(jsonObject);
        }

        return jsonArrayBuilder.build();
    }
    public static Timestamp convertStringToSqlDate(String date) throws ParseException {   //with time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return Timestamp.valueOf(localDateTime);
    }
    public static boolean isMeetingDateInFuture(String meetingDate) {
        try {
            Timestamp meetDate=convertStringToSqlDate(meetingDate);//sql timestamp
            LocalDateTime meetingTime=meetDate.toLocalDateTime();
            return meetingTime.isAfter(LocalDateTime.now());
        } catch ( ParseException e) {
            return false;
        }
    }

}
