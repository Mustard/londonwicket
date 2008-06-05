package org.londonwicket.gallery;

import java.io.File;
import java.util.Collections;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.ValueMap;
import org.londonwicket.js.JavaScriptBase;

public class AlbumPage extends TemplatePage {
  
  private static final int SIZE = 720;
  
  public AlbumPage(PageParameters params) {
    
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "prototype.js"));
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "scriptaculous.js"));
    add(HeaderContributor.forJavaScript(AlbumPage.class, "lightwindow.js"));
    add(HeaderContributor.forCss(AlbumPage.class, "lightwindow.css"));
    
    String album = params.getString("0");
    add(new Label("albumTitle", album));
    
    RepeatingView repeater = new RepeatingView("imageLink");
    add(repeater);
    
    for (final File image : getGalleryService().getImages(album)) {
      ValueMap map = new ValueMap(
          String.format("album=%s,image=%s,max=%s", album, image.getName(), SIZE));
      ResourceReference ref = new ResourceReference("image");
      ResourceLink link = new ResourceLink(repeater.newChildId(), ref, map);
      repeater.add(link);
      link.add(new SimpleAttributeModifier("class", "thumb lightwindow page-options"));
      link.add(new SimpleAttributeModifier("rel", "[]"));
      //link.add(new SimpleAttributeModifier("params", "lightwindow_loading_animation=false"));
      String name = image.getName();
      int lastDot = name.indexOf('.');
      if (lastDot > 0) {
        name = name.substring(0, lastDot);
      }
      link.add(new SimpleAttributeModifier("title", name));
      map = new ValueMap(
          String.format("album=%s,image=%s,max=%s", album, image.getName(), SIZE));
      String url = urlFor(ref) + String.format("?album=%s&image=%s", album, image.getName());
      url = url.replaceAll("'", "&amp;#39;");
      link.add(new SimpleAttributeModifier("author", "&lt;a target='_new' href=\'" + url + "\'&gt;full resolution &raquo;&lt;/a&gt;"));
      
      map = new ValueMap(
          String.format("album=%s,image=%s", album, image.getName()));
      link.add(new Image("thumbnail", new ResourceReference("thumbnail"), map));
    }
    
    add(new BookmarkablePageLink("upload", UploadPage.class, new PageParameters(Collections.singletonMap("album", album))));
    add(new BookmarkablePageLink("edit", EditAlbumPage.class, new PageParameters(Collections.singletonMap("0", album))));
  }
}
