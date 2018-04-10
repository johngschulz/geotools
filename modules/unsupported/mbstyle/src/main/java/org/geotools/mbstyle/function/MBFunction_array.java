package org.geotools.mbstyle.function;

import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

    class MBFunction_array extends FunctionExpressionImpl {
        public static FunctionName NAME = new FunctionNameImpl("mbArray",
                parameter("mbArray", Object.class),
                parameter("unused", Object.class));

        MBFunction_array() {
            super(NAME);
        }

        /**
         * @see org.geotools.filter.FunctionExpressionImpl#setParameters(java.util.List)
         */
        @Override
        public void setParameters(List<Expression> params) {
            // set the parameters
            this.params = new ArrayList<>(params);
        }

        @Override
        public Object evaluate(Object feature) {
            // loop over the arguments and ensure at least one evaluates to a JSONObject

            for (Integer i =1; i<= this.params.size() -1; i++) {
                Object evaluation = this.params.get(i).evaluate(feature);

                if (Array.class.isAssignableFrom(evaluation.getClass())) {
                    return evaluation;
                }
            }
            // couldn't find a JSONObject value
            throw new IllegalArgumentException("Function mbArray failed with no arguments of type Array");
        }

        public Class type(String string) {
            switch (string) {
                case "string":
                    return String.class;
                case "boolean":
                    return Boolean.class;
                case "number":
                    return Number.class;
                default:
                    return null;
            }
        }
}
