/*
 * Created on Nov 14, 2005
 */
package uk.org.ponder.rsf.viewstate;

import java.util.Map;

import uk.org.ponder.util.Logger;

/**
 * A simple parser of view parameters, which will parse into clones of supplied
 * "exemplar" objects. The lookup will be performed on the first segment of the
 * pathinfo (up to first slash), which will be assumed to represent the viewID.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
// Really has two functions, that of VPP and also of the basic default
// view discoverer, since we have not made the effort to factor off 
// ViewParamsReceiver
public class BasicViewParametersParser implements ViewParametersParser {
  private ViewParamsCodec vpcodec;
  private ViewIDInferrer viewIDInferrer;

  private ViewParamsRegistryImpl defaultViewInfoReceiver;
  private boolean implicitNullRedirect;
  private ViewParameters defaultViewParams;
  
  public void setDefaultViewInfoReceiver(
      ViewParamsRegistryImpl defaultViewInfoReceiver) {
    this.defaultViewInfoReceiver = defaultViewInfoReceiver;
  }

  public void setViewParamsCodec(ViewParamsCodec vpcodec) {
    this.vpcodec = vpcodec;
  }

  public void setViewIDInferrer(ViewIDInferrer viewIDInferrer) {
    this.viewIDInferrer = viewIDInferrer;
  }
  
  public void setImplicitNullRedirect(boolean implicitNullRedirect) {
    this.implicitNullRedirect = implicitNullRedirect;
  }
  
  public void setDefaultViewParams(ViewParameters defaultViewParams) {
    this.defaultViewParams = defaultViewParams;
  }
  
  public ViewParameters parse(String pathinfo, Map requestmap) {
    // JSF memorial comment:
    // restoreView is the very first of the ViewHandler methods to be called for
    // each request, and it is guaranteed to be called. We take this opportunity
    // to stash away a parsed parameter object corresponding to our original
    // request.
    if (implicitNullRedirect && (pathinfo.equals("") || pathinfo.equals("/") )) {
      return (ViewParameters) defaultViewParams.get().copy();
    }
    else {
      String viewID = viewIDInferrer.inferViewID(pathinfo, requestmap);
      ViewParameters origrequest = defaultViewInfoReceiver.getViewParamsExemplar(viewID);

      vpcodec.parseViewParams(origrequest, new RawURLState(requestmap, pathinfo));

    //  this may *disagree* with value forced in by parsePathInfo due to VII
      origrequest.viewID = viewID;
      // Map requestmap = req.
      // requestmap.put(ViewParameters.CURRENT_REQUEST, origrequest);
      if (Logger.log.isDebugEnabled()) {
        Logger.log.debug("Parsed view " + origrequest.viewID
            + " from request parameters "
            + ViewParamUtil.toHTTPRequest(vpcodec, origrequest));
      }
      return origrequest;
    }
  
  }

}
