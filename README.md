# ImageApp 3.3.4 - Sample Solution

## Files I added to the PLTW starter
| File | What it is |
|---|---|
| `Vector1by2.java` | Immutable row vector `[row, col]`; static `add`, `subtract`, `scale`, `dot`, `approxEquals` |
| `Matrix2by2.java` | Immutable 2x2 matrix; static `multiply(Matrix, Matrix)`, `multiply(Vector1by2, Matrix2by2)`, `rotation(degrees)`, `transpose`, `determinant`; constants `ROTATE_180`, `ROTATE_90_CW`, `ROTATE_90_CCW` |
| `ImageEditor.java` | The 2D array algorithms: recolor x3, matrix rotation, paste with blend + background removal |
| `ImageApp.java` | `main` (the starter file, filled in) with the pseudocode comment block |
| `MiniTest.java`, `Vector1by2Test.java`, `Matrix2by2Test.java`, `ImageEditorTest.java`, `AllTests.java` | Unit tests (80 checks, no JUnit needed) |
| `sample_output/` | Screenshots produced by the program |

## Run
* **Image program:** run `ImageApp.java` (opens exactly seven windows: 3 recolored, 3 rotated, 1 combined, stacked in the order created).
  To save PNGs instead of opening windows: `java -DsaveDir=screenshots ImageApp`
* **Unit tests:** run `AllTests.java` -> expect `Passed: 80   Failed: 0`

## The one idea behind the rotations
Positions are row vectors, and `newPosition = [row, col] * M`.

| Rotation | Matrix M | Effect |
|---|---|---|
| 180 | `[[-1, 0], [0, -1]]` | (row, col) -> (-row, -col) |
| 90 clockwise | `[[0, -1], [1, 0]]` | (row, col) -> (col, -row) |
| 90 counterclockwise | `[[0, 1], [-1, 0]]` | (row, col) -> (-col, row) |

Rotated pixels can land on negative numbers, so `ImageEditor.transform`
sends the four corners through the matrix first, finds the new bounding box,
and slides everything so the smallest row and col become 0.
