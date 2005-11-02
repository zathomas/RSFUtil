/*
 * Created on Aug 8, 2005
 */
package uk.org.ponder.rsf.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.org.ponder.rsf.util.SplitID;
import uk.org.ponder.stringutil.CharWrap;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * UIContainer represents a "branch point" in the IKAT rendering process,
 * rather than simply just a level of component containment.
 * <p>UIContainer has responsibility for managing naming of child components, 
 * as well as separate and parallel responsibility for forms.
 * The key to the child map is the ID prefix - if the ID has no suffix, the
 * value is the single component with that ID at this level. If the ID
 * has a suffix, indicating a repetitive domain, the value is an ordered
 * list of components provided by the producer which will drive the 
 * rendering at this recursion level.
 * <p>It is assumed that an ID prefix is globally unique within the tree, not
 * just within its own recursion level - i.e. IKAT resolution takes place
 * over ALL components sharing a prefix throughout the template. This is "safe"
 * since "execution" will always return to the call site once the base
 * (XML) nesting level at the target is reached again.
 * <p>"Leaf" rendering classes <it>may</it> be derived from UIContainer - only
 * concrete instances of UIContainer will be considered as representatives of
 * pure branch points. By the time fixups have concluded, all non-branching
 * containers (e.g. UIForms) MUST have been removed from the component hierarchy.
 */
public class UIContainer extends UIComponent {
  /** The localID allows clients to distinguish between multiple instantiations
   * of the "same" (by rsf:id) component within the same scope. It forms part
   * of the global path constructed by getFullID() which uniquely identifies
   * the component.
   */
  public String localID = "";
  // This is a map to either the single component with a given ID prefix, or a 
  // list in the case of a repetitive domain (non-null suffix)
  private Map childmap = new HashMap();
  
  // this is created by the first call to flatChildren() which is assumed to
  // occur during the render phase. Implicit model that component tree is
  // i) constructed, ii) rendered, iii) discarded.
  // It is worth caching this since it is iterated over up to 4n times during
  // rendering, for each HTMLLump headlump that matches the requested call
  // in the 4 scopes.
  private transient UIComponent[] flatchildren;
 
  /** Return the single component with the given ID. This should be an 
   * ID without colon designating a leaf child.
   */
  public UIComponent getComponent(String id) {
    return (UIComponent) childmap.get(id);
  }
  /** Return all child components with the given prefix. This should be an ID
   * containing colon designating a child container. 
   * */
  public List getComponents(String id) {
    return (List) childmap.get(id);
  }
  
  public String debugChildren() {
    CharWrap togo = new CharWrap();
    togo.append("Child IDs: (");
    UIComponent[] children = flatChildren();
    for (int i = 0; i < children.length; ++ i) {
      if (i != 0) {
        togo.append(", ");
      }
      togo.append(children[i].ID);
    }
    togo.append(")");
    return togo.toString();
  }
  
  /** Returns a flattened array of all children of this container.
   * Note that this method will trigger the creation of a cached internal array
   * on its first use, which cannot be recreated. It is essential therefore that
   * it only be used once ALL modifications to the component tree have 
   * concluded (i.e. once rendering starts).
   */
  public UIComponent[] flatChildren() {
    if (flatchildren == null) {
      flatchildren = flattenChildren();
    }
    return flatchildren;
  }
  
  private UIComponent[] flattenChildren() {
    ArrayList children = new ArrayList();
    for (Iterator childit = childmap.values().iterator(); childit.hasNext(); ) {
      Object child = childit.next();
      if (child instanceof UIComponent) {
        children.add(child);
      }
      else if (child instanceof List) {
        children.addAll((List)child);
      }
    }
   return (UIComponent[]) children.toArray(new UIComponent[children.size()]);  
  }
   
  public void addComponent(UIComponent toadd) {
    toadd.parent = this;
//    if (childmap == null) {
//      childmap = new HashMap();
//    }
    SplitID split = toadd.ID == null? null : new SplitID(toadd.ID);
    String childkey = toadd.ID == null? NON_PEER_ID : split.prefix;
    if (toadd.ID != null && split.suffix == null) {
      childmap.put(childkey, toadd);
    }
    else {
      List children = (List) childmap.get(childkey);
      if (children == null) {
        children = new ArrayList();
        childmap.put(childkey, children);
      }
      children.add(toadd);
    }
//    UIContainer formholder = getFormHolder();
//    if (formholder != null) {  
//      formholder.componentToForm.put(toadd, currentform);
//    }
  }
}