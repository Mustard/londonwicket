package org.londonwicket.gallery;

import java.io.File;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.value.ValueMap;

public class HomePage extends TemplatePage {

  public HomePage() {
    
    GalleryService gallery = getGalleryService();
    
    RepeatingView repeater = new RepeatingView("link");
    add(repeater);
    
    for (String album : gallery.getAlbums()) {
      Link link = new BookmarkablePageLink(repeater.newChildId(), AlbumPage.class, new PageParameters("0=" + album));
      repeater.add(link);

      List<File> images = gallery.getImages(album);
      if (images.size() == 0) {
        link.setVisible(false);
      } else {
        String image = images.get(0).getName();
        ValueMap map = new ValueMap();
        map.add("album", album);
        map.add("image", image);
        link.add(new Image("thumbnail", new ResourceReference("thumbnail"), map));
        link.add(new Label("title", album));
      }
    }
    
  }
}
