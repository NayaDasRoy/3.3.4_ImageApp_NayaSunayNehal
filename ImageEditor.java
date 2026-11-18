import java.awt.Color;

/**
 * ImageEditor: the 2D array algorithms used by ImageApp.
 *
 * Three kinds of edits are provided:
 *   1. Recoloring   - change the RGB values of every Pixel (in place).
 *   2. Rotating     - move every Pixel to a new (row, col) using
 *                     Matrix2by2 / Vector1by2 math (returns a new Picture).
 *   3. Pasting      - copy a small Picture on top of a large one, with the
 *                     small picture's white background removed.
 *
 * Every image is a Pixel[][] indexed [row][col], where [0][0] is the upper
 * left corner, row grows downward and col grows rightward.
 */
public class ImageEditor
{
  /** Smallest red/green/blue value that still counts as "white background". */
  public static final int WHITE_THRESHOLD = 240;

  // -------------------------------------------------------------------
  // Recoloring (these change the Pixels they are given)
  // -------------------------------------------------------------------

  /**
   * Image #1: recolors an image by reordering the color channels. The old
   * (red, green, blue) becomes (blue, red, green), so skies turn orange-ish
   * and grass turns blue-ish.
   *
   * @param pixels the pixels to recolor, changed in place
   */
  public static void changeColors(Pixel[][] pixels)
  {
    for (int row = 0; row < pixels.length; row++)
    {
      for (int col = 0; col < pixels[row].length; col++)
      {
        Color old = pixels[row][col].getColor();
        pixels[row][col].setColor(
            new Color(old.getBlue(), old.getRed(), old.getGreen()));
      }
    }
  }

  /**
   * Image #2: makes a photographic negative by replacing each color value
   * with 255 minus itself (white becomes black, red becomes cyan, ...).
   *
   * @param pixels the pixels to invert, changed in place
   */
  public static void makeNegative(Pixel[][] pixels)
  {
    for (Pixel[] rowOfPixels : pixels)
    {
      for (Pixel p : rowOfPixels)
      {
        Color old = p.getColor();
        p.setColor(new Color(255 - old.getRed(),
                             255 - old.getGreen(),
                             255 - old.getBlue()));
      }
    }
  }

  /**
   * Image #3: makes a grayscale image by setting red, green and blue to the
   * average of the three original values.
   *
   * @param pixels the pixels to convert, changed in place
   */
  public static void makeGrayscale(Pixel[][] pixels)
  {
    for (Pixel[] rowOfPixels : pixels)
    {
      for (Pixel p : rowOfPixels)
      {
        Color old = p.getColor();
        int average = (old.getRed() + old.getGreen() + old.getBlue()) / 3;
        p.setColor(new Color(average, average, average));
      }
    }
  }

  // -------------------------------------------------------------------
  // Rotating (these return a NEW Picture; the source is not changed)
  // -------------------------------------------------------------------

  /**
   * Images #4, #5 and #6: transforms every pixel position with a matrix.
   *
   * For each source pixel at (row, col):
   *     newPosition = [row, col] * matrix            (Matrix2by2.multiply)
   * The four corners of the source are transformed first to find out how
   * big the new picture must be and how far to slide everything so the
   * smallest new row and col become 0. That way no pixel falls off the
   * edge, and any spot nothing lands on stays white.
   *
   * This gives exact results for rotations of 0, 90, 180 and 270 degrees
   * (use Matrix2by2.ROTATE_180, ROTATE_90_CW, ROTATE_90_CCW).
   *
   * @param source the picture to rotate (not changed)
   * @param matrix the transformation matrix
   * @return a new, rotated Picture
   */
  public static Picture transform(Picture source, Matrix2by2 matrix)
  {
    Pixel[][] src = source.getPixels2D();
    int height = src.length;
    int width = src[0].length;

    // Where do the four corners go? They define the new bounding box.
    Vector1by2[] corners = {
        Matrix2by2.multiply(new Vector1by2(0, 0), matrix),
        Matrix2by2.multiply(new Vector1by2(0, width - 1), matrix),
        Matrix2by2.multiply(new Vector1by2(height - 1, 0), matrix),
        Matrix2by2.multiply(new Vector1by2(height - 1, width - 1), matrix)
    };

    int minRow = corners[0].roundedRow();
    int maxRow = minRow;
    int minCol = corners[0].roundedCol();
    int maxCol = minCol;

    for (Vector1by2 corner : corners)
    {
      minRow = Math.min(minRow, corner.roundedRow());
      maxRow = Math.max(maxRow, corner.roundedRow());
      minCol = Math.min(minCol, corner.roundedCol());
      maxCol = Math.max(maxCol, corner.roundedCol());
    }

    // New pictures start out all white, so un-landed-on pixels are white.
    Picture result = new Picture(maxRow - minRow + 1, maxCol - minCol + 1);
    Pixel[][] dest = result.getPixels2D();

    for (int row = 0; row < height; row++)
    {
      for (int col = 0; col < width; col++)
      {
        Vector1by2 moved = Matrix2by2.multiply(new Vector1by2(row, col), matrix);
        int newRow = moved.roundedRow() - minRow;
        int newCol = moved.roundedCol() - minCol;
        dest[newRow][newCol].setColor(src[row][col].getColor());
      }
    }
    return result;
  }

