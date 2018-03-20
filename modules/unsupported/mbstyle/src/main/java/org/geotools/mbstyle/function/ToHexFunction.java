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

import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.filter.capability.FunctionName;

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

/**
 * Generate a hexadecimal value from an rgb value in the range of 0-255.
 */
public class ToHexFunction extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("tohex",
            parameter("int", Integer.class));

    public ToHexFunction() {
        super(NAME);
    }

    public Object evaluate(Object feature) {
        Integer arg0;

        try { // attempt to get value and perform conversion
            Number number = getExpression(0).evaluate(feature, Integer.class );
            arg0 = number.intValue();
        } catch (Exception e) {
            // probably a type error
            throw new IllegalArgumentException(
                    "Filter Function problem for function tohex argument #0 - expected type integer");
        }
        String hex = Integer.toHexString(arg0);
        if (hex.length() == 1) {
            hex = 0 + hex;
        }
            return hex;
    }
}