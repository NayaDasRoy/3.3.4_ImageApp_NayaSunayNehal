/**
 * Unit tests for the public interface of Matrix2by2, including the
 * multiply(Vector1by2, Matrix2by2) function that drives image rotation.
 * Run main to see PASS/FAIL for each test.
 */
public class Matrix2by2Test
{
  public static void main(String[] args)
  {
    runTests();
    MiniTest.summary();
  }

  /** Runs every Matrix2by2 test (also called by AllTests). */
  public static void runTests()
  {
    System.out.println("Matrix2by2 tests");
    testGetters();
    testRowsAndColumns();
    testIdentity();
    testMatrixMultiply();
    testMatrixMultiplyNotCommutative();
    testVectorTimesMatrix();
    testRotationConstants();
    testRotationFunction();
    testRotationCombinations();
    testAddScaleTransposeDeterminant();
    testBadIndexThrows();
  }

  private static Matrix2by2 sample()
  {
    return new Matrix2by2(1, 2, 3, 4);
  }

  private static void testGetters()
  {
    Matrix2by2 m = sample();
    MiniTest.checkEquals("get(0,0)", 1.0, m.get(0, 0));
    MiniTest.checkEquals("get(0,1)", 2.0, m.get(0, 1));
    MiniTest.checkEquals("get(1,0)", 3.0, m.get(1, 0));
    MiniTest.checkEquals("get(1,1)", 4.0, m.get(1, 1));
  }

  private static void testRowsAndColumns()
  {
    Matrix2by2 m = sample();
    MiniTest.check("row 0 is [1,2]",
        Vector1by2.approxEquals(m.getRow(0), new Vector1by2(1, 2)));
    MiniTest.check("row 1 is [3,4]",
        Vector1by2.approxEquals(m.getRow(1), new Vector1by2(3, 4)));
    MiniTest.check("column 0 is [1,3]",
        Vector1by2.approxEquals(m.getColumn(0), new Vector1by2(1, 3)));
    MiniTest.check("column 1 is [2,4]",
        Vector1by2.approxEquals(m.getColumn(1), new Vector1by2(2, 4)));
  }

