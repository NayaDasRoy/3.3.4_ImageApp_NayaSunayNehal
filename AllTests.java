/**
 * Runs every unit test in the project and prints one overall summary.
 * Run this file to prove that Vector1by2, Matrix2by2 and ImageEditor all work.
 */
public class AllTests
{
  public static void main(String[] args)
  {
    Vector1by2Test.runTests();
    System.out.println();
    Matrix2by2Test.runTests();
    System.out.println();
    ImageEditorTest.runTests();
    MiniTest.summary();
  }
}
