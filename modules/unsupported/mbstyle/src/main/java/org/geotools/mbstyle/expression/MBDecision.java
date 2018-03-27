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

import org.geotools.mbstyle.parse.MBFormatException;
import org.json.simple.JSONArray;
import org.opengis.filter.expression.Expression;

/**
 * The expressions in this section can be used to add conditional logic to your styles. For example, the 'case'
 * expression provides basic "if/then/else" logic, and 'match' allows you to map specific values of an input
 * expression to different output expressions.
 */
public class MBDecision extends MBExpression {

    // static String operators
    private static final String NOT = "!";
    private static final String NOT_EQUALS = "!=";
    private static final String LESS_THAN = "<";
    private static final String LESS_THAN_EQUALS = "<=";
    private static final String EQUALS = "==";
    private static final String GREATER_THAN = ">";
    private static final String GREATER_THAN_EQUALS = ">=";
    private static final String ALL = "all";
    private static final String ANY = "any";
    private static final String CASE = "case";
    private static final String COALESCE = "coalesce";
    private static final String MATCH = "match";

    public MBDecision(JSONArray json) {
        super(json);
    }

    /**
     * Logical negation. Returns true if the input is false, and false if the input is true.
     * Example: ["!", boolean]: boolean
     * @return
     */
    private Expression decisionNot() {
        // validate the arg list
        if (json.size() != 2) {
            throwUnexpectedArgumentCount(NOT, 1);
        }
        // first arg is the "!" operator
        final String notOp = parse.get(json, 0);
        if (!NOT.equals(notOp)) {
            // not a NOT(!) op
            throwUnexpectedOperationException(NOT, notOp);
        }
        // second argument better be a Boolean, or another expression that results in a Boolean
        Expression boolArg = parse.string(json, 1);
        // return the opposite of the arg
        return ff.function("not", boolArg);
    }


