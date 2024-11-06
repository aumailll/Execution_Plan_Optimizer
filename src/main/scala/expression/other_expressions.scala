/** ====================================================================================================================
 * EXPRESSION - OtherExpressions
 * ====================================================================================================================
 *
 * An expression is a column, a transformation over a column, or a condition over a column
 */

package expression
import types.DataType

/**
 * Represents a type cast of an expression to a specified data type.
 *
 * @param expression The expression to be cast, which must conform to the `Expression` trait.
 * @param dataType   The target data type to which the expression should be cast, represented by a `datatype.DataType`.
 * @return An `Expression` that represents the original expression cast to the specified data type.
 */
case class Cast(expression: Expression, dataType: DataType) extends Expression

/**
 * Represents a constant literal value in an expression.
 *
 * @param value    The constant value of any type (`Any`) that this literal represents.
 * @param dataType The data type of the literal value, represented by a `datatype.DataType`. This helps in understanding 
 *                 how the value should be treated in operations.
 * @return An `Expression` that encapsulates a constant value, making it usable in expressions and calculations.
 */
case class Literal(value: Any, dataType: DataType) extends Expression

/**
 * Represents a wildcard in a SELECT statement, denoted by `*`.
 * This is typically used to select all columns from a table.
 *
 * @return An `Expression` that signifies the selection of all columns in a query context.
 */
case object Star extends Expression