  /**
   * Convenience: rotates a picture 180 degrees.
   *
   * @param source the picture to rotate (not changed)
   * @return a new Picture, upside down
   */
  public static Picture rotate180(Picture source)
  {
    return transform(source, Matrix2by2.ROTATE_180);
  }

  /**
   * Convenience: rotates a picture 90 degrees clockwise.
   *
   * @param source the picture to rotate (not changed)
   * @return a new Picture, turned clockwise (width and height swap)
   */
  public static Picture rotate90Clockwise(Picture source)
  {
    return transform(source, Matrix2by2.ROTATE_90_CW);
  }

  /**
   * Convenience: rotates a picture 90 degrees counterclockwise
   * (the same as 270 degrees clockwise, or -90).
   *
   * @param source the picture to rotate (not changed)
   * @return a new Picture, turned counterclockwise (width and height swap)
   */
  public static Picture rotate90CounterClockwise(Picture source)
  {
    return transform(source, Matrix2by2.ROTATE_90_CCW);
  }

  // -------------------------------------------------------------------
  // Pasting a small image onto a large one
  // -------------------------------------------------------------------

  /**
   * Tells whether a color is part of the small image's white background.
   * A color counts as background when red, green and blue are all at least
   * WHITE_THRESHOLD, which also catches off-white JPEG/PNG edge pixels.
   *
   * @param c the color to test
   * @return true if the color is (nearly) pure white
   */
  public static boolean isBackground(Color c)
  {
    return c.getRed() >= WHITE_THRESHOLD
        && c.getGreen() >= WHITE_THRESHOLD
        && c.getBlue() >= WHITE_THRESHOLD;
  }

  /**
   * Cuts out a small picture and pastes it on top of a large picture. The
   * small picture's white background is removed (those pixels are skipped,
   * so the large picture shows through).
   *
   * The opacity decides how the small colors combine with the large colors:
   *   1.0 = complete change: the pixel becomes the small picture's color.
   *   0.5 = blend: half small color, half large color.
   *   0.0 = no change at all.
   * Each color value is   small * opacity + large * (1 - opacity).
   *
   * Any part of the small picture that would hang off the edge of the large
   * picture is clipped.
   *
   * @param large   the picture to paste onto, changed in place
   * @param small   the picture to paste (not changed)
   * @param top     the row of the large picture where the small one's top goes
   * @param left    the col of the large picture where the small one's left goes
   * @param opacity how strongly the small colors replace the large (0.0 to 1.0)
   */
  public static void paste(Picture large, Picture small,
                           int top, int left, double opacity)
  {
    Pixel[][] bigPixels = large.getPixels2D();
    Pixel[][] smallPixels = small.getPixels2D();

    for (int row = 0; row < smallPixels.length; row++)
    {
      for (int col = 0; col < smallPixels[row].length; col++)
      {
        int bigRow = top + row;
        int bigCol = left + col;

        boolean onBigPicture = bigRow >= 0 && bigRow < bigPixels.length
                            && bigCol >= 0 && bigCol < bigPixels[0].length;
        if (!onBigPicture)
        {
          continue;                                // clipped off the edge
        }

        Color smallColor = smallPixels[row][col].getColor();
        if (isBackground(smallColor))
        {
          continue;                                // remove the white background
        }

        Color bigColor = bigPixels[bigRow][bigCol].getColor();
        bigPixels[bigRow][bigCol].setColor(blend(smallColor, bigColor, opacity));
      }
    }
  }

  /**
   * Mixes two colors: first * weight + second * (1 - weight), per channel.
   *
   * @param first  the color that gets "weight"
   * @param second the color that gets "1 - weight"
   * @param weight a number from 0.0 to 1.0
   * @return the mixed color
   */
  public static Color blend(Color first, Color second, double weight)
  {
    int red = (int) Math.round(first.getRed() * weight
                             + second.getRed() * (1 - weight));
    int green = (int) Math.round(first.getGreen() * weight
                               + second.getGreen() * (1 - weight));
    int blue = (int) Math.round(first.getBlue() * weight
                              + second.getBlue() * (1 - weight));
    return new Color(red, green, blue);
  }
}
