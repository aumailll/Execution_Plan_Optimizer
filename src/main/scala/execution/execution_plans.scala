/** ====================================================================================================================
 * EXECUTION PLAN - ExecutionPlan
 * ====================================================================================================================
 *
 * An Execution Plan represents the operations that must be executed from the database. The architecture is based on
 * several nodes levels.
 */

package execution

/** Define a base trait `ExecutionPlan` to represent general Execution Plan. */
trait ExecutionPlan

/** Define a base trait `LeafNode` to represent the last node of the Execution Plan. */
trait LeafNode extends ExecutionPlan

/** Define a base trait `UnaryNode` to represent the node one layer before the last one in the Execution Plan. */
trait UnaryNode(next: ExecutionPlan) extends ExecutionPlan

/** Define a base trait `BinaryNode` to represent the node 2 layers before the last one in the Execution Plan. */
trait BinaryNode(left: ExecutionPlan, right: ExecutionPlan) extends ExecutionPlan