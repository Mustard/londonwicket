package org.londonwicket.gallery;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.ValueMap;
import org.londonwicket.cropper.CroppableImage;
import org.londonwicket.gallery.GalleryService.Image;

public class ThumbnailPage extends TemplatePage {
  
  private TreeMap<Image, Rectangle> cropArea = new TreeMap<Image, Rectangle>();
  
  private static final int SIZE = 640;
  
  private boolean moreToCome = false;
  
  public ThumbnailPage() {

    List<Image> images = getGalleryService().getImagesWithoutThumbnails();

    Form form = new Form("form") {
      @Override
      protected void onSubmit() {
        for (Map.Entry<Image, Rectangle> entry : cropArea.entrySet()) {
          Image image = entry.getKey();
          Rectangle r = entry.getValue();
          getGalleryService().createThumbnail(image, r, SIZE);
        }
        if (moreToCome) {
          setResponsePage(ThumbnailPage.class);
        }
        else {
          setResponsePage(HomePage.class);
        }
      }
    };
    add(form);
    if (images.isEmpty()) {
      form.setVisible(false);
    }
    
    RepeatingView repeater = new RepeatingView("container");
    form.add(repeater);
    
    if (images.size() > 16) {
      moreToCome = true;
      images = images.subList(0, 16);
    }
    
    for (final Image img : images) {
      WebMarkupContainer container = new WebMarkupContainer(repeater.newChildId());
      repeater.add(container);
      
      String album = img.getAlbum();
      String image = img.getImage();
      ValueMap map = new ValueMap(
          String.format("album=%s,image=%s,max=%d", album, image, SIZE));

      ImageInfo info = new ImageInfo();
      try {
        // We need to calculate this against the real image as it may be smaller.
        info.setInput(new FileInputStream(getGalleryService().getScaledImage(album, image, SIZE)));
        if (info.check()) {
          int size = Math.min(info.getWidth(), info.getHeight());
          int halfSize = size / 2;
          int cx = info.getWidth() / 2;
          int cy = info.getHeight() / 2;
          Rectangle rect = new Rectangle(
              cx - halfSize, cy - halfSize,
              size, size);
          cropArea.put(img, rect);
        }
      } catch (IOException e) {
        warn("Could not read image: " + img.toString());
      }
      
      IModel cropModel = new IModel() {
        public Object getObject() {
          return cropArea.get(img);
        }
        public void setObject(Object object) {
          cropArea.put(img, (Rectangle)object);
        }
        public void detach() {}
      };
      
      CroppableImage cropper = new CroppableImage("image", cropModel, new ResourceReference("image"), map) {
      
        @Override
        public void onCropAreaUpdate(AjaxRequestTarget target) {
          // Do nothing.
        }
      
      };
      cropper.setConstrainedRatio(96, 96);
      cropper.setMinWidth(96);
      cropper.setMinHeight(96);
      cropper.add(new SimpleAttributeModifier("width" , String.valueOf(info.getWidth())));
      cropper.add(new SimpleAttributeModifier("height" , String.valueOf(info.getHeight())));
      container.add(cropper);
      
      WebMarkupContainer preview = new WebMarkupContainer("preview");
      container.add(preview);
      preview.setOutputMarkupId(true);
      cropper.setPreviewComponent(preview);
      
      container.add(new Label("title", image));
    }
  }
}
