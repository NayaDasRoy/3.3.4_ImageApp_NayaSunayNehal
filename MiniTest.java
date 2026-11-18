/**
 * MiniTest: a tiny stand-in for JUnit so the unit tests run in any IDE with
 * no extra libraries. Each check prints PASS or FAIL and keeps a tally.
 */
public class MiniTest
{
  private static int passed = 0;
  private static int failed = 0;

  /**
   * Checks that a condition is true.
   *
   * @param name      a short description of what is being tested
   * @param condition the result that must be true
   */
  public static void check(String name, boolean condition)
  {
    if (condition)
    {
      passed++;
      System.out.println("  PASS  " + name);
    }
    else
    {
      failed++;
      System.out.println("  FAIL  " + name);
    }
  }

  /**
   * Checks that two doubles are equal within a small tolerance.
   *
   * @param name     a short description of what is being tested
   * @param expected the correct value
   * @param actual   the value the code produced
   */
  public static void checkEquals(String name, double expected, double actual)
  {
    check(name + " (expected " + expected + ", got " + actual + ")",
          Math.abs(expected - actual) < Vector1by2.EPSILON);
  }

  /**
   * Checks that two ints are equal.
   *
   * @param name     a short description of what is being tested
   * @param expected the correct value
   * @param actual   the value the code produced
   */
  public static void checkEquals(String name, int expected, int actual)
  {
    check(name + " (expected " + expected + ", got " + actual + ")",
          expected == actual);
  }

  /** Prints the totals for the whole run. */
  public static void summary()
  {
    System.out.println();
    System.out.println("Passed: " + passed + "   Failed: " + failed);
    System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
  }

  /** @return the number of failed checks so far */
  public static int failures()
  {
    return failed;
  }
}
