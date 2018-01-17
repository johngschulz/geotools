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

import org.geotools.mbstyle.parse.MBObjectParser;
import org.json.simple.JSONArray;
import org.opengis.filter.expression.Expression;

/**
 * The expressions in this section are provided for the purpose of testing for and converting between different
 * data types like strings, numbers, and boolean values.
 *
 * Often, such tests and conversions are unnecessary, but they may be necessary in some expressions where the type of a
 * certain sub-expression is ambiguous. They can also be useful in cases where your feature data has inconsistent types;
 * for example, you could use to-number to make sure that values like ""1.5"" (instead of 1.5) are treated as numeric
 */
public class MBTypes extends MBExpression {
    public MBTypes(JSONArray json) {
        super(json);
    }
    @Override
    public Expression getExpression() {
        return null;
    }
}
