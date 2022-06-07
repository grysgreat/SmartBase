package com.star.classana.gsonhelp;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Author: luxian
 * LocalData类型的Gson解析器
 */
public class LocalDataTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonPrimitive)){
            throw new JsonParseException("The date should be a string value");
        }
        return  LocalDate.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String createTime = dateTimeFormatter.format(localDate);
        return new JsonPrimitive(createTime);
    }
}