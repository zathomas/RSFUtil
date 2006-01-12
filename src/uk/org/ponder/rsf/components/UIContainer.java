/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.rsf.components;

public abstract class UIContainer extends UIComponent {

  public abstract void addComponent(UIComponent toadd);
  public abstract ComponentList flattenChildren();
  
  /** A list of key/value pairs which should represent EL bindings
   * ({@link UIELBinding})
   */
  //TODO: mapping!!!
  public ParameterList parameters;
}
