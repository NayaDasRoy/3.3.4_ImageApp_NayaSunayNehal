/**
 * Matrix2by2: an immutable 2x2 matrix used for 2D transformations.
 *
 *     | m00  m01 |
 *     | m10  m11 |
 *
 * CONVENTION: positions are ROW vectors [row, col], and a vector is
 * transformed by multiplying it on the LEFT of the matrix:
 *
 *     [row', col'] = [row, col] * M
 *                  = [row*m00 + col*m10,  row*m01 + col*m11]
 *
 * Each result component is the dot product of the vector with one COLUMN
 * of the matrix, so multiplication here is built from Vector1by2.dot.
 *
 * SCREEN NOTE: in an image the row number grows DOWNWARD, so the usual
 * "counterclockwise" math rotation looks clockwise on screen. The rotation
 * matrices below are labeled by how the rotation looks on the SCREEN.
 */
public class Matrix2by2
{
  /** The identity matrix: leaves every vector unchanged. */
  public static final Matrix2by2 IDENTITY = new Matrix2by2(1, 0, 0, 1);

  /** Rotates 90 degrees clockwise on screen: (row, col) -> (col, -row). */
  public static final Matrix2by2 ROTATE_90_CW = new Matrix2by2(0, -1, 1, 0);

  /** Rotates 180 degrees (a flip): (row, col) -> (-row, -col). */
  public static final Matrix2by2 ROTATE_180 = new Matrix2by2(-1, 0, 0, -1);

  /** Rotates 90 degrees counterclockwise on screen: (row, col) -> (-col, row). */
  public static final Matrix2by2 ROTATE_90_CCW = new Matrix2by2(0, 1, -1, 0);

  private final double m00;
  private final double m01;
  private final double m10;
  private final double m11;

