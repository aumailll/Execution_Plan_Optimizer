/** ====================================================================================================================
 * EXECUTION PLAN - UnaryNodes
 * ====================================================================================================================
 *
 * An Execution Plan represents the operations that must be executed from the database. The architecture is based on
 * several nodes levels.
 */

package execution

import expression.{Expression, NamedExpression}

import scala.collection.immutable

/**
 * Represents the projection operation in a SELECT query (e.g: "SELECT col1, col2, ..., coln").
 *
 * @param expression A list of `NamedExpression` objects representing the columns or expressions
 *                   to be projected in the result set. Each entry contains a name and its associated expression.
 * @param next       The next execution plan to which this projection is applied, represented by an `ExecutionPlan`.
 *                   This indicates the input from which the projections are derived.
 * @return An `ExecutionPlan` that represents the result of projecting the specified expressions from the input.
 */
case class Project(expression: List[NamedExpression], next: ExecutionPlan) extends UnaryNode(next)


/** Represent the RANGE par in a SELECT query (eg: SELECT ... FROM ... RANGE 0, 10).
 *
 * @param start the line number in where starts to be collected
 * @param count number of line to collect
 */
case class Range(start: Int, count: Int, next: ExecutionPlan) extends UnaryNode(next)

/**
 * Represents the filtering operation in a SELECT query. This corresponds to the "WHERE" clause of a SQL statement,
 * allowing for the specification of conditions that must be met for rows to be included in the result set.
 *
 * @param expression The expression representing the filter condition. This can include logical and comparison
 *                   operations (e.g., `col1 = 'data' AND col2 < 3`). It must conform to the `Expression` trait.
 * @param next      The next execution plan to which this filter is applied, represented by an `ExecutionPlan`. This
 *                  indicates the input from which the filtered rows are derived.
 * @return An `ExecutionPlan` that represents the result of applying the filter condition to the input.
 */
case class Filter(expression: Expression, next: ExecutionPlan) extends UnaryNode(next)