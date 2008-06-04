package org.londonwicket.gallery;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.UnsharpFilter;

public class GalleryService {
  private static final Logger LOG = LoggerFactory.getLogger(GalleryService.class);
  
  private static final int THUMB_SIZE = 96;
  
  private File base, albums, scaleds, thumbs;

  public static enum RenameStatus {
    OK, DESTINATION_EXISTS, FAILURE
  }
  
  private static final FileFilter DIRECTORIES_ONLY = new FileFilter() {

    public boolean accept(File file) {
      return file.isDirectory();
    }

  };

  private static final FileFilter IMAGES_ONLY = new FileFilter() {

    public boolean accept(File file) {
      return file.getName().toLowerCase().endsWith(".jpg");
    }

  };
  
  public GalleryService(String baseDir) {
    base = new File(baseDir);
    ensureDirExists(base);
    albums = ensureDirExists(new File(base, "albums"));
    scaleds = ensureDirExists(new File(base, "scaled"));
    thumbs = ensureDirExists(new File(base, "thumbs"));
  }

  public List<String> getAlbums() {
    List<String> result = new ArrayList<String>();
    File[] listing = albums.listFiles(DIRECTORIES_ONLY);
    for (File dir : listing) {
      result.add(dir.getName());
    }
    return result;
  }

  public void createAlbum(String album) {
    checkPath(album);
    ensureDirExists(new File(albums, album));
  }

  public File getAlbum(String album) {
    checkPath(album);
    return new File(albums, album);
  }
  
  public RenameStatus renameAlbum(String existingName, String newName) {
    if (getAlbum(newName).exists()) {
      return RenameStatus.DESTINATION_EXISTS;
    }
    if (!getAlbum(existingName).renameTo(getAlbum(newName))) {
      return RenameStatus.FAILURE;
    }
    // Assume that if we can rename the original album, we can rename the thumbs and scaleds.
    new File(thumbs, existingName).renameTo(new File(thumbs, newName));
    new File(scaleds, existingName).renameTo(new File(scaleds, newName));
    

    return RenameStatus.OK;
  }
  
  public List<File> getImages(String album) {
    return new AlbumList(album);
  }
  
  public List<Image> getImagesWithoutThumbnails() {
    List<Image> result = new ArrayList<Image>();
    for (File album : albums.listFiles(DIRECTORIES_ONLY)) {
      for (File image : album.listFiles(IMAGES_ONLY)) {
        if (!getThumbnail(image).exists()) {
          result.add(new Image(album.getName(), image.getName()));
        }
      }
    }
    return result;
  }
  
  public File getImage(String album, String image) {
    checkPath(album);
    checkPath(image);
    return new File(getAlbum(album), image);
  }
  
