/*
 * Created on Aug 5, 2005
 */
package uk.org.ponder.rsf.view;

import uk.org.ponder.webapputil.ViewParameters;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface TemplateResolver {
  /** Locates a view template suitable for rendering the specified view,
   * possibly using other private (thread-local) information based on the
   * consumer type for the current request.
   */
  public ViewTemplate locateTemplate(ViewParameters viewparams);
}
