/*
 * Created on 16-Sep-2006
 */
package uk.org.ponder.rsf.components;

/** A "two-faced" container that expresses a discontinuous "joint" between
 * two segments of template. The container appears in its containing context
 * as a BranchContainer with the details contained in the parent - however
 * the IKAT branch resolution behaviour is "as if" it in fact had the ID
 * given by its <code>jointID</code> field. This is the core container allowing
 * modularity of RSF templates and producers ("components").
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class UIJointContainer extends UIBranchContainer {
  public String jointID;
  
  public static UIJointContainer makeJoint(UIContainer parent, String ID, String jointID) {
    UIJointContainer togo = new UIJointContainer();
    togo.ID = ID;
    togo.jointID = jointID;
    parent.addComponent(togo);
    return togo;
  }
  public static UIJointContainer makeClient(UIContainer parent, String ID) {
    return makeJoint(parent, ID, null);
  }
  
}
