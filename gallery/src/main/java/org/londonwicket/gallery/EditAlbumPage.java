package org.londonwicket.gallery;

import java.io.File;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.validator.PatternValidator;
import org.londonwicket.listeditor.ListEditor;

public class EditAlbumPage extends TemplatePage {
  
  private String album;
  private String oldName;
  private ListEditor<File> listEditor;

  public EditAlbumPage(PageParameters params) {
    
    album = params.getString("0");
    if (!getGalleryService().getAlbum(album).exists()) {
      Session.get().error("Album " + album + " does not exist.");
      throw new RestartResponseException(HomePage.class);
    }
    add(new AjaxEditableLabel("albumTitle", new PropertyModel(this, "album")) {
      @Override
      protected void onEdit(AjaxRequestTarget target) {
        super.onEdit(target);
        oldName = album;
      }
      @Override
      protected void onError(AjaxRequestTarget target) {
        target.addComponent(getFeedbackPanel());
        super.onError(target);
      }
      @Override
      protected void onSubmit(AjaxRequestTarget target) {
        super.onSubmit(target);
        GalleryService.RenameStatus status = getGalleryService().renameAlbum(oldName, album);
        switch (status) {
        case DESTINATION_EXISTS:
          error("Can't rename - new album name already exists.");
        case FAILURE:
          error("Error renaming album. Check to make sure the name is a valid filename.");
        case OK:
          throw new RestartResponseException(EditAlbumPage.class, new PageParameters("0=" + album));
        }
      }
    }.add(new PatternValidator("[^,=:/\\\\\\*\\|]+")));
    
    IModel model = new AbstractReadOnlyModel() {

      public Object getObject() {
        return getGalleryService().getImages(album);
      }

    };

    add(listEditor = new ListEditor<File>("images", model) {

      @Override
      protected File createItem(AjaxRequestTarget target) {
        // We could pop up a window, etc. but we take the easy route out for the
        // moment and do a redirect to the upload page.
        setResponsePage(UploadPage.class, new PageParameters("album=" + album));
        return null;
      }
      
      @Override
      protected void onDelete(AjaxRequestTarget target, File removedItem) {
        target.addComponent(getFeedbackPanel());
      }

      @Override
      protected Component getItemComponent(String id, IModel model, int index) {
        Fragment fragment = new Fragment(id, "imageFragment", EditAlbumPage.this);
        final File image = (File)model.getObject();
        ValueMap map = new ValueMap(
            String.format("album=%s,image=%s", album, image.getName()));
        fragment.add(new Image("thumbnail", new ResourceReference("thumbnail"), map));
        fragment.add(new AjaxEditableLabel("title", new IModel() {
        
          public void detach() {}
        
          public void setObject(Object object) {
            getGalleryService().renameImage(album, image.getName(), (String)object + ".jpg");
          }
        
          public Object getObject() {
            String name = image.getName();
            int lastDot = name.indexOf('.');
            if (lastDot > 0) {
              name = name.substring(0, lastDot);
            }
            return name;
          }
        
        }) {
          @Override
          protected void onSubmit(AjaxRequestTarget target) {
            target.addComponent(listEditor);
          }
        }.add(new PatternValidator("[^,=:/\\\\\\*\\|]+")));

        return fragment;
      }
    });
    listEditor.setOutputMarkupId(true);
  }
}
