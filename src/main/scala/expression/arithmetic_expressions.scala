/** ====================================================================================================================
 * EXPRESSION - ArithmeticExpression
 * ====================================================================================================================
 *
 * An expression is a column, a transformation over a column, or a condition over a column
 */

package expression

/**
 * Represents an addition operation between two expressions.
 *
 * @param left  The left operand of the addition, which must be an `Expression`.
 * @param right The right operand of the addition, which must also be an `Expression`.
 * @return An `Expression` that represents the sum of the left and right operands.
 */
case class Add(left: Expression, right: Expression) extends Expression

/**
 * Represents a subtraction operation between two expressions.
 *
 * @param left  The left operand of the subtraction, which must be an `Expression`.
 * @param right The right operand of the subtraction, which must also be an `Expression`.
 * @return An `Expression` that represents the result of subtracting the right operand from the left.
 */
case class Subtract(left: Expression, right: Expression) extends Expression

/**
 * Represents a multiplication operation between two expressions.
 *
 * @param left  The left operand of the multiplication, which must be an `Expression`.
 * @param right The right operand of the multiplication, which must also be an `Expression`.
 * @return An `Expression` that represents the product of the left and right operands.
 */
case class Multiply(left: Expression, right: Expression) extends Expression

/**
 * Represents a division operation between two expressions.
 *
 * @param left  The numerator (left operand) of the division, which must be an `Expression`.
 * @param right The denominator (right operand) of the division, which must also be an `Expression`.
 * @return An `Expression` that represents the result of dividing the left operand by the right operand.
 */
case class Divide(left: Expression, right: Expression) extends Expression

/**
 * Represents the absolute value of an expression.
 *
 * @param expression The expression whose absolute value is to be calculated. This must conform to the `Expression` trait.
 * @return An `Expression` that represents the absolute value of the given expression.
 */
case class Abs(expression: Expression) extends Expression
