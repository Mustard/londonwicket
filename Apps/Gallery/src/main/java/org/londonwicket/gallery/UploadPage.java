package org.londonwicket.gallery;

import java.io.File;
import java.io.IOException;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

public class UploadPage extends TemplatePage {
  
  private String uploadAlbum;
  private String newAlbumName;
  
  public UploadPage(PageParameters params) {
    uploadAlbum = params.getString("album", null);
    Form albumForm = new Form("albumForm") {
      @Override
      protected void onSubmit() {
        getGalleryService().createAlbum(newAlbumName);
        uploadAlbum = newAlbumName;
      }
    };
    add(albumForm);
    albumForm.add(new TextField("name", new PropertyModel(this, "newAlbumName")).setRequired(true));
    
    final FileUploadField uploadField = new FileUploadField("image");
    Form form = new Form("uploadForm") {
      @Override
      protected void onSubmit() {
        FileUpload upload = uploadField.getFileUpload();
        String fileName = upload.getClientFileName();
        if (fileName.contains("..")) {
          throw new RuntimeException("Dodgy filename: " + fileName);
        }
        try {
          upload.writeTo(new File(getGalleryService().getAlbum(uploadAlbum), fileName));
          info("Successfully uploaded: " + fileName);
          setResponsePage(ThumbnailPage.class);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
    add(form);
    uploadField.setRequired(true);
    form.add(uploadField);
    form.add(new DropDownChoice("album", new PropertyModel(this, "uploadAlbum"), new AbstractReadOnlyModel() {

      @Override
      public Object getObject() {
        return getGalleryService().getAlbums();
      }
    
    }).setRequired(true)); // Have to pick an album.
    
    form.add(new UploadProgressBar("progress", form));
    // Max size of uploaded file.
    form.setMaxSize(Bytes.megabytes(10));
  }
}
