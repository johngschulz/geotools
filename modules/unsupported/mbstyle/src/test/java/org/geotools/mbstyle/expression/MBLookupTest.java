/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2018, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotools.mbstyle.expression;

import org.geotools.mbstyle.MBStyle;
import org.geotools.mbstyle.MapboxTestUtils;
import org.geotools.mbstyle.layer.SymbolMBLayer;
import org.geotools.mbstyle.parse.MBObjectParser;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.SLDTransformer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MBLookupTest {
    Map<String, JSONObject> testLayersById = new HashMap<>();
    MBObjectParser parse;
    FilterFactory2 ff;
    JSONObject mbstyle;

    @Before
    public void setUp() throws IOException, ParseException {
        mbstyle = MapboxTestUtils.parseTestStyle("expressionParseLookupTest.json");
        JSONArray layers = (JSONArray) mbstyle.get("layers");
        for (Object o : layers) {
            JSONObject layer = (JSONObject) o;
            testLayersById.put((String) layer.get("id"), layer);
        }
        parse = new MBObjectParser(MBExpression.class);
        ff = parse.getFilterFactory();
    }
    /**
     * Verify that a "get" lookup expression can be parsed correctly.
     */
    @Test
    public void testParseGetExpression() {

        JSONObject layer = testLayersById.get("getExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("text-color").getClass());
        JSONArray arr = (JSONArray) j.get("text-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression get = MBExpression.transformExpression(arr);
        Object got = get.evaluate(get);
        assertEquals(ff.literal("#006fde"), ff.literal(got));
        //
        Optional<JSONObject> g = traverse(layer, JSONObject.class, "layout");
        JSONObject jg = g.get();
        assertEquals(JSONArray.class, jg.get("text-field").getClass());
        JSONArray garr = (JSONArray) jg.get("text-field");
        assertEquals(MBLookup.class, MBExpression.create(garr).getClass());
    }
    /** Testing nested "get" lookup functions */
    @Test
    public void testParseNestedGetExpression() {

        JSONObject layer = testLayersById.get("getExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("halo-color").getClass());
        JSONArray arr = (JSONArray) j.get("halo-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression ex = MBExpression.transformExpression(arr);
        Object item = ex.evaluate(ex);
        assertEquals("RED", item);
    }
    /**
     * Verify that a "get" lookup expression transforms correctly to SLD.
     */
    @Test
    public void testGetTransform() {
        MBStyle getTest = MBStyle.create(mbstyle);
        SymbolMBLayer rgbLayer = (SymbolMBLayer) getTest.layer("getExpression");
        List<FeatureTypeStyle> getFeatures = rgbLayer.transformInternal(getTest);
        try {
            String xml = new SLDTransformer().transform(getFeatures.get(0));
            assertTrue(xml.contains("<ogc:PropertyName>name</ogc:PropertyName>"));
        } catch(Exception e) { }
    }
    /**
     * Verify that an "at" lookup expression can be parsed correctly.
     */
    @Test
    public void testParseAtExpression() {

        JSONObject layer = testLayersById.get("atExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("text-color").getClass());
        JSONArray arr = (JSONArray) j.get("text-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        // requires literal array which hasn't been implemented yet
        // hack is in place in the mbobject parser
        Expression ex = MBExpression.transformExpression(arr);
        Object item = ex.evaluate(ex);
        assertEquals("#006fde", item);
    }
    /** Testing nested "at" lookup functions */
    @Test
    public void testParseNestedAtExpression() {

        JSONObject layer = testLayersById.get("atExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("halo-color").getClass());
        JSONArray arr = (JSONArray) j.get("halo-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        // requires literal array which hasn't been implemented yet
        // hack is in place in the mbobject parser
        Expression ex = MBExpression.transformExpression(arr);
        Object item = ex.evaluate(ex);
        assertEquals("length", item);
    }
    /**
     * Verify that a "length" lookup expression can be parsed correctly.
     */
    @Test
    public void testParseLengthExpression() {

        JSONObject layer = testLayersById.get("lengthExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("text-color").getClass());
        JSONArray arr = (JSONArray) j.get("text-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression sl = MBExpression.transformExpression(arr);
        Object length = sl.evaluate(sl);
        assertEquals(9, length);

        Optional<JSONObject> oa = traverse(layer, JSONObject.class, "layout");
        JSONObject ja = oa.get();
        assertEquals(JSONArray.class, ja.get("text-field").getClass());
        JSONArray arra = (JSONArray) ja.get("text-field");
        Expression al = MBExpression.transformExpression(arra);
        Object alength = sl.evaluate(al);
        assertEquals(9, alength);
    }
    /** Testing nested "length" lookup functions */
    @Test
    public void testParseNestedLengthExpression() {

        JSONObject layer = testLayersById.get("lengthExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("halo-color").getClass());
        JSONArray arr = (JSONArray) j.get("halo-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression ex = MBExpression.transformExpression(arr);
        Object item = ex.evaluate(ex);
        assertEquals(9, item);
    }
    /**
     * Verify that a "has" Lookup expression can be parsed correctly.
     */
    @Test
    public void testParseHasExpression() {

        JSONObject layer = testLayersById.get("hasExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("text-color").getClass());
        JSONArray arr = (JSONArray) j.get("text-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression has = MBExpression.transformExpression(arr);
        Object value = has.evaluate(has);
        assertEquals(true, value);

        Optional<JSONObject> g = traverse(layer, JSONObject.class, "layout");
        JSONObject hg = g.get();
        assertEquals(JSONArray.class, hg.get("text-field").getClass());
        JSONArray garr = (JSONArray) hg.get("text-field");
        assertEquals(MBLookup.class, MBExpression.create(garr).getClass());
    }
    /** Testing nested "has" lookup functions */
    @Test
    public void testParseNestedHasExpression() {

        JSONObject layer = testLayersById.get("hasExpression");
        Optional<JSONObject> o = traverse(layer, JSONObject.class, "paint");
        JSONObject j = o.get();
        assertEquals(JSONArray.class, j.get("halo-color").getClass());
        JSONArray arr = (JSONArray) j.get("halo-color");
        assertEquals(MBLookup.class, MBExpression.create(arr).getClass());
        Expression ex = MBExpression.transformExpression(arr);
        Object item = ex.evaluate(ex);
        assertEquals(true, item);
    }
    /**
     * Verify that a "has" lookup expression transforms correctly to SLD.
     */
    @Test
    public void testHasTransform() {
        MBStyle getTest = MBStyle.create(mbstyle);
        SymbolMBLayer rgbLayer = (SymbolMBLayer) getTest.layer("hasExpression");
        List<FeatureTypeStyle> getFeatures = rgbLayer.transformInternal(getTest);
        try {
            String xml = new SLDTransformer().transform(getFeatures.get(0));
            assertTrue(xml.contains("<ogc:Function name=\"PropertyExists\"><ogc:Literal>name</ogc:Literal></ogc:Function>"));
        } catch(Exception e) { }
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