  /**
   * Creates a matrix from its four entries, listed row by row.
   *
   * @param m00 top-left entry
   * @param m01 top-right entry
   * @param m10 bottom-left entry
   * @param m11 bottom-right entry
   */
  public Matrix2by2(double m00, double m01, double m10, double m11)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m10 = m10;
    this.m11 = m11;
  }

  /**
   * Gets one row of the matrix as a vector.
   *
   * @param i the row number, 0 (top) or 1 (bottom)
   * @return the row as a Vector1by2
   * @throws IllegalArgumentException if i is not 0 or 1
   */
  public Vector1by2 getRow(int i)
  {
    if (i == 0)
    {
      return new Vector1by2(m00, m01);
    }
    if (i == 1)
    {
      return new Vector1by2(m10, m11);
    }
    throw new IllegalArgumentException("row must be 0 or 1, but was " + i);
  }

  /**
   * Gets one column of the matrix, stored in a Vector1by2 so that it can be
   * used with the dot product.
   *
   * @param j the column number, 0 (left) or 1 (right)
   * @return the column as a Vector1by2
   * @throws IllegalArgumentException if j is not 0 or 1
   */
  public Vector1by2 getColumn(int j)
  {
    if (j == 0)
    {
      return new Vector1by2(m00, m10);
    }
    if (j == 1)
    {
      return new Vector1by2(m01, m11);
    }
    throw new IllegalArgumentException("column must be 0 or 1, but was " + j);
  }

  /**
   * Gets a single entry of the matrix.
   *
   * @param i the row number (0 or 1)
   * @param j the column number (0 or 1)
   * @return the entry at row i, column j
   */
  public double get(int i, int j)
  {
    Vector1by2 r = getRow(i);
    return (j == 0) ? r.getRow() : r.getCol();
  }

  /**
   * Builds the matrix that rotates by the given angle clockwise on screen.
   * Use a negative angle for counterclockwise. Tiny floating point noise
   * (like 6e-17 for cos 90) is cleaned up to exactly 0.
   *
   * @param degrees the angle in degrees
   * @return the rotation matrix [[cos, -sin], [sin, cos]]
   */
  public static Matrix2by2 rotation(double degrees)
  {
    double radians = Math.toRadians(degrees);
    double cos = clean(Math.cos(radians));
    double sin = clean(Math.sin(radians));
    return new Matrix2by2(cos, -sin, sin, cos);
  }

  /**
   * Multiplies two matrices. Entry (i, j) of the product is the dot product
   * of row i of the left matrix with column j of the right matrix.
   * Matrix multiplication is NOT commutative: multiply(a, b) is usually
   * different from multiply(b, a).
   *
   * @param a the left matrix
   * @param b the right matrix
   * @return the product a * b
   */
  public static Matrix2by2 multiply(Matrix2by2 a, Matrix2by2 b)
  {
    return new Matrix2by2(
        Vector1by2.dot(a.getRow(0), b.getColumn(0)),
        Vector1by2.dot(a.getRow(0), b.getColumn(1)),
        Vector1by2.dot(a.getRow(1), b.getColumn(0)),
        Vector1by2.dot(a.getRow(1), b.getColumn(1)));
  }

  /**
   * Multiplies a row vector by a matrix (the vector goes on the LEFT).
   * Each component of the result is the dot product of the vector with one
   * column of the matrix.
   *
   * @param v the row vector [row, col]
   * @param m the matrix
   * @return the transformed vector v * m
   */
  public static Vector1by2 multiply(Vector1by2 v, Matrix2by2 m)
  {
    return new Vector1by2(
        Vector1by2.dot(v, m.getColumn(0)),
        Vector1by2.dot(v, m.getColumn(1)));
  }

  /**
   * Adds two matrices entry by entry.
   *
   * @param a the first matrix
   * @param b the second matrix
   * @return the sum a + b
   */
  public static Matrix2by2 add(Matrix2by2 a, Matrix2by2 b)
  {
    return new Matrix2by2(a.m00 + b.m00, a.m01 + b.m01,
                          a.m10 + b.m10, a.m11 + b.m11);
  }

  /**
   * Multiplies every entry of a matrix by a single number.
   *
   * @param m      the matrix to scale
   * @param scalar the number to multiply by
   * @return the scaled matrix
   */
  public static Matrix2by2 scale(Matrix2by2 m, double scalar)
  {
    return new Matrix2by2(scalar * m.m00, scalar * m.m01,
                          scalar * m.m10, scalar * m.m11);
  }

  /**
   * Flips a matrix over its diagonal (rows become columns). For a rotation
   * matrix the transpose is the inverse, so it undoes the rotation.
   *
   * @param m the matrix to transpose
   * @return the transpose of m
   */
  public static Matrix2by2 transpose(Matrix2by2 m)
  {
    return new Matrix2by2(m.m00, m.m10, m.m01, m.m11);
  }

  /**
   * Computes the determinant m00*m11 - m01*m10. A rotation matrix always
   * has determinant 1 (it does not stretch or mirror the image).
   *
   * @param m the matrix
   * @return the determinant
   */
  public static double determinant(Matrix2by2 m)
  {
    return m.m00 * m.m11 - m.m01 * m.m10;
  }

  /**
   * Tests whether two matrices are equal, allowing for tiny floating point
   * rounding error.
   *
   * @param a the first matrix
   * @param b the second matrix
   * @return true if every pair of entries differs by less than
   *         Vector1by2.EPSILON
   */
  public static boolean approxEquals(Matrix2by2 a, Matrix2by2 b)
  {
    return Vector1by2.approxEquals(a.getRow(0), b.getRow(0))
        && Vector1by2.approxEquals(a.getRow(1), b.getRow(1));
  }

  /**
   * Snaps values that are within rounding error of 0, 1 or -1 to exactly
   * that value.
   */
  private static double clean(double value)
  {
    if (Math.abs(value) < 1e-12)
    {
      return 0;
    }
    if (Math.abs(value - 1) < 1e-12)
    {
      return 1;
    }
    if (Math.abs(value + 1) < 1e-12)
    {
      return -1;
    }
    return value;
  }

  /**
   * @return a printable form such as "[[0.0, -1.0], [1.0, 0.0]]"
   */
  @Override
  public String toString()
  {
    return "[" + getRow(0) + ", " + getRow(1) + "]";
  }
}
