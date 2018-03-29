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
import org.opengis.filter.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates each expression in turn until the first non-null value is obtained, and returns that value.
 * Example: ["coalesce", OutputType, OutputType, ...]: OutputType
 */
class MBFunction_coalesce extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("mbCoalesce",
        parameter("mbCoalesce", Object.class),
        parameter("unused", Object.class));

    MBFunction_coalesce() {
        super(NAME);
    }

    @Override
    public void setParameters(List<Expression> params) {
        // set the parameters
        this.params = new ArrayList<>(params);
    }

    @Override
    public Object evaluate(Object feature) {
        for (Expression expression : params) {
            Object evaluate = expression.evaluate(feature);
            if (evaluate != null) {
                return evaluate;
            }
        }
        // no non-null expression evaluations
        return null;
    }
}