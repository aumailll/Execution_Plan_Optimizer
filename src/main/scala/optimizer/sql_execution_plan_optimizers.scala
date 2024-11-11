  /** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizer
 * ====================================================================================================================
 */

package optimizer

import optimizer.rules._
import execution._
import catalog._

/**
 * Trait for Execution Plan Optimizer
 */
trait sql_execution_plan_optimizers {
  def optimize(executionPlan: ExecutionPlan, catalog: Catalog): ExecutionPlan
}

/**
 * Execution Plan Optimizer for SQL for arithmetic expression
 */
class SqlExecutionPlanArithmeticOptimizer extends sql_execution_plan_optimizers{
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    // Create an instance of the ArithmeticRule
    val simplifyArithmeticRule = new SimplifyArithmeticRule

    // Return an Optimized Execution Plan or the old one is the rule cannot be applied
    if (simplifyArithmeticRule.isApplicable(execution_plan, catalog)) {
      // Apply the rule and return a new Execution Plan
      val (optimizedPlan, _) = simplifyArithmeticRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL for '*' Projection
 */
class SqlExecutionPlanStarOptimizer extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    // Create an instance of the StarProjectRule
    val simplifyStarRule = new SimplifyStarProjectRule
    
    // Return an Optimized Execution Plan or the old one is the rule cannot be applied
    if (simplifyStarRule.isApplicable(execution_plan, catalog)) {
      val (optimizedPlan, _) = simplifyStarRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL for UnresolvedAttributes to become ResolvedAttributes
 */
class SqlExecutionPlanResolveUnresolvedOptimizer extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    // Create an instance of the ResolveUnresolvedAttributeRule
    val resolveUnresolvedAttributeRule = new ResolveUnresolvedAttributeRule

    // Return an Optimized Execution Plan or the old one if the rule cannot be applied
    if (resolveUnresolvedAttributeRule.isApplicable(execution_plan, catalog)) {
      // Apply the rule and return a new Execution Plan
      val (optimizedPlan, _) = resolveUnresolvedAttributeRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL for SubQueryAlias
 */
class SqlExecutionPlanSubQueryAliasOptimizer extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    val simplifySubQueryAliasRule = new SimplifySubQueryAliasRule

    if (simplifySubQueryAliasRule.isApplicable(execution_plan, catalog)) {
      val (optimizedPlan, _) = simplifySubQueryAliasRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL to Replace UnresolvedAttribute(alias) by the expression mapped with this alias
 */
class SqlExecutionPlanReplaceUnresolvedAttributes extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    val replaceUnresolvedAttributesRule = new ReplaceUnresolvedAttributesRule

    if (replaceUnresolvedAttributesRule.isApplicable(execution_plan, catalog)) {
      val (optimizedPlan, _) = replaceUnresolvedAttributesRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL to Prune unused columns
 */
class SqlExecutionPlanPruneUnusedColumns extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    val pruneUnusedColumnsRule = new PruneUnusedColumnsRule

    if (pruneUnusedColumnsRule.isApplicable(execution_plan, catalog)) {
      val (optimizedPlan, _) = pruneUnusedColumnsRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}

/**
 * Execution Plan Optimizer for SQL to merge operations
 */
class SqlExecutionPlanFusionOptimizer extends sql_execution_plan_optimizers {
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    val fusionRule = new FusionRule

    if (fusionRule.isApplicable(execution_plan, catalog)) {
      val (optimizedPlan, _) = fusionRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}