import java.awt.Color;

/**
 * Tests that ImageEditor moves and recolors pixels correctly, using a tiny
 * 2-row by 3-col picture where every pixel has its own color so that we can
 * see exactly where each one lands.
 *
 *     A B C        (row 0)
 *     D E F        (row 1)
 */
public class ImageEditorTest
{
  private static final Color A = new Color(10, 20, 30);
  private static final Color B = new Color(40, 50, 60);
  private static final Color C = new Color(70, 80, 90);
  private static final Color D = new Color(100, 110, 120);
  private static final Color E = new Color(130, 140, 150);
  private static final Color F = new Color(160, 170, 180);

  public static void main(String[] args)
  {
    runTests();
    MiniTest.summary();
  }

  /** Runs every ImageEditor test (also called by AllTests). */
  public static void runTests()
  {
    System.out.println("ImageEditor tests");
    testRotate180();
    testRotate90Clockwise();
    testRotate90CounterClockwise();
    testRotationsUndoEachOther();
    testRecolors();
    testPaste();
  }

  private static Picture makeSample()
  {
    Picture pic = new Picture(2, 3);
    Pixel[][] p = pic.getPixels2D();
    p[0][0].setColor(A);  p[0][1].setColor(B);  p[0][2].setColor(C);
    p[1][0].setColor(D);  p[1][1].setColor(E);  p[1][2].setColor(F);
    return pic;
  }

  private static boolean layoutIs(Picture pic, Color[][] expected)
  {
    Pixel[][] p = pic.getPixels2D();
    if (p.length != expected.length || p[0].length != expected[0].length)
    {
      return false;
    }
    for (int r = 0; r < p.length; r++)
    {
      for (int c = 0; c < p[0].length; c++)
      {
        if (!p[r][c].getColor().equals(expected[r][c]))
        {
          return false;
        }
      }
    }
    return true;
  }

  private static void testRotate180()
  {
    //  F E D
    //  C B A
    Picture r = ImageEditor.rotate180(makeSample());
    MiniTest.check("rotate 180 -> 2x3 with F E D / C B A",
        layoutIs(r, new Color[][] { { F, E, D }, { C, B, A } }));
  }

  private static void testRotate90Clockwise()
  {
    //  D A
    //  E B
    //  F C
    Picture r = ImageEditor.rotate90Clockwise(makeSample());
    MiniTest.check("rotate 90 CW -> 3x2 with D A / E B / F C",
        layoutIs(r, new Color[][] { { D, A }, { E, B }, { F, C } }));
  }

  private static void testRotate90CounterClockwise()
  {
    //  C F
    //  B E
    //  A D
    Picture r = ImageEditor.rotate90CounterClockwise(makeSample());
    MiniTest.check("rotate 90 CCW -> 3x2 with C F / B E / A D",
        layoutIs(r, new Color[][] { { C, F }, { B, E }, { A, D } }));
  }

  private static void testRotationsUndoEachOther()
  {
    Color[][] original = { { A, B, C }, { D, E, F } };
    Picture there = ImageEditor.rotate90Clockwise(makeSample());
    MiniTest.check("CW then CCW gives back the original",
        layoutIs(ImageEditor.rotate90CounterClockwise(there), original));
    MiniTest.check("180 twice gives back the original",
        layoutIs(ImageEditor.rotate180(ImageEditor.rotate180(makeSample())), original));
    Picture quarter = makeSample();
    for (int i = 0; i < 4; i++)
    {
      quarter = ImageEditor.rotate90Clockwise(quarter);
    }
    MiniTest.check("four CW turns give back the original",
        layoutIs(quarter, original));
    MiniTest.check("transform with a rotation(90) matrix matches rotate90Clockwise",
        layoutIs(ImageEditor.transform(makeSample(), Matrix2by2.rotation(90)),
                 new Color[][] { { D, A }, { E, B }, { F, C } }));
  }

  private static void testRecolors()
  {
    Picture pic = makeSample();
    ImageEditor.changeColors(pic.getPixels2D());
    MiniTest.check("changeColors: (10,20,30) -> (30,10,20)",
        pic.getPixels2D()[0][0].getColor().equals(new Color(30, 10, 20)));

    pic = makeSample();
    ImageEditor.makeNegative(pic.getPixels2D());
    MiniTest.check("negative: (10,20,30) -> (245,235,225)",
        pic.getPixels2D()[0][0].getColor().equals(new Color(245, 235, 225)));

    pic = makeSample();
    ImageEditor.makeGrayscale(pic.getPixels2D());
    MiniTest.check("grayscale: (10,20,30) -> (20,20,20)",
        pic.getPixels2D()[0][0].getColor().equals(new Color(20, 20, 20)));
  }

  private static void testPaste()
  {
    Picture big = new Picture(3, 3);
    Pixel[][] bp = big.getPixels2D();
    for (Pixel[] row : bp)
    {
      for (Pixel p : row)
      {
        p.setColor(new Color(100, 100, 100));
      }
    }
    Picture small = new Picture(1, 2);                 // one row, two cols
    Pixel[][] sp = small.getPixels2D();
    sp[0][0].setColor(new Color(200, 0, 0));           // red pixel
    sp[0][1].setColor(Color.WHITE);                    // background pixel

    ImageEditor.paste(big, small, 1, 1, 1.0);
    MiniTest.check("opaque paste: red replaces the big color",
        big.getPixels2D()[1][1].getColor().equals(new Color(200, 0, 0)));
    MiniTest.check("white background is not pasted",
        big.getPixels2D()[1][2].getColor().equals(new Color(100, 100, 100)));
    MiniTest.check("pixels outside the paste are untouched",
        big.getPixels2D()[0][0].getColor().equals(new Color(100, 100, 100)));

    ImageEditor.paste(big, small, 2, 0, 0.5);
    MiniTest.check("50% blend of (200,0,0) over (100,100,100) = (150,50,50)",
        big.getPixels2D()[2][0].getColor().equals(new Color(150, 50, 50)));

    ImageEditor.paste(big, small, 2, 2, 1.0);          // hangs off the right edge
    MiniTest.check("paste that hangs off the edge is clipped, no crash",
        big.getPixels2D()[2][2].getColor().equals(new Color(200, 0, 0)));

    MiniTest.check("isBackground: white and near-white are background",
        ImageEditor.isBackground(Color.WHITE)
        && ImageEditor.isBackground(new Color(250, 248, 245)));
    MiniTest.check("isBackground: colors are not background",
        !ImageEditor.isBackground(new Color(255, 255, 100))
        && !ImageEditor.isBackground(Color.BLACK));
  }
}
