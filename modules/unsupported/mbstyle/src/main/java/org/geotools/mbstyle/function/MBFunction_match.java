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
package org.geotools.mbstyle.function;

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.mbstyle.parse.MBFormatException;
import org.json.simple.JSONArray;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Selects the output whose label value matches the input value, or the fallback value if no match is found.
 * The input can be any string or number expression (e.g. ["get", "building_type"]).
 * Each label can either be a single literal value or an array of values.
 * Example: ["match",
 * input: InputType (number or string),
 * label_1: InputType | [InputType, InputType, ...], output_1: OutputType,
 * label_n: InputType | [InputType, InputType, ...], output_n: OutputType,
 * ..., default: OutputType]: OutputType
 */
class MBFunction_match extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("mbMatch",
        parameter("mbMatch", Object.class),
        parameter("unused", Object.class));

    MBFunction_match() {
        super(NAME);
    }

    @Override
    public void setParameters(List<Expression> params) {
        // set the parameters
        this.params = new ArrayList<>(params);
    }

    @Override
    public Object evaluate(Object feature) {
        // get the first expression. It's the InputType to match.
        final Object inputType = params.get(0).evaluate(feature);
        // if it's not a Number or String, it's not formatted correctly
        if (inputType == null ||
            (!Number.class.isAssignableFrom(inputType.getClass()) &&
            !String.class.isAssignableFrom(inputType.getClass()))) {
            throw new MBFormatException(String.format(
                "MBDecision \"match\" requires a number or string expression for input type, found %s",
                inputType != null ? inputType.getClass().getName() : null));
        }
        int labelIndex = 1;
        int outputIndex = 2;
        while (outputIndex < params.size()) {
            // evaluate the label
            Object label = params.get(labelIndex).evaluate(feature);
            if (label != null) {
                // if the label is a Number or String, compare it to the InputType
                if (Number.class.isAssignableFrom(label.getClass()) ||
                    String.class.isAssignableFrom(label.getClass()) &&
                    MBFunctionUtil.argsEqual(label, inputType)) {
                    // found a matching label
                    return params.get(outputIndex).evaluate(feature);
                } else if (JSONArray.class.isAssignableFrom(label.getClass())) {
                    // potential label is an array. Loop through the values tosee if we have a match
                    final JSONArray jsonArray = JSONArray.class.cast(label);
                    if (jsonArray.contains(inputType)) {
                        // found a match
                        return params.get(outputIndex).evaluate(feature);
                    }
                }
            }
            // no match yet, increment indecies
            labelIndex += 2;
            outputIndex += 2;
        }
        // no label match, return default, if provided
        if (labelIndex < params.size()) {
            // we have a default
            return params.get(labelIndex).evaluate(feature);
        }
        // no label match and no default
        return null;
    }
}
