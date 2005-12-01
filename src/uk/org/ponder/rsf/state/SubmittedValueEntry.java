/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.rsf.state;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * Represents a submitted value as received from a user submission. For 
 * full coverage, we must know 
 * <br>- the (full) ID of the component producing the submission
 * <br>- the value binding for the submitted value
 * <br>- the initial value presented to the UI when the view was rendered.
 * These values, once accumulated, are stored inside the 
 * {@see uk.org.ponder.rsf.state.RequestSubmittedValueCache}
 * In addition to encoding a component submission, an SVE may either 
 * represent a deletion binding or a pure EL binding.
 */
public class SubmittedValueEntry {
 
  /** The key used in the parameters of a command link (and hence in the 
   * servlet request) whose value is the method binding EL to be invoked on
   * this request.
   */
  public static final String FAST_TRACK_ACTION = "Fast track action";
  
  /** The key used to find the ID of the <it>submitting control</it> used
   * to produce this submission, which is that of the corresponding UIForm 
   * component. For "WAP-style" forms this may well be the same as the
   * physical submitting control.
   */
  public static final String SUBMITTING_CONTROL = "Submitting control";
  /** The EL path (without #{}) that is the l-value target of this binding.
   */
  public String valuebinding;
  /** The full ID of the component giving rise to this submission. 
   * This will be null for a non-component binding. */
  public String componentid;
  /** The value held by the component at the time the view holding it was
   * rendered. This was the initial value seen by the user at render time.
   * Either an object of type String or String[]. Holds an additional value
   * (binding or EL) in the case of a non-component binding.
   */
  public Object oldvalue;
  /** The value received back to the system via the (current) submission.
  * Either a String or a String[]. However, if it is a String, there are some
  * interesting possibilities - i) String or SaxLeafType, ii) EL reference,
  * iii) XML-encoded object tree. Type ii) is determined by setting the 
  * <code>isEL</code> flag, the other two cases resolved once the type of 
  * the target bean is seen (via ConvertUtil).
  * This value is null for non-component bindings.
  */
  public Object newvalue;
  /** Holds <code>true</code> in the case the rvalue (encoded in oldvalue)
   * of this non-component binding represents an EL binding, rather
   * than a serialised object */
  public boolean isEL;
  /** Holds <code>true</code> if this submitted value is a deletion binding,
   * in which case newvalue will be <code>null</code>
   */
  public boolean isdeletion = false;
  
}
