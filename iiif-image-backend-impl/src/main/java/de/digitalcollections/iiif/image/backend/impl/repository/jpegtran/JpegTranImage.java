package de.digitalcollections.iiif.image.backend.impl.repository.jpegtran;

import de.digitalcollections.iiif.image.model.api.Image;
import de.digitalcollections.iiif.image.model.api.RegionParameters;
import de.digitalcollections.iiif.image.model.api.ResizeParameters;
import de.digitalcollections.iiif.image.model.api.enums.ImageBitDepth;
import de.digitalcollections.iiif.image.model.api.enums.ImageFormat;
import de.digitalcollections.iiif.image.model.api.exception.InvalidParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpegTranImage implements Image {

  private static final Logger LOGGER = LoggerFactory.getLogger(JpegTranImage.class);

  private JpegImage jpegImage;
  private int height;
  private int width;

  public JpegTranImage(byte[] imgData) {
    this(new JpegImage(imgData));
  }

  private JpegTranImage(JpegImage image) {
    this.setBackendImage(image);
  }

  private void setBackendImage(JpegImage image) {
    this.jpegImage = image;
    this.width = image.getWidth();
    this.height = image.getHeight();
  }

  @Override
  public Image flipHorizontally() {
    this.setBackendImage(jpegImage.flipHorizontal());
    return this;
  }

  @Override
  public ImageFormat getFormat() {
    return ImageFormat.JPEG;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public byte[] toByteArray() {
    return jpegImage.toByteArray();
  }

  @Override
  public Image crop(RegionParameters params) throws InvalidParametersException {
    try {
      int x, y, width, height;
      int origWidth = getWidth();
      int origHeight = getHeight();
      if (params.isAbsolute()) {
        x = (int) Math.ceil(params.getHorizontalOffset());
        y = (int) Math.ceil(params.getVerticalOffset());
        width = (int) Math.ceil(params.getWidth());
        height = (int) Math.ceil(params.getHeight());
      } else {
        x = (int) Math.ceil(getWidth() * (params.getHorizontalOffset()) / 100);
        y = (int) Math.ceil(getHeight() * (params.getVerticalOffset()) / 100);
        width = (int) Math.ceil(getWidth() * (params.getWidth()) / 100);
        height = (int) Math.ceil(getHeight() * (params.getHeight()) / 100);
      }
      if (x >= origWidth || y >= origHeight) {
        throw new InvalidParametersException(String.
                format("x and/or y out of bounds (image size is %sx%s)", origWidth, origHeight));
      }
      if (width > (origWidth - x)) {
        width = origWidth - x;
      }
      if (height > (origHeight - y)) {
        height = origHeight - y;
      }
      setBackendImage(jpegImage.crop(x, y, width, height));
    } catch (IllegalArgumentException e) {
      throw new InvalidParametersException(e.getMessage());
    }
    return this;
  }

  @Override
  public Image scale(ResizeParameters params) throws InvalidParametersException {
    int oldWidth = getWidth();
    int oldHeight = getHeight();

    int newWidth = (int) Math.ceil(params.getWidth());
    int newHeight = (int) Math.ceil(params.getHeight());
    try {
      if (oldWidth == newWidth && oldHeight == newHeight) {
        return this;
      }
      LOGGER.info("Downscaling from old size {}x{} to new size {}x{}", new String[]{"" + oldWidth,
        "" + oldHeight, "" + newWidth, "" + newHeight});
      setBackendImage(jpegImage.downScale(newWidth, newHeight));
    } catch (IllegalArgumentException e) {
      LOGGER.error("Downscaling image failed");
      LOGGER.info("ResizeParameters: " + params.toString());
    }
    return this;
  }

  @Override
  public Image rotate(int arcDegree) throws InvalidParametersException {
    try {
      setBackendImage(jpegImage.rotate(arcDegree));
    } catch (IllegalArgumentException e) {
      throw new InvalidParametersException(e.getMessage());
    }
    return this;
  }

  @Override
  public Image toDepth(ImageBitDepth depth) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Bit depth transformations are not supported.");
  }

  @Override
  public Image convert(ImageFormat format) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Format conversions are not supported");
  }
}