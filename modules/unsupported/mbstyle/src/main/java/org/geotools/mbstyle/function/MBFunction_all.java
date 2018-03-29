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
 * MapBox Expression function that returns {@link java.lang.Boolean#TRUE} if and only if all expressions evaluate to
 * {@link java.lang.Boolean#TRUE}, {@link java.lang.Boolean#FALSE} otherwise. This function is implemented as a short-
 * circuit in that it will return {@link java.lang.Boolean#FALSE} for the first expression that does not evaluate to
 * {@link java.lang.Boolean#TRUE}.
 */
class MBFunction_all extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("mbAll",
        parameter("mbAll", Boolean.class),
        parameter("unused", Object.class));

    MBFunction_all() {
        super(NAME);
    }

    @Override
    public void setParameters(List<Expression> params) {
        // set the parameters
        this.params = new ArrayList<>(params);
    }

    @Override
    public Object evaluate(Object feature) {
        // loop over the arguments and ensure each evaluates to true
        for (Expression expression : this.params) {
            Object evaluation = expression.evaluate(feature);
            if (!Boolean.TRUE.equals(evaluation)) {
                return Boolean.FALSE;
            }
        }
        // Couldn't find a FALSE
        return Boolean.TRUE;
    }
}
