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

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.mbstyle.parse.MBFormatException;
import org.json.simple.JSONArray;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

import java.util.ArrayList;

/**
 * The value for any layout property, paint property, or filter may be specified as an expression. An expression defines
 * a formula for computing the value of the property using the operators described below:
 *   -Mathematical operators for performing arithmetic and other operations on numeric values
 *   -Logical operators for manipulating boolean values and making conditional decisions
 *   -String operators for manipulating strings
 *   -Data operators, providing access to the properties of source features
 *   -Camera operators, providing access to the parameters defining the current map view
 *
 * Expressions are represented as JSON arrays. The first element of an expression array is a string naming the expression
 * operator, e.g. "*"or "case". Subsequent elements (if any) are the arguments to the expression. Each argument is
 * either a literal value (a string, number, boolean, or null), or another expression array.
 */
public abstract class MBExpression {
    /**
     * JSON expression being used.
     */
    final protected JSONArray json;

    /**
     * name of the expression, determines how the arguments are used
     */
    final protected String name;

    final protected FilterFactory2 ff;

    public MBExpression(JSONArray json) {
        this.json = json;
        this.name = (String) json.get(0);
        this.ff = CommonFactoryFinder.getFilterFactory2();
    }

    public String getName() {
        return name;
    }

    public static MBExpression create(JSONArray json) {
        String name;
        if (json.get(0) instanceof String) {
            name = (String) json.get(0);

            /**
             * A list of color expression names
             */
            ArrayList<String> colors = new ArrayList<>();
            colors.add("rgb");
            colors.add("rgba");
            colors.add("to-rgba");

            /**
             * A list of decision expression names
             */
            ArrayList<String> decisions = new ArrayList<>();
            decisions.add("!");
            decisions.add("!=");
            decisions.add("<");
            decisions.add("<=");
            decisions.add("==");
            decisions.add(">");
            decisions.add(">=");
            decisions.add("all");
            decisions.add("any");
            decisions.add("case");
            decisions.add("coalesce");
            decisions.add("match");

            /**
             * @A list of feature data expression names
             */
            ArrayList<String> featureData = new ArrayList<>();
            featureData.add("geometry-type");
            featureData.add("id");
            featureData.add("properties");

            /**
             * A list of heatmap expression names
             */
            ArrayList<String> heatMap = new ArrayList<>();
            heatMap.add("heatmap-density");


            /**
             * A list of lookup expression names
             */
            ArrayList<String> lookUp = new ArrayList<>();
            lookUp.add("at");
            lookUp.add("get");
            lookUp.add("has");
            lookUp.add("length");


            /**
             * A list of math expression names
             */
            ArrayList<String> math = new ArrayList<>();
            math.add("-");
            math.add("*");
            math.add("/");
            math.add("%");
            math.add("^");
            math.add("+");
            math.add("acos");
            math.add("asin");
            math.add("atan");
            math.add("cos");
            math.add("e");
            math.add("ln");
            math.add("ln2");
            math.add("log10");
            math.add("log2");
            math.add("max");
            math.add("min");
            math.add("pi");
            math.add("sin");
            math.add("sqrt");
            math.add("tan");

            /**
             * A list of ramps expression names
             */
            ArrayList<String> ramps = new ArrayList<>();
            ramps.add("interpolate");
            ramps.add("step");

            /**
             * A list of string expression names
             */
            ArrayList<String> string = new ArrayList<>();
            string.add("concat");
            string.add("downcase");
            string.add("upcase");

            /**
             * A list of types expression names
             */
            ArrayList<String> types = new ArrayList<>();

            /**
             * A list of variable bindings expression names
             */
            ArrayList<String> variableBindings = new ArrayList<>();
            variableBindings.add("let");
            variableBindings.add("var");

            /**
             * A list of zoom expression names
             */
            ArrayList<String> zoom = new ArrayList<>();
            zoom.add("zoom");

            if (colors.contains(name)) {
                return new MBColor(json);
            } else if (decisions.contains(name)) {
                return new MBDecision(json);
            } else if (featureData.contains(name)) {
                return new MBFeatureData(json);
            } else if (heatMap.contains(name)) {
                return new MBHeatmap(json);
            } else if (lookUp.contains(name)) {
                return new MBLookup(json);
            } else if (math.contains(name)) {
                return new MBMath(json);
            } else if (ramps.contains(name)) {
                return new MBRampsScalesCurves(json);
            } else if (string.contains(name)) {
                return new MBString(json);
            } else if (types.contains(name)) {
                return new MBTypes(json);
            } else if (variableBindings.contains(name)) {
                return new MBVariableBinding(json);
            } else if (zoom.contains(name)) {
                return new MBZoom(json);
            } else {
                throw new MBFormatException("Expression \"" + name
                        + "\" invalid.");
            }
        }
        throw new MBFormatException("Requires a string naming the expression at position [0]");
    }

    /**
     * Determines expression to use
     */
    public abstract Expression getExpression ();

    /**
     *
     * @param json
     * @return
     */
    public static Expression transformExpression (JSONArray json) {
            return create(json).getExpression();
        }
    }

