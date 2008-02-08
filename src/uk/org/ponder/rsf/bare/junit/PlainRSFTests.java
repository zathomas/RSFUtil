/*
 * Created on 22 Jun 2007
 */
package uk.org.ponder.rsf.bare.junit;

import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.rsac.test.AbstractRSACTests;
import uk.org.ponder.rsf.bare.RequestLauncher;

public class PlainRSFTests extends AbstractRSACTests {

  public PlainRSFTests() {
    contributeConfigLocations(new String[] {
        "classpath:conf/rsf-config.xml", 
        "classpath:conf/blank-applicationContext.xml",
        "classpath:conf/test-applicationContext.xml"
    });
    
    contributeRequestConfigLocations(
        new String[] {
            "classpath:conf/rsf-requestscope-config.xml",
            "classpath:conf/blank-requestContext.xml",
            "classpath:conf/test-requestContext.xml"} 
    );
  }
  
  protected RequestLauncher requestLauncher;
  
  private RequestLauncher allocateRequestLauncher() {
    getRSACBeanLocator().startRequest();
    WriteableBeanLocator wbl = getRSACBeanLocator().getDeadBeanLocator();
    RequestLauncher togo = new RequestLauncher(applicationContext, getRSACBeanLocator(), isSingleShot());
    wbl.set("earlyRequestParser", togo);
    return togo;
  }
  
  public RequestLauncher getRequestLauncher() {
    if (isSingleShot()) {
      return requestLauncher;
    }
    else {
      return allocateRequestLauncher();
    }
  }
  

  protected void onSetUp() throws Exception {
    super.onSetUp();
    if (isSingleShot()) {
      requestLauncher = allocateRequestLauncher();
    }
  }
  
}
