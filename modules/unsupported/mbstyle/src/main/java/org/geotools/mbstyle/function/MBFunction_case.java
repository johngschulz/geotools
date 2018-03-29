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
 * Selects the first output whose corresponding test condition evaluates to true.
 * Example: [""case"", condition: boolean, output: OutputType,
 * ...condition: boolean, output: OutputType,
 * ...default: OutputType]: OutputType
 */
class MBFunction_case extends FunctionExpressionImpl {

    public static FunctionName NAME = new FunctionNameImpl("mbCase",
        parameter("mbCase", Object.class),
        parameter("unused", Object.class));

    MBFunction_case() {
        super(NAME);
    }

    @Override
    public void setParameters(List<Expression> params) {
        // set the parameters
        this.params = new ArrayList<>(params);
    }

    @Override
    public Object evaluate(Object feature) {
        int conditionExprssionIndex = 0;
        int outputExpressionIndex = 1;
        while (outputExpressionIndex < this.params.size()) {
            // get the condition expression and see if it evaluates to TRUE
            Object condition = params.get(conditionExprssionIndex).evaluate(feature);
            if (Boolean.TRUE.equals(condition)) {
                // condition passes, return output evaluation.
                return params.get(outputExpressionIndex).evaluate(feature);
            }
            // condition didn't evaluate to TRUE, try the next one.
            conditionExprssionIndex += 2;
            outputExpressionIndex += 2;
        }
        // if we are here, we have't found a condition that evaluated to TRUE. Return the default, if one exists.
        if (conditionExprssionIndex < params.size()) {
            return params.get(conditionExprssionIndex).evaluate(feature);
        }
        // no conditions evaluated to TRUE and no default... problem
        return null;
    }
}
