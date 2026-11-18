/**
 * Unit tests for the public interface of Vector1by2.
 * Run main to see PASS/FAIL for each test.
 */
public class Vector1by2Test
{
  public static void main(String[] args)
  {
    runTests();
    MiniTest.summary();
  }

  /** Runs every Vector1by2 test (also called by AllTests). */
  public static void runTests()
  {
    System.out.println("Vector1by2 tests");
    testConstructorAndGetters();
    testAdd();
    testSubtract();
    testScale();
    testDot();
    testApproxEquals();
    testRounding();
    testToString();
    testImmutable();
  }

  private static void testConstructorAndGetters()
  {
    Vector1by2 v = new Vector1by2(3, -4.5);
    MiniTest.checkEquals("getRow", 3.0, v.getRow());
    MiniTest.checkEquals("getCol", -4.5, v.getCol());
  }

  private static void testAdd()
  {
    Vector1by2 sum = Vector1by2.add(new Vector1by2(1, 2), new Vector1by2(3, 4));
    MiniTest.check("add [1,2]+[3,4] = [4,6]",
        Vector1by2.approxEquals(sum, new Vector1by2(4, 6)));

    Vector1by2 zero = Vector1by2.add(new Vector1by2(5, -7), new Vector1by2(0, 0));
    MiniTest.check("adding the zero vector changes nothing",
        Vector1by2.approxEquals(zero, new Vector1by2(5, -7)));

    Vector1by2 a = new Vector1by2(1, 2);
    Vector1by2 b = new Vector1by2(-8, 9);
    MiniTest.check("add is commutative",
        Vector1by2.approxEquals(Vector1by2.add(a, b), Vector1by2.add(b, a)));
  }

  private static void testSubtract()
  {
    Vector1by2 diff = Vector1by2.subtract(new Vector1by2(5, 7), new Vector1by2(2, 10));
    MiniTest.check("subtract [5,7]-[2,10] = [3,-3]",
        Vector1by2.approxEquals(diff, new Vector1by2(3, -3)));

    Vector1by2 v = new Vector1by2(4, 4);
    MiniTest.check("v - v = zero vector",
        Vector1by2.approxEquals(Vector1by2.subtract(v, v), new Vector1by2(0, 0)));
  }

  private static void testScale()
  {
    Vector1by2 scaled = Vector1by2.scale(new Vector1by2(2, -3), 4);
    MiniTest.check("scale [2,-3] by 4 = [8,-12]",
        Vector1by2.approxEquals(scaled, new Vector1by2(8, -12)));

    MiniTest.check("scale by 1 changes nothing",
        Vector1by2.approxEquals(Vector1by2.scale(new Vector1by2(2, -3), 1),
                                new Vector1by2(2, -3)));
    MiniTest.check("scale by 0 gives the zero vector",
        Vector1by2.approxEquals(Vector1by2.scale(new Vector1by2(2, -3), 0),
                                new Vector1by2(0, 0)));
  }

  private static void testDot()
  {
    MiniTest.checkEquals("dot [1,2].[3,4] = 11",
        11.0, Vector1by2.dot(new Vector1by2(1, 2), new Vector1by2(3, 4)));
    MiniTest.checkEquals("dot of perpendicular vectors [1,0].[0,5] = 0",
        0.0, Vector1by2.dot(new Vector1by2(1, 0), new Vector1by2(0, 5)));
    MiniTest.checkEquals("dot [-2,3].[4,-1] = -11",
        -11.0, Vector1by2.dot(new Vector1by2(-2, 3), new Vector1by2(4, -1)));
    MiniTest.checkEquals("dot of a vector with itself [3,4].[3,4] = 25",
        25.0, Vector1by2.dot(new Vector1by2(3, 4), new Vector1by2(3, 4)));

    Vector1by2 a = new Vector1by2(1.5, -2);
    Vector1by2 b = new Vector1by2(7, 3);
    MiniTest.checkEquals("dot is commutative",
        Vector1by2.dot(a, b), Vector1by2.dot(b, a));
  }

  private static void testApproxEquals()
  {
    MiniTest.check("equal vectors are approxEquals",
        Vector1by2.approxEquals(new Vector1by2(1, 2), new Vector1by2(1, 2)));
    MiniTest.check("tiny rounding error still equal",
        Vector1by2.approxEquals(new Vector1by2(1, 2),
                                new Vector1by2(1 + 1e-12, 2 - 1e-12)));
    MiniTest.check("different row is not equal",
        !Vector1by2.approxEquals(new Vector1by2(1, 2), new Vector1by2(1.1, 2)));
    MiniTest.check("different col is not equal",
        !Vector1by2.approxEquals(new Vector1by2(1, 2), new Vector1by2(1, 2.1)));
  }

  private static void testRounding()
  {
    Vector1by2 v = new Vector1by2(2.6, -1.4);
    MiniTest.checkEquals("roundedRow of 2.6 is 3", 3, v.roundedRow());
    MiniTest.checkEquals("roundedCol of -1.4 is -1", -1, v.roundedCol());
  }

  private static void testToString()
  {
    MiniTest.check("toString shows both components",
        new Vector1by2(1, 2).toString().equals("[1.0, 2.0]"));
  }

  private static void testImmutable()
  {
    Vector1by2 a = new Vector1by2(1, 2);
    Vector1by2.add(a, new Vector1by2(10, 10));
    Vector1by2.scale(a, 5);
    MiniTest.check("static functions do not change their arguments",
        Vector1by2.approxEquals(a, new Vector1by2(1, 2)));
  }
}
