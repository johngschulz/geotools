package org.geotools.mbstyle.expression;

import org.geotools.filter.function.EnvFunction;
import org.geotools.mbstyle.MapboxTestUtils;
import org.geotools.mbstyle.parse.MBObjectParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class MBZoomTest {
    Map<String, JSONObject> testLayersById = new HashMap<>();
    MBObjectParser parse;
    FilterFactory2 ff;
    JSONObject mbstyle;

    @Before
    public void setUp() throws IOException, ParseException {
        mbstyle = MapboxTestUtils.parseTestStyle("expressionParseTestZoom.json");
        JSONArray layers = (JSONArray) mbstyle.get("layers");
        for (Object o : layers) {
            JSONObject layer = (JSONObject) o;
            testLayersById.put((String) layer.get("id"), layer);
        }
        parse = new MBObjectParser(MBExpression.class);
        ff = parse.getFilterFactory();
    }

    /**
     * Verify that a upcase string expression can be parsed correctly.
     */
    @Test
    public void testParseZoomExpression() {

        JSONObject layer = testLayersById.get("zoomExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("circle-radius").getClass());
        JSONArray arr = (JSONArray) j.get("circle-radius");
        EnvFunction.setGlobalValue("wms_scale_denominator", "2132.729584");
        assertEquals(MBZoom.class, MBExpression.create(arr).getClass());
        Expression zoom = MBExpression.transformExpression(arr);
        Object level = zoom.evaluate(zoom);
        assertEquals(ff.literal(Math.round(17.999)), ff.literal(Math.round((double)level)));
    }

    private <T> Optional<T> traverse(JSONObject map, Class<T> clazz,
                                     String... path) {
        if (path == null || path.length == 0) {
            return Optional.empty();
        }

        Object value = map;
        for (String key : path) {
            if (value instanceof JSONObject) {
                JSONObject m = (JSONObject) value;
                if (m.containsKey(key)) {
                    value = m.get(key);
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(clazz.cast(value));
    }
}