    /**
     * Returns true if the input values are not equal, false otherwise.
     * The inputs must be numbers, strings, or booleans, and both of the same type.
     * Examples:["!=", number, number]: boolean
     *          ["!=", string, string]: boolean
     *          ["!=", boolean, boolean]: boolean
     *          ["!=", null, null]: boolean
     * @return
     */
    private Expression decisionNotEqual() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(NOT_EQUALS, 2);
        }
        // first arg needs to be the operator
        final String notEqualsOp = parse.get(json, 0);
        if (!NOT_EQUALS.equals(notEqualsOp)) {
            // not a NOT_EQUALS(!=) op
            throwUnexpectedOperationException(NOT_EQUALS, notEqualsOp);
        }
        // get the comparables, treat nulls as literals
        Object comparable1 = parse.value(json, 1, true);
        Object comparable2 = parse.value(json, 2, true);
        // handle nulls first
        if (comparable1 == null) {
            // not equal if comp2 is not null
            return ff.literal(comparable2 != null);
        }
        // comp1 is not null
        if (comparable2 == null) {
            // not equal
            return ff.literal(true);
        }
        // filter functions seem to determine Booleans equal to their string representations,
        // so we need to ensure types are equal first
        final Class comparable1Class = comparable1.getClass();
        final Class comparabel2Class = comparable2.getClass();
        // if the class types aren't equal, by definition the values are not equal
        if (!comparable1Class.equals(comparabel2Class)) {
            // class types are not equal
            return ff.literal(true);
        }
        // 2 non-null comparables of the same class type, return the function
        return ff.function("notEqualTo", ff.literal(comparable1), ff.literal(comparable2));
    }

    /**
     * Returns true if the first input is strictly less than the second, false otherwise.
     * The inputs must be numbers or strings, and both of the same type.
     * Examples: ["<", number, number]: boolean
     *           ["<", string, string]: boolean
     * @return
     */
    private Expression decisionLessThan() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(LESS_THAN, 2);
        }
        // first arg needs to be the operator
        final String lessThanOp = parse.get(json, 0);
        if (!LESS_THAN.equals(lessThanOp)) {
            // not a LESS_THAN(<) op
            throwUnexpectedOperationException(LESS_THAN, lessThanOp);
        }
        // get the first arg
        Expression firstArgument = parse.string(json, 1);
        Expression secondArgument = parse.string(json, 2);
        // filter
        return ff.function("lessThan", firstArgument, secondArgument);
    }

    /**
     * Returns true if the first input is less than or equal to the second, false otherwise.
     * The inputs must be numbers or strings, and both of the same type.
     * Examples: ["<=", number, number]: boolean
     *           ["<=", string, string]: boolean
     * @return
     */
    private Expression decisionLessEqualThan() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(LESS_THAN_EQUALS, 2);
        }
        // first arg needs to be the operator
        final String lessThanEqualsOp = parse.get(json, 0);
        if (!LESS_THAN_EQUALS.equals(lessThanEqualsOp)) {
            // not a LESS_THAN_EQUALS(<=) op
            throwUnexpectedOperationException(LESS_THAN_EQUALS, lessThanEqualsOp);
        }
        // get the first arg
        Expression firstArgument = parse.string(json, 1);
        Expression secondArgument = parse.string(json, 2);
        // filter
        return ff.function("lessEqualThan", firstArgument, secondArgument);
    }

    /**
     * Returns true if the input values are equal, false otherwise.
     * The inputs must be numbers, strings, or booleans, and both of the same type.
     * Examples: ["==", number, number]: boolean
     *           ["==", string, string]: boolean
     *           ["==", boolean, boolean]: boolean
     *           ["==", null, null]: boolean
     * @return
     */
    private Expression decisionEqualTo() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(EQUALS, 2);
        }
        // first arg needs to be the operator
        final String equlasOp = parse.get(json, 0);
        if (!EQUALS.equals(equlasOp)) {
            // not a EQUALS(==) op
            throwUnexpectedOperationException(EQUALS, equlasOp);
        }
        // get the comparables, treat nulls as literals
        Object comparable1 = parse.value(json, 1, true);
        Object comparable2 = parse.value(json, 2, true);
        // handle nulls first
        if (comparable1 == null) {
            // not equal if comp2 is not null
            return ff.literal(comparable2 == null);
        }
        // comp1 is not null
        if (comparable2 == null) {
            // not equal
            return ff.literal(false);
        }
        // filter functions seem to determine Booleans equal to their string representations,
        // so we need to ensure types are equal first
        final Class comparable1Class = comparable1.getClass();
        final Class comparable2Class = comparable2.getClass();
        // if the class types aren't equal, by definition the values are not equal
        if (!comparable1Class.equals(comparable2Class)) {
            // class types are not equal
            return ff.literal(false);
        }
        // 2 non-null comparables of the same class type, return the function
        return ff.function("equalTo", ff.literal(comparable1), ff.literal(comparable2));
    }

    /**
     * Returns true if the first input is strictly greater than the second, false otherwise.
     * The inputs must be numbers or strings, and both of the same type.
     * Example: [">", number, number]: boolean
     *          [">", string, string]: boolean
     * @return
     */
    private Expression decisionGreaterThan() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(GREATER_THAN, 2);
        }
        // first arg needs to be the operator
        final String greaterThanOp = parse.get(json, 0);
        if (!GREATER_THAN.equals(greaterThanOp)) {
            // not a GREATER_THAN(>) op
            throwUnexpectedOperationException(GREATER_THAN, greaterThanOp);
        }
        // get the first arg
        Expression firstArgument = parse.string(json, 1);
        Expression secondArgument = parse.string(json, 2);
        // filter
        return ff.function("greaterThan", firstArgument, secondArgument);
    }

    /**
     * Returns true if the first input is greater than or equal to the second, false otherwise.
     * The inputs must be numbers or strings, and both of the same type.
     * Example: [">=", number, number]: boolean
     *          [">=", string, string]: boolean
     * @return
     */
    private Expression decisionGreaterEqualThan() {
        // validate the arg list
        if (json.size() != 3) {
            throwUnexpectedArgumentCount(GREATER_THAN_EQUALS, 2);
        }
        // first arg needs to be the operator
        final String greaterThanEqualsOp = parse.get(json, 0);
        if (!GREATER_THAN_EQUALS.equals(greaterThanEqualsOp)) {
            // not a GREATER_THAN_EQUALS(>=) op
            throwUnexpectedOperationException(GREATER_THAN_EQUALS, greaterThanEqualsOp);
        }
        // get the first arg
        Expression firstArgument = parse.string(json, 1);
        Expression secondArgument = parse.string(json, 2);
        // filter
        return ff.function("greaterEqualThan", firstArgument, secondArgument);
    }

    /**
     * Returns true if all the inputs are true, false otherwise. The inputs are evaluated in order, and evaluation is
     * short-circuiting: once an input expression evaluates to false, the result is false and no further input
     * expressions are evaluated.
     * Example: [""all"", boolean, boolean]: boolean
     *          [""all"", boolean, boolean, ...]: boolean
     * @return
     */
    private Expression decisionAll() {
        // validate the arg list
        if (json.size() < 2) {
            throwInsufficientArgumentCount(ALL, 1);
        }
        // first arg needs to be the operator
        final String allOp = parse.get(json, 0);
        if (!ALL.equals(allOp)) {
            // not a ALL(all) op
            throwUnexpectedOperationException(ALL, allOp);
        }
        // loop over each expression and evaluate. If an expression evaluates to false, we are done.
        for (int i = 1; i < json.size(); ++i) {
            Expression expression = parse.string(json, i);
            // if the expression evaluates to false, we are done
            final Boolean evaluate = expression.evaluate(expression, Boolean.class);
            if (Boolean.FALSE.equals(evaluate)) {
                return ff.literal(false);
            }
        }
        // if we have made it here, none of the arguments have evaluated to false
        return ff.literal(true);
    }

    /**
     * Returns true if any of the inputs are true, false otherwise. The inputs are evaluated in order,
     * and evaluation is short-circuiting: once an input expression evaluates to true, the result is true and no
     * further input expressions are evaluated.
     * Example: [""any"", boolean, boolean]: boolean
     *          [""any"", boolean, boolean, ...]: boolean
     * @return
     */
    private Expression decisionAny() {
        // validate the arg list
        if (json.size() < 2) {
            throwInsufficientArgumentCount(ALL, 1);
        }
        // first arg needs to be the operator
        final String anyOp = parse.get(json, 0);
        if (!ANY.equals(anyOp)) {
            // not a ANY(any) op
            throwUnexpectedOperationException(ANY, anyOp);
        }
        // loop over each expression and evaluate. If an expression evaluates to true, we are done.
        for (int i = 1; i < json.size(); ++i) {
            Expression expression = parse.string(json, i);
            // if the expression evaluates to true, we are done
            final Boolean evaluate = expression.evaluate(expression, Boolean.class);
            if (Boolean.TRUE.equals(evaluate)) {
                return ff.literal(true);
            }
        }
        // if we have made it here, none of the arguments have evaluated to true
        return ff.literal(false);
    }

    /**
     * Selects the first output whose corresponding test condition evaluates to true.
     * Example: [""case"", condition: boolean, output: OutputType,
     *              ...condition: boolean, output: OutputType,
     *              ...default: OutputType]: OutputType
     * @return
     */
    private Expression decisionCase() {
        // validate the arg list
        if (json.size() < 3) {
            throwInsufficientArgumentCount(ALL, 2);
        }
        // first arg needs to be the operator
        final String caseOp = parse.get(json, 0);
        if (!CASE.equals(caseOp)) {
            // not a CASE(case) op
            throwUnexpectedOperationException(CASE, caseOp);
        }
        // loop and evaluate conditions
        int conditionIndex = 1;
        int outputIndex = 2;
        while (outputIndex < json.size()) {
            // get the condition
            Expression conditionExp = parse.string(json, conditionIndex);
            if (Boolean.TRUE.equals(conditionExp.evaluate(conditionExp, Boolean.class))) {
                return deepEvaluate(ff.literal(parse.value(json, outputIndex)));
            }
            // still looking, increment the argument positions
            conditionIndex+=2;
            outputIndex+=2;
        }
        // if we are here, we found no condition that evaluated to true. if we have a default, it will be in the
        // conditionIndex. If not, conditionIndex will be past the end and we have an error
        if (conditionIndex < json.size()) {
            // we have a default, use it
            return deepEvaluate(ff.literal(parse.value(json, conditionIndex)));
        }
        // no default, error
        throw new MBFormatException(String.format(
            "MBDecision \"%s\" had no conditions that evaluated true and no defualt", CASE));
    }

    /**
     * Evaluates each expression in turn until the first non-null value is obtained, and returns that value.
     * Example: ["coalesce", OutputType, OutputType, ...]: OutputType
     * @return
     */
    private Expression decisionCoalesce() {
        // validate the arg list
        if (json.size() < 2) {
            throwInsufficientArgumentCount(COALESCE, 1);
        }
        // first arg needs to be the operator
        final String coalesceOp = parse.get(json, 0);
        if (!COALESCE.equals(coalesceOp)) {
            // not a COALESCE(coalesce) op
            throwUnexpectedOperationException(COALESCE, coalesceOp);
        }
        // loop through and return the first non-null evaluation
        for (int i = 1; i< json.size(); ++i) {
            Object value = parse.value(json, i, true);
            if (value != null) {
                return deepEvaluate(ff.literal(value));
            }
        }
        // no non-null evaluations
        return ff.literal(null);
    }

    /**
     * Selects the output whose label value matches the input value, or the fallback value if no match is found.
     * The input can be any string or number expression (e.g. ["get", "building_type"]).
     * Each label can either be a single literal value or an array of values.
     * Example: ["match",
     *              input: InputType (number or string),
     *              label_1: InputType | [InputType, InputType, ...], output_1: OutputType,
     *              label_n: InputType | [InputType, InputType, ...], output_n: OutputType,
     *               ..., default: OutputType]: OutputType
     * @return
     */
    private Expression decisionMatch() {
        // validate the arg list
        if (json.size() < 4) {
            throwInsufficientArgumentCount(COALESCE, 3);
        }
        // first arg needs to be the operator
        final String matchOp = parse.get(json, 0);
        if (!MATCH.equals(matchOp)) {
            // not a MATCH(match) op
            throwUnexpectedOperationException(MATCH, matchOp);
        }
        // the first arg needs to be the input expression
        Object inputValue = deepEvaluate(ff.literal(parse.value(json, 1))).evaluate(null);
        final boolean labelIsNumber = Number.class.isAssignableFrom(inputValue.getClass());
        final boolean labelIsString = String.class.isAssignableFrom(inputValue.getClass());
        // inputValue needs to be a number or String
        if (!labelIsString && !labelIsNumber) {
            // not a number or String
            throw new MBFormatException(String.format(
                "MBDecision \"%s\" requires a number or string expression for input type, found %s",
                MATCH, inputValue.getClass().getName()));
        }
        // now we need either Strings or numbers that potentially match the input, or an array of them
        int labelIndex = 2;
        int outputIndex = 3;
        while (outputIndex < json.size()) {
            // get the label
            Object potentiallyMatchingLabel = json.get(labelIndex);
            // if the label is a Number of String, compare
            if (Number.class.isAssignableFrom(potentiallyMatchingLabel.getClass()) ||
                String.class.isAssignableFrom(potentiallyMatchingLabel.getClass())) {
                // compare
                if (potentiallyMatchingLabel.equals(inputValue)) {
                    return deepEvaluate(ff.literal(parse.value(json, outputIndex)));
                }
            } else if (JSONArray.class.isAssignableFrom(potentiallyMatchingLabel.getClass())) {
                // it's an array, loop through it to see if we have a match
                JSONArray arrayOfLabels = JSONArray.class.cast(potentiallyMatchingLabel);
                if (arrayOfLabels.contains(inputValue)) {
                    return deepEvaluate(ff.literal(parse.value(json, outputIndex)));
                }
            } else {
                throw new MBFormatException(String.format(
                    "MBDecision \"%s\" supports numbers, strings and arrays as label matches. Found \"%s\"",
                    MATCH, potentiallyMatchingLabel.getClass().getName()));
            }
            labelIndex += 2;
            outputIndex += 2;
        }
        // if we are here, we found no matching label, use the default
        if (labelIndex < json.size()) {
            return deepEvaluate(ff.literal(parse.value(json, labelIndex)));
        }
        // no default, error
        throw new MBFormatException(String.format(
            "MBDecision \"%s\" had no matching labels and no defualt", CASE));
    }

    @Override
    public Expression getExpression()throws MBFormatException {
            switch (name) {
                case NOT:
                    return decisionNot();
                case NOT_EQUALS:
                    return decisionNotEqual();
                case LESS_THAN:
                    return decisionLessThan();
                case LESS_THAN_EQUALS:
                    return decisionLessEqualThan();
                case EQUALS:
                    return decisionEqualTo();
                case GREATER_THAN:
                    return decisionGreaterThan();
                case GREATER_THAN_EQUALS:
                    return decisionGreaterEqualThan();
                case ALL:
                    return decisionAll();
                case ANY:
                    return decisionAny();
                case CASE:
                    return decisionCase();
                case COALESCE:
                    return decisionCoalesce();
                case MATCH:
                    return decisionMatch();
                default:
                    throw new MBFormatException(name + " is an unsupported decision expression");
            }
    }

    /**
     * Recursively evaluates an Expression until it's fully evaluated.
     * @param exp Expression to fully evaluate.
     * @return a LiteralExpression wrapping the fully evaluated Expression value.
     */
    private Expression deepEvaluate(Expression exp) {
        Object evaluate = exp.evaluate(null);
        if (Expression.class.isAssignableFrom(evaluate.getClass())) {
            return deepEvaluate(Expression.class.cast(evaluate));
        }
        return ff.literal(evaluate);
    }

    private void throwUnexpectedOperationException(String expected, String actual) throws MBFormatException {
        throw new MBFormatException(String.format("Unexpected operation. Expected \"%s\" but parsed \"%s\"",
            expected, actual));
    }

    private void throwUnexpectedArgumentCount(String decisionOp, int argCount) throws MBFormatException {
        throw new MBFormatException(String.format("Decision \"%s\" should have exactly %d argument(s)",
            decisionOp, argCount));
    }

    private void throwInsufficientArgumentCount(String decisionOp, int argCount) throws MBFormatException {
        throw new MBFormatException(String.format("Decision \"%s\" should have at least %d argument(s)",
            decisionOp, argCount));
    }
}
