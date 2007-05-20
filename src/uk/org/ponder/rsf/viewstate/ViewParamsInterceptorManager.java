/*
 * Created on 18 May 2007
 */
package uk.org.ponder.rsf.viewstate;

import java.util.List;

public class ViewParamsInterceptorManager {
  private List interceptors;
  private ViewParameters inferred;

  // This is a list of ViewParamsInterceptor
  public void setInterceptors(List interceptors) {
    this.interceptors = interceptors;
  }

  public void setViewParameters(ViewParameters inferred) {
    this.inferred = inferred;
  }

  public ViewParameters getViewParameters() {
    ViewParameters adjust = inferred.get();
    if (interceptors != null) {
      for (int i = 0; i < interceptors.size(); ++i) {
        ViewParamsInterceptor interceptor = (ViewParamsInterceptor) interceptors
            .get(i);
        adjust = interceptor.adjustViewParameters(adjust);
      }
    }
    return adjust;
  }
}
