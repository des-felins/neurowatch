package dev.cyberjar.neurowatch.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.geo.Point;

import java.io.IOException;

public class PointFromXYDeserializer extends JsonDeserializer<Point> {

    @Override
    public Point deserialize(JsonParser p, DeserializationContext context) throws IOException {

        JsonNode node = p.getCodec().readTree(p);
        double x = node.get("x").asDouble();
        double y = node.get("y").asDouble();
        return new Point(x, y);
    }
}