  private static void testIdentity()
  {
    Matrix2by2 m = sample();
    MiniTest.check("M * I = M",
        Matrix2by2.approxEquals(Matrix2by2.multiply(m, Matrix2by2.IDENTITY), m));
    MiniTest.check("I * M = M",
        Matrix2by2.approxEquals(Matrix2by2.multiply(Matrix2by2.IDENTITY, m), m));
    MiniTest.check("[7,-3] * I = [7,-3]",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(7, -3), Matrix2by2.IDENTITY),
            new Vector1by2(7, -3)));
  }

  private static void testMatrixMultiply()
  {
    // [[1,2],[3,4]] * [[5,6],[7,8]] = [[19,22],[43,50]]
    Matrix2by2 product = Matrix2by2.multiply(sample(), new Matrix2by2(5, 6, 7, 8));
    MiniTest.check("[[1,2],[3,4]] * [[5,6],[7,8]] = [[19,22],[43,50]]",
        Matrix2by2.approxEquals(product, new Matrix2by2(19, 22, 43, 50)));
  }

  private static void testMatrixMultiplyNotCommutative()
  {
    Matrix2by2 a = sample();
    Matrix2by2 b = new Matrix2by2(5, 6, 7, 8);
    // b * a = [[23,34],[31,46]], which is different from a * b
    MiniTest.check("[[5,6],[7,8]] * [[1,2],[3,4]] = [[23,34],[31,46]]",
        Matrix2by2.approxEquals(Matrix2by2.multiply(b, a),
                                new Matrix2by2(23, 34, 31, 46)));
    MiniTest.check("a * b is not the same as b * a",
        !Matrix2by2.approxEquals(Matrix2by2.multiply(a, b),
                                 Matrix2by2.multiply(b, a)));
  }

  private static void testVectorTimesMatrix()
  {
    // [1,2] * [[3,4],[5,6]] = [1*3+2*5, 1*4+2*6] = [13,16]
    Vector1by2 result = Matrix2by2.multiply(new Vector1by2(1, 2),
                                            new Matrix2by2(3, 4, 5, 6));
    MiniTest.check("[1,2] * [[3,4],[5,6]] = [13,16]",
        Vector1by2.approxEquals(result, new Vector1by2(13, 16)));

    MiniTest.check("zero vector stays zero",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(0, 0), sample()),
            new Vector1by2(0, 0)));

    // (v * A) * B must equal v * (A * B)
    Vector1by2 v = new Vector1by2(2, -5);
    Matrix2by2 a = sample();
    Matrix2by2 b = new Matrix2by2(0, 1, -1, 2);
    MiniTest.check("(v*A)*B = v*(A*B)",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(Matrix2by2.multiply(v, a), b),
            Matrix2by2.multiply(v, Matrix2by2.multiply(a, b))));
  }

  private static void testRotationConstants()
  {
    // On screen, row grows downward and col grows rightward.
    // 90 clockwise: the top-right corner (0,5) moves to the bottom-right (5,0).
    MiniTest.check("90 CW: (0,5) -> (5,0)",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(0, 5), Matrix2by2.ROTATE_90_CW),
            new Vector1by2(5, 0)));
    MiniTest.check("90 CW: (5,0) -> (0,-5)",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(5, 0), Matrix2by2.ROTATE_90_CW),
            new Vector1by2(0, -5)));
    MiniTest.check("90 CCW: (0,5) -> (-5,0)",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(0, 5), Matrix2by2.ROTATE_90_CCW),
            new Vector1by2(-5, 0)));
    MiniTest.check("180: (2,3) -> (-2,-3)",
        Vector1by2.approxEquals(
            Matrix2by2.multiply(new Vector1by2(2, 3), Matrix2by2.ROTATE_180),
            new Vector1by2(-2, -3)));
  }

  private static void testRotationFunction()
  {
    MiniTest.check("rotation(0) = identity",
        Matrix2by2.approxEquals(Matrix2by2.rotation(0), Matrix2by2.IDENTITY));
    MiniTest.check("rotation(90) = ROTATE_90_CW",
        Matrix2by2.approxEquals(Matrix2by2.rotation(90), Matrix2by2.ROTATE_90_CW));
    MiniTest.check("rotation(180) = ROTATE_180",
        Matrix2by2.approxEquals(Matrix2by2.rotation(180), Matrix2by2.ROTATE_180));
    MiniTest.check("rotation(-90) = ROTATE_90_CCW",
        Matrix2by2.approxEquals(Matrix2by2.rotation(-90), Matrix2by2.ROTATE_90_CCW));
    MiniTest.check("rotation(270) = ROTATE_90_CCW",
        Matrix2by2.approxEquals(Matrix2by2.rotation(270), Matrix2by2.ROTATE_90_CCW));
    MiniTest.checkEquals("rotation(90) has an exact 0 (no 6e-17 noise)",
        0.0, Matrix2by2.rotation(90).get(0, 0));
  }

  private static void testRotationCombinations()
  {
    Matrix2by2 cw = Matrix2by2.ROTATE_90_CW;
    Matrix2by2 ccw = Matrix2by2.ROTATE_90_CCW;
    MiniTest.check("CW * CCW = identity",
        Matrix2by2.approxEquals(Matrix2by2.multiply(cw, ccw), Matrix2by2.IDENTITY));
    MiniTest.check("CW * CW = 180",
        Matrix2by2.approxEquals(Matrix2by2.multiply(cw, cw), Matrix2by2.ROTATE_180));
    MiniTest.check("180 * 180 = identity",
        Matrix2by2.approxEquals(
            Matrix2by2.multiply(Matrix2by2.ROTATE_180, Matrix2by2.ROTATE_180),
            Matrix2by2.IDENTITY));
    Matrix2by2 fourTurns = Matrix2by2.multiply(
        Matrix2by2.multiply(cw, cw), Matrix2by2.multiply(cw, cw));
    MiniTest.check("four 90-degree turns = identity",
        Matrix2by2.approxEquals(fourTurns, Matrix2by2.IDENTITY));
    MiniTest.check("transpose of a rotation is its inverse",
        Matrix2by2.approxEquals(
            Matrix2by2.multiply(cw, Matrix2by2.transpose(cw)), Matrix2by2.IDENTITY));
  }

  private static void testAddScaleTransposeDeterminant()
  {
    MiniTest.check("add [[1,2],[3,4]] + [[5,6],[7,8]] = [[6,8],[10,12]]",
        Matrix2by2.approxEquals(
            Matrix2by2.add(sample(), new Matrix2by2(5, 6, 7, 8)),
            new Matrix2by2(6, 8, 10, 12)));
    MiniTest.check("scale [[1,2],[3,4]] by 2 = [[2,4],[6,8]]",
        Matrix2by2.approxEquals(Matrix2by2.scale(sample(), 2),
                                new Matrix2by2(2, 4, 6, 8)));
    MiniTest.check("transpose [[1,2],[3,4]] = [[1,3],[2,4]]",
        Matrix2by2.approxEquals(Matrix2by2.transpose(sample()),
                                new Matrix2by2(1, 3, 2, 4)));
    MiniTest.checkEquals("determinant [[1,2],[3,4]] = -2",
        -2.0, Matrix2by2.determinant(sample()));
    MiniTest.checkEquals("determinant of a rotation = 1",
        1.0, Matrix2by2.determinant(Matrix2by2.ROTATE_90_CW));
    MiniTest.checkEquals("determinant of identity = 1",
        1.0, Matrix2by2.determinant(Matrix2by2.IDENTITY));
  }

  private static void testBadIndexThrows()
  {
    boolean threw = false;
    try
    {
      sample().getRow(2);
    }
    catch (IllegalArgumentException e)
    {
      threw = true;
    }
    MiniTest.check("getRow(2) throws IllegalArgumentException", threw);

    threw = false;
    try
    {
      sample().getColumn(-1);
    }
    catch (IllegalArgumentException e)
    {
      threw = true;
    }
    MiniTest.check("getColumn(-1) throws IllegalArgumentException", threw);
  }
}
