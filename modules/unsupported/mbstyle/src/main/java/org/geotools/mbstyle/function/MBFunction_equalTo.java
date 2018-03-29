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
import org.opengis.filter.capability.FunctionName;

/**
 * MapBox Expression function that returns {@link java.lang.Boolean#TRUE} if two Objects are equivalent,
 * {@link java.lang.Boolean#FALSE} otherwise. It is slightly different from the GeoTools "equalTo" function as it treats
 * NULLs as equal (instead of not equal), and it does not compare Object.toString() values, which would result in false
 * equivalences for things like Boolean.TRUE compared to the literal string "true".
 */
class MBFunction_equalTo extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("mbEqualTo",
            parameter("mbEqualTo", Boolean.class),
            parameter("a", Object.class),
            parameter("b", Object.class));

    MBFunction_equalTo() {
        super(NAME);
    }

    @Override
    public Object evaluate(Object feature) {
        Object arg0;
        Object arg1;

        try { // attempt to get value and perform conversion
            arg0 = (Object) getExpression(0).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function equalTo argument #0 - expected type Object");
        }

        try { // attempt to get value and perform conversion
            arg1 = (Object) getExpression(1).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function equalTo argument #1 - expected type Object");
        }

        return MBFunctionUtil.argsEqual(arg0, arg1);
    }
}
