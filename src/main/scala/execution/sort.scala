/** ====================================================================================================================
 * EXECUTION PLAN - Sort
 * ====================================================================================================================
 *
 * An Execution Plan represents the operations that must be executed from the database. The architecture is based on
 * several nodes levels.
 */

package execution

import expression.Expression

/** Allow two possibilities defining the order of the sorting operation */
enum SortDirection:
  case Descending, Ascending
  
/**
 * Represents the ORDER BY clause of a SELECT query. This corresponds to the SQL ORDER BY statement, which specifies the
 * order in which the results of the query should be returned, based on one or more columns or expressions.
 *
 * @param order A list of `SortOrder` objects that define the sorting criteria for the result set. Each `SortOrder`
 *              specifies a column or expression to sort by, as well as the sort direction (ascending or descending).
 * @param next  The next execution plan to which this sorting operation is applied, represented by an `ExecutionPlan`.
 *              This indicates the input from which the sorted results are derived.
 * @return An `ExecutionPlan` that represents the result of applying the specified sort order to the input.
 */
case class Sort(order: List[SortOrder], next: ExecutionPlan) extends UnaryNode(next)

/**
 * Represents the sorting order for a specific expression in a query. This class is used to define how results should be
 * sorted in a SQL-like query context, including the expression to sort by and the direction of the sort (ascending or 
 * descending).
 *
 * @param expression The expression that determines the value by which the results will be sorted. This must conform to 
 *                   the `Expression` trait, and can represent a column or other calculable expression.
 * @param direction  The direction of the sort, represented by a `SortDirection` value. This specifies whether the 
 *                   sorting should be done in ascending or descending order.
 * @return A `SortOrder` object that encapsulates the sorting criteria for use in query execution.
 */
case class SortOrder(expression: Expression, direction: SortDirection)