  public void renameImage(String album, String existingImage, String newImage) {
    File existingFile = getImage(album, existingImage);
    File newFile = getImage(album, newImage);
    existingFile.renameTo(newFile);
    getThumbnail(existingFile).renameTo(getThumbnail(newFile));
    
    // Preserve ordering through file renames.
    try {
      File sortFile = new File(getAlbum(album), "sorting.txt");
      if (sortFile.exists()) {
        File newSortFile = new File(getAlbum(album), "sorting-new.txt");
        PrintWriter writer = new PrintWriter(newSortFile);
        BufferedReader reader = new BufferedReader(new FileReader(sortFile));
        String file;
        while ((file = reader.readLine()) != null) {
          File image = getImage(album, file);
          if (image.equals(existingFile)) {
            writer.println(newFile.getName());
          } else {
            writer.println(file);
          }
        }
        reader.close();
        writer.close();
        sortFile.delete();
        newSortFile.renameTo(sortFile);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public File getThumbnail(File image) {
    try {
      String localPart = image.getCanonicalPath().substring(
          albums.getCanonicalPath().length());
      return new File(thumbs, localPart);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public File getScaledImage(String album, String image, int max) {
    File file = getImage(album, image);
    ImageInfo info = new ImageInfo();
    try {
      info.setInput(new FileInputStream(file));
  
      if (info.check()) {
        // If it's within the dimensions, just return it.
        if (info.getWidth() <= max && info.getHeight() <= max) {
          return file;
        }
        // Else go find a scaled image instead.
        else {
          File scaledAlbum = new File(scaleds, album);
          ensureDirExists(scaledAlbum);
          File scaledImage = new File(scaledAlbum, image + "-" + max + ".jpg");
          if (scaledImage.exists()) {
            // Great - we've already made it.
            return scaledImage;
          }
          long start = System.currentTimeMillis();
          
          //
          // Otherwise, we need to scale the image.
          //

          // Load the image in.
          BufferedImage originalImage = ImageIO.read(file);

          // Figure out the new dimensions.
          int w = originalImage.getWidth();
          int h = originalImage.getHeight();
          double maxOriginal = Math.max(w, h);
          double scaling = max / maxOriginal;
          
          int newW = (int)Math.round(scaling * w);
          int newH = (int)Math.round(scaling * h);

          /*
          BufferedImageOp op = new GaussianFilter(0.5f / (float)scaling);
          originalImage = op.filter(originalImage, null);
          */
          
          // If we need to scale down by more than 2x, scale to double the
          // eventual size and then scale again. This provides much higher
          // quality results.
          if (scaling < 0.5f) {
            LOG.info("Scaling twice for better quality...");
            BufferedImage newImg = new BufferedImage(newW * 2, newH * 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D gfx = newImg.createGraphics();
            gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gfx.drawImage(originalImage, 0, 0, newW * 2, newH * 2, null);
            gfx.dispose();
            newImg.flush();
            originalImage = newImg;
          }
          
          // Scale it.
          BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
          Graphics2D gfx = newImg.createGraphics();
          gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
          gfx.drawImage(originalImage, 0, 0, newW, newH, null);
          gfx.dispose();
          newImg.flush();
          originalImage.flush();
          
          // Run an unsharp filter.
          LOG.info("Running unsharp mask...");
          UnsharpFilter unsharp = new UnsharpFilter();
          unsharp.setAmount(0.25f);
          newImg = unsharp.filter(newImg, null);

          // Output.
          writeJpeg(newImg, scaledImage, 0.85f);

          long timing = System.currentTimeMillis() - start;
          LOG.info("Created new scaled image in {}ms: {}", timing, scaledImage);
          return scaledImage;
        }
      }
    } catch (IOException e) {
      LOG.error("Error creating scaled image.", e);
    }
    // Don't scale it.
    return file;
  }

  public void createThumbnail(Image originalImage, Rectangle thumbnailCoords, int res) {
    try {
      File originalFile = getScaledImage(originalImage.getAlbum(), originalImage.getImage(), res);
      BufferedImage original = ImageIO.read(originalFile);

      if (thumbnailCoords.width > 140) {
        // Gaussian blur the original before making a thumbnail, so they have less artefacts and load faster.
        // 128 is pretty arbitrary and was selected after some trial and error.
        BufferedImageOp op = new GaussianFilter(thumbnailCoords.width / 128);
        original = op.filter(original, null);
      }
      
      BufferedImage intermediate = new BufferedImage(THUMB_SIZE * 2, THUMB_SIZE * 2, BufferedImage.TYPE_INT_RGB);
      Graphics2D gfx = intermediate.createGraphics();
      gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      Rectangle c = thumbnailCoords;
      LOG.info("Thumbnail coords: {},{} - {}x{}", new Object[] {c.x, c.y, c.width, c.height});
      BufferedImage thumbImage = original.getSubimage(c.x, c.y, c.width, c.height);
      gfx.drawImage(thumbImage, 0, 0, THUMB_SIZE * 2, THUMB_SIZE * 2, null);
      gfx.dispose();
      intermediate.flush();
      
      BufferedImage thumbnail = new BufferedImage(THUMB_SIZE, THUMB_SIZE, BufferedImage.TYPE_INT_RGB);
      gfx = thumbnail.createGraphics();
      gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      gfx.drawImage(intermediate, 0, 0, THUMB_SIZE, THUMB_SIZE, null);
      gfx.dispose();
      thumbnail.flush();
      
      UnsharpFilter unsharp = new UnsharpFilter();
      unsharp.setAmount(0.1f);
      unsharp.setRadius(1.7f);
      thumbnail = unsharp.filter(thumbnail, null);
      
      File thumbFile = getThumbnail(getImage(originalImage.getAlbum(), originalImage.getImage()));
      ensureDirExists(thumbFile.getParentFile());
      
      writeJpeg(thumbnail, thumbFile, 0.85f);
      
    } catch (IOException e) {
      LOG.error("Could not create thumbnail", e);
    }
    
  }
  
  private void writeJpeg(BufferedImage image, File file, float quality) throws IOException {
    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
    ImageWriteParam iwp = writer.getDefaultWriteParam();
    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    iwp.setCompressionQuality(quality);

    FileImageOutputStream output = new FileImageOutputStream(file);
    writer.setOutput(output);
    writer.write(null, new IIOImage(image, null, null), iwp);
  }
  
  private File ensureDirExists(File dir) {
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        throw new RuntimeException("Couldn't make directory " + dir);
      }
    }
    return dir;
  }
  
  private void checkPath(String path) {
    if (path.contains("..") || path.contains("/")) {
      throw new IllegalArgumentException(
          "Paths with .. or / in the name are not allowed.");
    }
  }
  
  public static class Image implements Serializable, Comparable<Image> {
    private String album, image;
    
    public Image(String album, String image) {
      this.album = album;
      this.image = image;
    }

    public String getAlbum() {
      return album;
    }

    public String getImage() {
      return image;
    }
    
    @Override
    public String toString() {
      return album + "/" + image;
    }

    public int compareTo(Image o) {
      int result = album.compareTo(o.album);
      if (result != 0) {
        return result;
      }
      return image.compareTo(o.image);
    }
    
  }
  
  private class AlbumList extends AbstractList<File> {

    private final String album;
    private File[] files;
    private long lastChecked;
    private ArrayList<File> order;
    
    public AlbumList(String album) {
      checkPath(album);
      this.album = album;
      updateFiles();
    }
    
    private synchronized void updateFiles() {
      // Update at most 10 times a second.
      long now = System.currentTimeMillis();
      if (now - lastChecked > 100) {
        lastChecked = now;
        files = getAlbum(album).listFiles(IMAGES_ONLY);
        try {
          order = new ArrayList<File>(files.length);
          File sortFile = new File(getAlbum(album), "sorting.txt");
          if (sortFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(sortFile));
            String file;
            while ((file = reader.readLine()) != null) {
              File image = getImage(album, file);
              if (image.exists()) {
                order.add(image);
              }
            }
            reader.close();
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        for (File file : files) {
          if (!order.contains(file)) {
            order.add(file);
          }
        }
        files = order.toArray(new File[order.size()]);
      }
    }

    public void clear() {
      throw new UnsupportedOperationException("clear() is not supported - dangerous!");
    }

    public synchronized File get(int index) {
      updateFiles();
      return files[index];
    }

    public synchronized File remove(int index) {
      updateFiles();
      File file = files[index];
      file.delete();
      getThumbnail(file).delete();
      updateFiles();
      return file;
    }

    public synchronized File set(int index, File element) {
      File result = order.set(index, element);
      files = order.toArray(new File[order.size()]);
      try {
        PrintWriter writer = new PrintWriter(new File(getAlbum(album), "sorting.txt"));
        for (File file : order) {
          writer.println(file.getName());
        }
        writer.flush();
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return result;
    }

    public synchronized int size() {
      updateFiles();
      return getAlbum(album).listFiles(IMAGES_ONLY).length;
    }    
  }
}
