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

import org.geotools.mbstyle.parse.MBFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.filter.expression.Expression;

import java.util.List;

public class MBLookup extends MBExpression {

    public MBLookup(JSONArray json) {
        super(json);
    }

    /**
     * Retrieves an item from an array.
     * Example:
     *   ["at", number, array]: ItemType
     * @return
     */
    public Expression lookupAt(){
        // requires an instance of a "literal" array expression
        if (json.size() == 3 && json.get(2) instanceof JSONArray){
//            Expression e = ff.literal(json.get(2));
            Expression e = parse.string(json,2);
            if (e.evaluate(null, JSONArray.class) != null) {
                JSONArray arr = e.evaluate(null, JSONArray.class);
                Integer at = parse.string(json, 1).evaluate(null, Integer.class);
                Expression value = parse.string(arr, at);
                return value;
            }
        }
        throw new MBFormatException("The \"at\" expression requires an integer value at index 1, and a literal" +
                " array value at index 2");
    }

    /**
     * Retrieves a property value from the current feature's properties, or from another object if a second argument
     * is provided. Returns null if the requested property is missing.
     * Example:
     *   ["get", string]: value
     *   ["get", string, object]: value
     * @return
     */
    public Expression lookupGet() {
        if (json.size() == 2 || json.size() == 3) {
            if (json.size() == 2) {
                Expression property = parse.string(json, 1);
                String s = property.evaluate(null, String.class);
                return ff.property(s);
            }
            if (json.size() == 3) {
                Expression property = parse.string(json, 1);
                String s = (property).evaluate(null, String.class);
                Expression e = parse.string(json, 2);
                JSONObject jo = (e.evaluate(null, JSONObject.class));
                Object p = jo.get(s);
                return ff.literal(p);
            }
        }
        throw new MBFormatException("Expression \"get\" requires a maximum of 2 arguments.");
    }

    /**
     * Tests for the presence of an property value in the current feature's properties, or from another object
     * if a second argument is provided.
     * Example:
     *   ["has", string]: boolean
     *   ["has", string, object]: boolean
     * @return
     */
    public Expression lookupHas() {
        if (json.size() == 2 || json.size() == 3) {
            if (json.size() == 2) {
                Expression value = parse.string(json, 1);
                String s = value.evaluate(null, String.class);
                return ff.function("PropertyExists", ff.literal(s));
            }
            if (json.size() == 3) {
                String svalue;
                Expression value = parse.string(json, 1);
                if (value.evaluate(value) instanceof String){
                    svalue = value.evaluate(null, String.class);
                } else{
                    throw new MBFormatException("\"has\" requires an instance of type String for the first argument");
                }
                Expression e = parse.string(json, 2);
                if (e.evaluate(e) instanceof JSONObject ){
                    JSONObject jo = e.evaluate(null, JSONObject.class);
                    Boolean contained = jo.containsKey(svalue);
                    return ff.literal(contained);
                } else{
                    throw new MBFormatException("\"has\" requires an instance of a JSONObject for the second argument");
                }
            }
        }
        throw new MBFormatException("Expression \"has\" requires 1 or 2 arguments " + json.size() + " arguments found");
    }

    /**
     * Gets the length of an array or string.
     * Example:
     *   ["length", string]: number
     *   ["length", array]: number
     * @return
     */
    public Expression lookupLength() {
        Expression e = parse.string(json,1);
        if (e.evaluate(e) instanceof String) {
            return ff.function("strLength", e);
        } else if (e.evaluate(e) instanceof List) {
            return ff.function("listSize", e);
        } else {
            throw new MBFormatException("Expression \"length\" requires an argument of literal string or" +
                    " literal array");
        }
    }

    @Override
    public Expression getExpression() throws MBFormatException {
        switch (name) {
            case "at":
                return lookupAt();
            case "get":
                return lookupGet();
            case "has":
                return lookupHas();
            case "length":
                return lookupLength();
            default:
                throw new MBFormatException(name + " is an unsupported lookup expression");
        }
    }
}
