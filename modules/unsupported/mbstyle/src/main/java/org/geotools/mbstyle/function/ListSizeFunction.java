package org.geotools.mbstyle.function;
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

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.filter.capability.FunctionName;

import java.util.List;

/**
 * Returns the size of a given list
 */
public class ListSizeFunction extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("listSize",
            parameter("size", Integer.class),
            parameter("list", List.class));

    public ListSizeFunction() {
        super(NAME);
    }

    public Object evaluate(Object feature) {
        List arg0;

        try { // attempt to get value and perform conversion
            arg0 = getExpression(0).evaluate(feature, List.class);

        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function listSize argument #0 - expected type List");
        }
        return new Integer(arg0.size());
    }
}
