package org.londonwicket.cropper;

import java.awt.Rectangle;

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.ValueMap;
import org.londonwicket.js.JavaScriptBase;

public abstract class CroppableImage extends NonCachingImage {
  private int minWidth, minHeight;
  private int constrainedRatioWidth, constrainedRatioHeight;
  private Component previewComponent;

  /**
   * This constructor can be used if you have a img tag that has a src that
   * points to a PackageResource (which will be created and bind to the shared
   * resources) Or if you have a value attribute in your tag for which the image
   * factory can make an image.
   * 
   * @see org.apache.wicket.Component#Component(String)
   */
  public CroppableImage(final String id, final IModel cropAreaModel) {
    super(id, cropAreaModel);
    init();
  }

  /**
   * Constructs an image from an image resourcereference. That resource
   * reference will bind its resource to the current SharedResources.
   * 
   * If you are using non sticky session clustering and the resource reference
   * is pointing to a Resource that isn't guaranteed to be on every server, for
   * example a dynamic image or resources that aren't added with a IInitializer
   * at application startup. Then if only that resource is requested from
   * another server, without the rendering of the page, the image won't be there
   * and will result in a broken link.
   * 
   * @param id See Component
   * @param resourceReference The shared image resource
   */
  public CroppableImage(final String id, final IModel cropAreaModel,
      final ResourceReference resourceReference) {
    this(id, cropAreaModel, resourceReference, null);
    init();
  }

  /**
   * Constructs an image from an image resourcereference. That resource
   * reference will bind its resource to the current SharedResources.
   * 
   * If you are using non sticky session clustering and the resource reference
   * is pointing to a Resource that isn't guaranteed to be on every server, for
   * example a dynamic image or resources that aren't added with a IInitializer
   * at application startup. Then if only that resource is requested from
   * another server, without the rendering of the page, the image won't be there
   * and will result in a broken link.
   * 
   * @param id See Component
   * @param resourceReference The shared image resource
   * @param resourceParameters The resource parameters
   */
  public CroppableImage(final String id, final IModel cropAreaModel,
      final ResourceReference resourceReference, ValueMap resourceParameters) {
    super(id, cropAreaModel);
    setImageResourceReference(resourceReference, resourceParameters);
    init();
  }

  /**
   * Constructs an image directly from an image resource.
   * 
   * This one doesn't have the 'non sticky session clustering' problem that the
   * ResourceReference constructor has. But this will result in a non 'stable'
   * url and the url will have request parameters.
   * 
   * @param id See Component
   * 
   * @param imageResource The image resource
   */
  public CroppableImage(final String id, final IModel cropAreaModel, final Resource imageResource) {
    super(id, cropAreaModel);
    setImageResource(imageResource);
    init();
  }

  /**
   * Common initialisation shared by all constructors.
   */
  private void init() {
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "prototype.js"));
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "scriptaculous.js"));
    add(HeaderContributor.forJavaScript(CroppableImage.class, "cropper.js"));
    add(HeaderContributor.forCss(CroppableImage.class, "cropper.css"));

    setOutputMarkupId(true);
    add(new CroppableBehavior());
  }

  /**
   * Called when the area to be cropped is changed.
   * 
   * @param target
   */
  public abstract void onCropAreaUpdate(AjaxRequestTarget target);

  /**
   * @return minimum width of crop area in pixels
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Sets the minimum width of the crop area.
   * 
   * @param minWidth in pixels
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }

  /**
   * @return minimum height of crop area in pixels
   */
  public int getMinHeight() {
    return minHeight;
  }

  /**
   * Sets the minimum height of the crop area.
   * 
   * @param minHeight in pixels
   */
  public void setMinHeight(int minHeight) {
    this.minHeight = minHeight;
  }
  
  public void setConstrainedRatio(int width, int height) {
    this.constrainedRatioWidth = width;
    this.constrainedRatioHeight = width;
  }
  
  public void setPreviewComponent(Component previewComponent) {
    this.previewComponent = previewComponent;
  }

  private class CroppableBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    public void renderHead(IHeaderResponse response) {
      super.renderHead(response);
      // May well be null
      Rectangle onloadCoords = (Rectangle)CroppableImage.this.getModelObject();
      String javaScript = String.format("new Cropper.Img%s('%s', {"
          + "\n onEndCrop: function(coords, dimensions) {%s},"
          + "\n minWidth: %d, minHeight: %d,"
          + "%s"
          + "%s"
          + "%s"
          + "\n displayOnInit: true"
          + "\n});",
          previewComponent == null ? "" : "WithPreview",
          getComponent().getMarkupId(),
          getCallbackScript(),
          getMinWidth(),
          getMinHeight(),
          constrainedRatioHeight == 0 ? "" :
            String.format("\n ratioDim: { x: %d, y: %d }, ",
                constrainedRatioWidth, constrainedRatioHeight),
          onloadCoords == null ? "" :
            String.format("\n onloadCoords: { x1: %d, y1: %d, x2: %d, y2: %d}, ",
                onloadCoords.x,
                onloadCoords.y,
                onloadCoords.width + onloadCoords.x,
                onloadCoords.height + onloadCoords.y),
          previewComponent == null ? "" :
            String.format("\n previewWrap: %s, ", previewComponent.getMarkupId())
      );
      response.renderOnDomReadyJavascript(javaScript);
      
    }

    @Override
    public CharSequence getCallbackUrl(boolean onlyTargetActivePage) {
      String suffix = "&x=' + coords.x1 + '&y=' + coords.y1 + "
          + "'&w=' + dimensions.width + '&h=' + dimensions.height + '";
      return super.getCallbackUrl(onlyTargetActivePage) + suffix;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
      Request request = RequestCycle.get().getRequest();
      int x = Integer.parseInt(request.getParameter("x"));
      int y = Integer.parseInt(request.getParameter("y"));
      int w = Integer.parseInt(request.getParameter("w"));
      int h = Integer.parseInt(request.getParameter("h"));
      CroppableImage.this.setModelObject(new Rectangle(x, y, w, h));
      onCropAreaUpdate(target);
    }
  }
}
