/**
 * Vector1by2: an immutable 1x2 ROW vector [row, col].
 *
 * In ImageApp, every pixel position is a (row, col) pair, so a Vector1by2 is
 * the natural way to hold a pixel position (or a pixel offset from some
 * origin) so that it can be transformed by a Matrix2by2.
 *
 * All of the vector math is exposed as static functions, for example:
 *
 *     Vector1by2 sum = Vector1by2.add(a, b);
 *     double d       = Vector1by2.dot(a, b);
 *
 * Because the class is immutable, no function ever changes its arguments;
 * each one returns a brand new Vector1by2 (or a double for dot).
 */
public class Vector1by2
{
  /** Tolerance used when comparing doubles that came from floating point math. */
  public static final double EPSILON = 1e-9;

  private final double row;
  private final double col;

  /**
   * Creates the row vector [row, col].
   *
   * @param row the first component (the image row, which grows downward)
   * @param col the second component (the image column, which grows rightward)
   */
  public Vector1by2(double row, double col)
  {
    this.row = row;
    this.col = col;
  }

  /**
   * @return the first component (row)
   */
  public double getRow()
  {
    return row;
  }

  /**
   * @return the second component (col)
   */
  public double getCol()
  {
    return col;
  }

  /**
   * Adds two vectors component by component.
   *
   * @param a the first vector
   * @param b the second vector
   * @return a new vector [a.row + b.row, a.col + b.col]
   */
  public static Vector1by2 add(Vector1by2 a, Vector1by2 b)
  {
    return new Vector1by2(a.row + b.row, a.col + b.col);
  }

  /**
   * Subtracts one vector from another component by component.
   *
   * @param a the vector to subtract from
   * @param b the vector to subtract
   * @return a new vector [a.row - b.row, a.col - b.col]
   */
  public static Vector1by2 subtract(Vector1by2 a, Vector1by2 b)
  {
    return new Vector1by2(a.row - b.row, a.col - b.col);
  }

  /**
   * Multiplies a vector by a single number (a scalar).
   *
   * @param v      the vector to scale
   * @param scalar the number to multiply each component by
   * @return a new vector [scalar * v.row, scalar * v.col]
   */
  public static Vector1by2 scale(Vector1by2 v, double scalar)
  {
    return new Vector1by2(scalar * v.row, scalar * v.col);
  }

  /**
   * Computes the dot product of two vectors:
   * a.row * b.row + a.col * b.col.
   *
   * This is the building block of matrix multiplication. Each entry of a
   * product is the dot product of one row with one column.
   *
   * @param a the first vector
   * @param b the second vector
   * @return the dot product (a single number)
   */
  public static double dot(Vector1by2 a, Vector1by2 b)
  {
    return a.row * b.row + a.col * b.col;
  }

  /**
   * Tests whether two vectors are equal, allowing for tiny floating point
   * rounding error (for example, cos(90 degrees) is 6e-17 rather than 0).
   *
   * @param a the first vector
   * @param b the second vector
   * @return true if both components differ by less than EPSILON
   */
  public static boolean approxEquals(Vector1by2 a, Vector1by2 b)
  {
    return Math.abs(a.row - b.row) < EPSILON
        && Math.abs(a.col - b.col) < EPSILON;
  }

  /**
   * Rounds the row component to the nearest whole number so the vector can
   * be used as an array index.
   *
   * @return the row as an int
   */
  public int roundedRow()
  {
    return (int) Math.round(row);
  }

  /**
   * Rounds the col component to the nearest whole number so the vector can
   * be used as an array index.
   *
   * @return the col as an int
   */
  public int roundedCol()
  {
    return (int) Math.round(col);
  }

  /**
   * @return a printable form such as "[2.0, -3.0]"
   */
  @Override
  public String toString()
  {
    return "[" + row + ", " + col + "]";
  }
}
