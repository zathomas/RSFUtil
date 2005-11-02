/*
 * Created on Aug 5, 2005
 */
package uk.org.ponder.rsf.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.FactoryBean;

import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.servletutil.HttpServletRequestAware;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ViewParametersFactory implements HttpServletRequestAware,
    FactoryBean {

  private HttpServletRequest request;
  private ViewParameters vpexemplar;

  private ViewParameters viewparams;

  public void setHttpServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setViewParametersExemplar(ViewParameters vpexemplar) {
    this.vpexemplar = vpexemplar;
  }

  /**
   * Returns the UIViewRoot for the restored view identified by the provided
   * view ID, or null if no state is available.
   */
  public ViewParameters parseRequest(HttpServletRequest request) {
    Logger.log.info("begin parseRequest");
    // We INSIST that all data passed in and out should be in UTF-8.
    // TODO: Make sure we do not tread on the toes of a POST request in
    // doing this however.
    try {
      request.setCharacterEncoding("UTF-8");
    }
    catch (UnsupportedEncodingException uee) {
      throw UniversalRuntimeException.accumulate(uee,
          "Fatal internal error: UTF-8 encoding not found");
    }

    // restoreView is the very first of the ViewHandler methods to be called for
    // each request, and it is guaranteed to be called. We take this opportunity
    // to stash away a parsed parameter object corresponding to our original
    // request.
    ViewParameters origrequest = vpexemplar.copyBase();
    Map requestparams = request.getParameterMap();
    origrequest.fromRequest(requestparams, request.getPathInfo());

    // Map requestmap = req.
    // requestmap.put(ViewParameters.CURRENT_REQUEST, origrequest);
    Logger.log.info("Restoring view " + origrequest.viewID
        + " from request parameters " + origrequest.toHTTPRequest());
    return origrequest;
  }

  public Object getObject() throws Exception {
    if (viewparams == null) {
      viewparams = parseRequest(request);
    }
    return viewparams;
  }

  public Class getObjectType() {
    return ViewParameters.class;
  }

  public boolean isSingleton() {
    return true;
  }

}