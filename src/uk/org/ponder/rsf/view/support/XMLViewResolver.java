/*
 * Created on Nov 25, 2005
 */
package uk.org.ponder.rsf.view.support;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import uk.org.ponder.conversion.SerializationProvider;
import uk.org.ponder.rsf.expander.TemplateExpander;
import uk.org.ponder.rsf.view.ViewResolver;
import uk.org.ponder.rsf.view.ViewRoot;
import uk.org.ponder.rsf.viewstate.ViewParamsReceiver;
import uk.org.ponder.springutil.CachingInputStreamSource;
import uk.org.ponder.stringutil.FilenameUtil;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A view resolver which will return a component producer specified in a static
 * XML file. Will attempt to look up the supplied viewID, after prefixing and
 * postfixing, as a Spring resource. This is an application scope bean.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class XMLViewResolver implements ViewResolver, ApplicationContextAware {
  public static final int NO_CACHE = -1;
  public static final String DEFAULT_EXTENSION = ".xml";
  public static final String BASEPATH_DEFAULT = "";

  private StringList viewnames;
  private String basepath = BASEPATH_DEFAULT;
  private ResourceLoader resourceloader;
  private Map views = new HashMap();
  private String extension = DEFAULT_EXTENSION;
  private List defaultviews = null;
  private TemplateExpander templateexpander;
  private SerializationProvider xmlprovider;
  private int cachesecs = NO_CACHE;
  private ViewParamsReceiver vpreceiver;
  private CachingInputStreamSource cachingiss;

  /**
   * Sets the default extension (including period) that will be suffixed to a
   * viewId in order to generate the resource path which will be loaded.
   */
  public void setDefaultExtension(String extension) {
    this.extension = extension;
  }

  /**
   * Sets a collection of component producers which will execute for every view.
   */
  public void setDefaultViews(List defaults) {
    this.defaultviews = defaults;
  }

  /**
   * Set the base directory in which component files will be sought. This must
   * contain both trailing slash and leading slash. This may designate any
   * Spring-resolvable resource path (will be handed to application context).
   */

  public void setBasePath(String basepath) {
    this.basepath = basepath;
  }

  /**
   * Sets a list of view names which this resolver will attempt to resolve. If
   * this list is not set, resolver will attempt to resolve any view. If the
   * list is set and a request is received for a view not listed in it,
   * getProducers() will return null rather than throwing an exception, enabling
   * chaining of resolvers.
   */
  public void setViewNames(StringList viewnames) {
    this.viewnames = viewnames;
  }

  public void setXMLProvider(SerializationProvider xmlprovider) {
    this.xmlprovider = xmlprovider;
  }

  public void setTemplateExpander(TemplateExpander templateexpander) {
    this.templateexpander = templateexpander;
  }

  public void setResourceLoader(ResourceLoader resourceloader) {
    this.resourceloader = resourceloader;
  }

  public void setCacheSeconds(int cachesecs) {
    this.cachesecs = cachesecs;
  }

  public void setViewParamsReceiver(ViewParamsReceiver vpreceiver) {
    this.vpreceiver = vpreceiver;
  }

  private String getFullPath(String viewId) {
    String fullpath = basepath + viewId + extension;
    return fullpath;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    if (resourceloader == null) {
      this.resourceloader = applicationContext;
    }
    cachingiss = new CachingInputStreamSource(resourceloader, cachesecs);
    if (basepath != BASEPATH_DEFAULT) {
      // make an initial attempt to load ALL producers, both for validation and
      // also to discover viewparameter types.
      String allpath = basepath + "*" + extension;
      Resource[] resources = null;
      try {
        resources = applicationContext.getResources(allpath);
        if (resources == null)
          resources = new Resource[0];
      }
      catch (Exception e) {
        throw UniversalRuntimeException.accumulate(e,
            "Error getting resource list for pattern " + allpath);
      }
      for (int i = 0; i < resources.length; ++i) {
        String filename = resources[i].getFilename();
        String viewid = FilenameUtil.getStem(filename);
        String fullpath = getFullPath(viewid);
        tryLoadProducer(fullpath, viewid);
      }
    }
  }

  private XMLViewComponentProducer tryLoadProducer(String fullpath,
      String viewId) {
    XMLViewComponentProducer togo = null;
    if (Logger.log.isInfoEnabled()) {
      Logger.log.info("Loading view template from " + fullpath);
    }
    InputStream is = cachingiss.openStream(fullpath);
    if (is != null && is != CachingInputStreamSource.UP_TO_DATE) {
      try {
        ViewRoot viewroot = (ViewRoot) xmlprovider.readObject(ViewRoot.class, is);
        vpreceiver.setViewParamsExemplar(viewId, viewroot.viewParameters);
        if (viewroot.defaultview) {
          vpreceiver.setDefaultView(viewId);
        }
        togo = new XMLViewComponentProducer();
        togo.setTemplateExpander(templateexpander);
        togo.setViewID(viewId);
        togo.setTemplateContainer(viewroot);
        views.put(viewId, togo);
      }
      catch (Exception e) {
        throw UniversalRuntimeException.accumulate(e,
            "Error loading view components from view with path " + fullpath);
      }
    }
    else {
      return (XMLViewComponentProducer) views.get(viewId);
    }
    return togo;
  }

  public List getProducers(String viewId) {
    XMLViewComponentProducer producer = (XMLViewComponentProducer) views
        .get(viewId);
    if (producer == null || cachesecs >= 0) {
      if (viewnames == null || viewnames.contains(viewId)) {
        String fullpath = basepath + viewId + extension;
        producer = tryLoadProducer(fullpath, viewId);
      }
    }
    if (producer != null) {
      ArrayList togo = new ArrayList();
      if (defaultviews != null) {
        togo.addAll(defaultviews);
      }
      togo.add(producer);
      return togo;
    }
    return null;
  }

  
}
