/*
 * Created on Nov 1, 2005
 */
package uk.org.ponder.rsf.processor;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.rsf.componentprocessor.ComponentProcessor;
import uk.org.ponder.rsf.components.UIBound;
import uk.org.ponder.rsf.components.UIComponent;

/** Fetches values from the request bean model that are referenced via EL
 * value bindings, if such have not already been set.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class ValueFixer implements ComponentProcessor {
  private BeanLocator beanlocator;
  private BeanModelAlterer alterer;
  public void setBeanLocator(BeanLocator beanlocator) {
    this.beanlocator = beanlocator;
  }
  public void setModelAlterer(BeanModelAlterer alterer) {
    this.alterer = alterer;
  }
  
  public void processComponent(UIComponent toprocesso) {
    if (toprocesso instanceof UIBound) {
      UIBound toprocess = (UIBound) toprocesso;
      if (toprocess.valuebinding != null && toprocess.acquireValue() == null) {
        Object value = alterer.getBeanValue(toprocess.valuebinding, beanlocator);
        // The slightly tricky (and unique, in the codebase) work of converting 
        // a bean value to its semi-reduced representation. Note that this is inverse
        // to the unflattening operations in setBeanValue itself. However, these must
        // stay INSIDE the alterer since only it has knowledge of the target object type.
        toprocess.updateValue(value);
      }
    }
  }

}