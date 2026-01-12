package dev.cyberjar.neurowatch.utils;

import org.springframework.data.geo.Point;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

public class PointFromXYDeserializer extends StdDeserializer<Point> {

    public PointFromXYDeserializer() {
        super(Point.class);
    }

    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode node = (JsonNode) p.readValueAsTree(); // Jackson 3: no getCodec()

        double x = node.path("x").asDouble();
        double y = node.path("y").asDouble();
        return new Point(x, y);
    }
}
