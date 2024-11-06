/** ====================================================================================================================
 * ATTRIBUTES
 * ====================================================================================================================
 */

package types


/**
 * Trait representing a generic attribute with a name.
 *
 * @param name The name of the attribute.
 */
trait Attribute(name: String)

/**
 * Represents an unresolved attribute in a SQL query. An unresolved attribute is one that is not fully qualified with a 
 * table name or alias (eg: in the query "SELECT client", "client" is an unresolved attribute).
 *
 * @param name The name of the unresolved attribute.
 * @return An instance of `UnresolvedAttribute`, which contains the name of the attribute.
 */
case class UnresolvedAttribute(name: String) extends Attribute(name)

/**
 * Represents a resolved attribute in a SQL query. A resolved attribute is one that is fully qualified with the table 
 * name or an alias, providing clarity on which table the attribute belongs to (eg: in the queries "SELECT o.client" or 
 * "SELECT orders.client", "o.client" and "orders.client" are resolved attributes.)
 *
 * @param name  The name of the resolved attribute.
 * @param table The name of the table or alias that qualifies the attribute.
 * @return An instance of `ResolvedAttribute`, which contains the name of the attribute and its associated table.
 */
case class ResolvedAttribute(name: String, table: String) extends Attribute(name)