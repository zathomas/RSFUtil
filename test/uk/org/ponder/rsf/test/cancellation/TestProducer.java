/*
 * Created on 8 Jan 2008
 */
package uk.org.ponder.rsf.test.cancellation;

import uk.org.ponder.rsf.bare.RequestLauncher;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

public class TestProducer implements ViewComponentProducer {

  public String getViewID() {
    return RequestLauncher.TEST_VIEW;
  }

  public void fillComponents(UIContainer tofill, ViewParameters viewparams,
      ComponentChecker checker) {
    UIForm form = UIForm.make(tofill, "form");
  
    UICommand.make(form, "mycommand", "canceller.act");
  }

}
