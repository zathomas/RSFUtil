/*
 * Created on Nov 15, 2005
 */
package uk.org.ponder.rsf.preservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.NullBeanLocator;
import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.rsf.state.FlowLockGetter;

/** The central manager of all PreservationStrategies. This is a request-scope
 * bean which is called directly by the ActionHandler in order to be notified
 * of life-cycle events that require preservation, restoration and destruction
 * of the bean state stored by the Strategies. 
 * <p>This SPM implementation is specifically aware of "flow" scope state
 * as a first-class concept. It maintains three separate
 * pools of strategies, guided by their relationship to the flow lifecycle.
 * Plain "strategies" are used for storage during the main lifetime of a flow.
 * "startStrategies" are invoked not only during a flow, but at the initial
 * launching of a flow as well, to persist any required launch state (in
 * current RSF, this is a single bean, the FlowIDHolder). Finally, at the
 * expiration of a flow, the "endStrategies" operate to save any (presumably
 * reduced) state required to correctly render an "end of flow", "confirmation"
 * style page, stored into "bandgap" state with probably short lifetime. 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class StatePreservationManager {
  private List strategies;
  private List startflowstrategies;
  private List endflowstrategies;

  public void setStrategies(List strategies) {
    this.strategies = strategies;
  }

  public void setStartFlowStrategies(List startflowstrategies) {
    this.startflowstrategies = startflowstrategies;
  }
  
  public void setEndFlowStrategies(List endflowstrategies) {
    this.endflowstrategies = endflowstrategies;
  }

  private WriteableBeanLocator wbl;
  private BeanLocator deadbl;
 
  public void setWriteableBeanLocator(WriteableBeanLocator wbl) {
    this.wbl = wbl;
  }

  public void setDeadBeanLocator(BeanLocator deadbl) {
    this.deadbl = deadbl;
  }
  
  private StatePreservationStrategy strategyAt(int i) {
    return (StatePreservationStrategy) strategies.get(i);
  }

  private StatePreservationStrategy startStrategyAt(int i) {
    return (StatePreservationStrategy) startflowstrategies.get(i);
  }
  
  private StatePreservationStrategy endStrategyAt(int i) {
    return (StatePreservationStrategy) endflowstrategies.get(i);
  }

  public void init() {
    if (strategies == null) {
      strategies = new ArrayList(0);
    }
    if (endflowstrategies == null) {
      endflowstrategies = new ArrayList(0);
    }
  }

  public void preserve(String tokenid, boolean flowstart) {
    
    for (int i = 0; i < strategies.size(); ++i) {
      strategyAt(i).preserve(flowstart? NullBeanLocator.instance : deadbl, tokenid);
    }
    for (int i = 0; i < startflowstrategies.size(); ++i) {
      startStrategyAt(i).preserve(deadbl, tokenid);
    }
  }

  /** Request all registered strategies to restore state into the current
   * request-scope container.
   * @param The flow token keying the state to be restored.
   * @param flowend <code>true</code> if the specified flow has expired.
   */
  public void restore(String tokenid, boolean flowend) {
    if (flowend) {
      for (int i = 0; i < endflowstrategies.size(); ++i) {
        endStrategyAt(i).restore(wbl, tokenid);
      }
    }
    else {
      for (int i = 0; i < strategies.size(); ++i) {
        strategyAt(i).restore(wbl, tokenid);
      }
      for (int i = 0; i < startflowstrategies.size(); ++i) {
        startStrategyAt(i).restore(wbl, tokenid);
      }
    }
  }

  /**
   * Signal that the current flow (multi-request sequence) has ended. Remove all
   * state from flow preservation strategies, and transfer to flow-end
   * strategies (if any).
   */
  public void flowEnd(String tokenid) {
    for (int i = 0; i < strategies.size(); ++i) {
      strategyAt(i).clear(tokenid);
    }
    for (int i = 0; i < startflowstrategies.size(); ++i) {
      startStrategyAt(i).clear(tokenid);
    }
    for (int i = 0; i < endflowstrategies.size(); ++i) {
      endStrategyAt(i).preserve(deadbl, tokenid);
    }
  
  }

}