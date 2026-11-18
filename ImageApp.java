/*
  ImageApp  (PLTW AP CSA 3.3.4)

  PURPOSE
    Practice 2D array algorithms by manipulating the Pixel[][] of images.

  HOW THIS PROGRAM MEETS THE REQUIREMENTS
    1. Recolor three images three different ways ......... ImageEditor.changeColors
                                                            ImageEditor.makeNegative
                                                            ImageEditor.makeGrayscale
    2. Rotate three ways: 180, 90 clockwise, 90 counter-
       clockwise, using matrix multiplication ............ ImageEditor.transform, which uses
                                                            Vector1by2 (row/col vectors, dot product)
                                                            and Matrix2by2 (rotation matrices,
                                                            vector-times-matrix multiply)
    3. Add a small image to a larger one, removing the
       small image's white background (one final image shows both ways):
         a) complete change to the small image's colors .. ImageEditor.paste(..., 1.0)
         b) blend with the large image's colors .......... ImageEditor.paste(..., 0.5)
    4. Unit tests for the vector and matrix classes ...... run AllTests.java

  OUTPUT
    Exactly seven images are shown: 3 recolored, 3 rotated, 1 combined.

  TO SAVE SCREENSHOTS FOR THE WRITE-UP (optional)
    Run with the Java option  -DsaveDir=screenshots  to write each image to a
    PNG file in that folder instead of only opening a window.
*/
import java.awt.Color;
import java.io.File;

public class ImageApp
{
  // Files from the lib (large images) and lib2 (small images, white background) folders
  private static final String LARGE_FILE = "lib/beach.jpg";
  private static final String SMALL_FILE_1 = "lib2/rocket.png";
  private static final String SMALL_FILE_2 = "lib2/fish.png";

  public static void main(String[] args)
  {
    // use any file from the lib folder
    String pictureFile = LARGE_FILE;

    // Image #1 Using the original image and pixels, recolor an image by changing the RGB color of each Pixel
    Picture recoloredImg = new Picture(pictureFile);
    Pixel[][] recoloredPixels = recoloredImg.getPixels2D();
    ImageEditor.changeColors(recoloredPixels);            // (r, g, b) -> (b, r, g)
    show(recoloredImg, "1_recolored");

    // Image #2 Using the original image and pixels, create a photographic negative of the image
    Picture negImg = new Picture(pictureFile);
    Pixel[][] negPixels = negImg.getPixels2D();
    ImageEditor.makeNegative(negPixels);                  // each value -> 255 - value
    show(negImg, "2_negative");

    // Image #3 Using the original image and pixels, create a grayscale version of the image
    Picture grayscaleImg = new Picture(pictureFile);
    Pixel[][] grayscalePixels = grayscaleImg.getPixels2D();
    ImageEditor.makeGrayscale(grayscalePixels);           // r = g = b = average
    show(grayscaleImg, "3_grayscale");

    // Image #4 Using the original image and pixels, rotate it 180 degrees
    // (matrix multiplication: [row, col] * ROTATE_180)
    Picture upsidedownImage = ImageEditor.transform(new Picture(pictureFile),
                                                    Matrix2by2.ROTATE_180);
    show(upsidedownImage, "4_rotate_180");

    // Image #5 Using the original image and pixels, rotate image 90
    // (clockwise; the picture's width and height swap)
    Picture rotateImg = ImageEditor.transform(new Picture(pictureFile),
                                              Matrix2by2.ROTATE_90_CW);
    show(rotateImg, "5_rotate_90_clockwise");

    // Image #6 Using the original image and pixels, rotate image -90
    // (counterclockwise; the picture's width and height swap)
    Picture rotateImg2 = ImageEditor.transform(new Picture(pictureFile),
                                               Matrix2by2.ROTATE_90_CCW);
    show(rotateImg2, "6_rotate_90_counterclockwise");


    // Final Image: Add a small image to a larger one
    /*
      PSEUDOCODE: add a smaller image to a larger image

      paste(large, small, top, left, opacity):
        for each row of the small image:
          for each col of the small image:
            bigRow = top + row
            bigCol = left + col

            if (bigRow, bigCol) is off the edge of the large image:
              skip this pixel                          // clip

            smallColor = color of small[row][col]
            if smallColor is white (or nearly white):
              skip this pixel                          // remove the background

            bigColor = color of large[bigRow][bigCol]
            new color = smallColor * opacity + bigColor * (1 - opacity)
            set large[bigRow][bigCol] to the new color

      opacity 1.0 -> complete change to the small image's colors
      opacity 0.5 -> blend of small and large colors
    */

    // ONE final image shows both ways of combining:
    //   the rocket completely replaces the large image's colors (opacity 1.0)
    //   the fish blends 50/50 with the large image's colors (opacity 0.5)
    Picture finalImg = new Picture(pictureFile);
    ImageEditor.paste(finalImg, new Picture(SMALL_FILE_1), 100, 450, 1.0);
    ImageEditor.paste(finalImg, new Picture(SMALL_FILE_2), 330, 60, 0.5);
    show(finalImg, "7_final_image");
  }

  /**
   * Shows a picture in a window. If the program was started with
   * -DsaveDir=somefolder, the picture is ALSO saved there as a PNG file,
   * which makes it easy to collect screenshots for the write-up.
   *
   * @param pic  the picture to show
   * @param name the file name (without extension) to use when saving
   */
  private static void show(Picture pic, String name)
  {
    String saveDir = System.getProperty("saveDir");
    if (saveDir != null)
    {
      new File(saveDir).mkdirs();
      pic.write(saveDir + File.separator + name + ".png");
    }
    else
    {
      pic.explore();
    }
  }
}
