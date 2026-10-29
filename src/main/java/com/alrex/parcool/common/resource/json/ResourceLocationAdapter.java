package com.alrex.parcool.common.resource.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

public class ResourceLocationAdapter implements JsonDeserializer<Identifier> {
    @Override
    public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Identifier.tryParse(jsonElement.getAsJsonPrimitive().getAsString());
    }
}
