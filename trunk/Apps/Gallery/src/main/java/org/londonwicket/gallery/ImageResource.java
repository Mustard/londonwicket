package org.londonwicket.gallery;

import java.io.File;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;

public class ImageResource extends WebResource {
  
  final boolean isThumbnail;
  
  public ImageResource(boolean isThumbnail) {
    this.isThumbnail = isThumbnail;
  }
  
  @Override
  public IResourceStream getResourceStream() {
    String album = getParameters().getString("album");
    String image = getParameters().getString("image");
    int max = getParameters().getInt("max", Integer.MAX_VALUE);
    
    GalleryService gs = WicketApplication.get().getGalleryService();

    File file;
    if (isThumbnail) {
      file = gs.getThumbnail(gs.getImage(album, image));
    }
    else {
      file = gs.getScaledImage(album, image, max);
    }
    return new FileResourceStream(file);
  }

}
