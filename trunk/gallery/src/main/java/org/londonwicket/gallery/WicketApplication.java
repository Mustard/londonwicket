package org.londonwicket.gallery;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.londonwicket.js.JavaScriptBase;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
  private GalleryService galleryService;

  @Override
  public Class<?> getHomePage() {
    return HomePage.class;
  }

  @Override
  protected void init() {
    String baseDir = getServletContext().getInitParameter("basedir");
    if (baseDir == null) {
      throw new IllegalStateException(
          "'basedir' init param must be specified and is not.");
    }
    galleryService = new GalleryService(baseDir);

    mountBookmarkablePage("/admin", ThumbnailPage.class);
    mountBookmarkablePage("/upload", UploadPage.class);
    
    mount(new IndexedParamUrlCodingStrategy("/album", AlbumPage.class));
    mount(new IndexedParamUrlCodingStrategy("/edit", EditAlbumPage.class));
    getSharedResources().putClassAlias(JavaScriptBase.class, "js");
    getSharedResources().putClassAlias(Application.class, "a");
    getSharedResources().add("image", new ImageResource(false));
    getSharedResources().add("thumbnail", new ImageResource(true));
    getMarkupSettings().setDefaultAfterDisabledLink("");
    getMarkupSettings().setDefaultBeforeDisabledLink("");
  }

  public GalleryService getGalleryService() {
    return galleryService;
  }

  // Required for UploadProgressBar (see javadoc there).
  @Override
  protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
    return new UploadWebRequest(servletRequest);
  }

  public static WicketApplication get() {
    return (WicketApplication) Application.get();
  }
}
