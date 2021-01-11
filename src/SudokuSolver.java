import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * @author Srikar Mangalapalli
 * <p>
 * This class is a sudoku solver that uses recursive backtracking to generate the solution
 * <p>
 * The tests for this program came from https://www.websudoku.com/
 * - The first line in the TestCases file is the number of test cases.
 * - Every test case is 2 lines
 * - The first line is the actual solution to the puzzle
 * - The second line is a mask to determine which numbers to show for the puzzle
 * - O keeps the number and 1 removes number from puzzle
 * <p>
 * You can also input a puzzle with the solve inputPuzzle method, typing in 0's for empty squares.
 * The program will take the 81 digit puzzle and solve puzzle.
 */
public class SudokuSolver
{
    public static void main(String[] args) throws FileNotFoundException
    {
        //solveInputPuzzle();
        solveMaskedSolutionPuzzle();
    }

    private static void solveInputPuzzle()
    {
        Scanner input = new Scanner(System.in);
        int[][] puzzle = new int[9][9];
        String[] rows = input.nextLine().split("(?<=\\G\\d{9})");
        int i = 0;
        for (String row : rows)
            puzzle[i++] = row.chars().map(c -> Integer.parseInt((char) c + "")).toArray();
        if (!checkIfValidPuzzle(puzzle))
            System.out.println("Invalid Puzzle");
        else if (solveSudoku(new HashSet<>(), puzzle, 0))
        {
            System.out.println("Sudoku Puzzle Solved!!");
//                printSudoku(puzzle);
        }
        else
            System.out.println("Sudoku Puzzle is UNSOLVEABLE");
    }

    public static void solveMaskedSolutionPuzzle() throws FileNotFoundException
    {
        Scanner input = new Scanner(new File("TestCases.txt"));
        int puzzles = input.nextInt();
        input.nextLine();

        for (int i = 0; i < puzzles; i++)
        {
            String[] rows = input.nextLine().split("(?<=\\G\\d{9})");
            int[] mask = input.nextLine().chars().map(c -> Integer.parseInt((char) c + "")).toArray();
            int k = 0;
            int[][] puzzle = new int[9][9];
            for (String row : rows)
                puzzle[k++] = row.chars().map(c -> Integer.parseInt((char) c + "")).toArray();

            if (!checkIfValidPuzzle(puzzle))
                System.out.println("INVALID PUZZLE!!!");
            else
            {
                k = 0;
                final int[] j = {0};
                for (String row : rows)
                    puzzle[k++] = row.chars().map(c -> mask[j[0]++] == 0 ? Integer.parseInt((char) c + "") : 0).toArray();

                System.out.println("Sudoku Puzzle #" + (i + 1));
                printSudoku(puzzle);
                if (solveSudoku(new HashSet<>(), puzzle, 0))
                {
                    System.out.println("Sudoku Puzzle #" + (i + 1) + " Solved!!");
//                    printSudoku(puzzle);
                }
                else
                    System.out.println("Sudoku Puzzle #" + (i + 1) + " is UNSOLVEABLE!!\n");

            }
        }
    }

    /**
     * Recursive Backtracking method for this sudoku solver. Returns true if puzzle was solved,
     * false otherwise
     *
     * @param fixedIndices HashSet for storing the indexes that we should not change the values of
     * @param puzzle       2D array representing the current state of the sudoku puzzle
     * @param square       The index of the puzzle currently being modified
     * @return true if puzzle was solved, false otherwise
     */
    public static boolean solveSudoku(HashSet<Integer> fixedIndices, int[][] puzzle, int square)
    {
        // We finished assigning all squares in the sudoku puzzle so our puzzle is solved!
        if (square == 81)
            return true;
            // We don't want to change the value of a fixed square, so we skip to the next index
        else if (fixedIndices.contains(square))
            return solveSudoku(fixedIndices, puzzle, square + 1);

        int row = square / 9;
        int col = square % 9;
        // if value is already assigned then this is a fixed square, so we add this index to our
        // HashSet and move to the next index
        if (puzzle[row][col] != 0)
        {
            fixedIndices.add(square);
            return solveSudoku(fixedIndices, puzzle, square + 1);
        }
        else
        {
            // Get a list of all nums we can assign this square
            HashSet<Integer> availableNums = getAvailableNums(puzzle, row, col);
            for (int num : availableNums)
            {
                // assign the square an available num
                puzzle[row][col] = num;

                // if puzzle can be solved with this num then we return true
                if (solveSudoku(fixedIndices, puzzle, square + 1))
                    return true;
            }
        }

        // We failed to assign this square a value so we set it back to 0 and return false
        puzzle[row][col] = 0;
        return false;
    }

    /**
     * Gets all the available numbers that can be validly placed on a square in the sudoku puzzle.
     * Returns a HashSet of all the valid numbers that are able to be placed at the row and column index.
     *
     * @param puzzle 2D array representing the current state of the sudoku puzzle
     * @param row    The row index to check in the puzzle
     * @param col    The column index to check in the puzzle
     * @return a HashSet of all the valid numbers that are able to be placed at the
     * row and column index
     */
    public static HashSet<Integer> getAvailableNums(int[][] puzzle, int row, int col)
    {
        HashSet<Integer> availableNums = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // remove all the nums in the row and col of the square
        for (int i = 0; i < puzzle.length; i++)
            for (int j = 0; j < puzzle[0].length; j++)
                if ((i == row && puzzle[i][j] != 0) || (j == col && puzzle[i][j] != 0))
                    availableNums.remove(puzzle[i][j]);

        // remove all the nums in the box that this square is located in
        for (int i = (row / 3) * 3; i < ((row / 3) + 1) * 3; i++)
            for (int j = (col / 3) * 3; j < ((col / 3) + 1) * 3; j++)
                if (puzzle[i][j] != 0)
                    availableNums.remove(puzzle[i][j]);

        return availableNums;
    }

    /**
     * Checks if the given sudoku puzzle in its current state is a valid one.
     * Returns true is puzzle is valid, false otherwise.
     *
     * @param puzzle 2D array representing the current state of the sudoku puzzle
     * @return true if puzzle is valid, false otherwise
     */
    public static boolean checkIfValidPuzzle(int[][] puzzle)
    {
        // Checks if all the rows in the puzzle are valid
        for (int[] row : puzzle)
        {
            TreeSet<Integer> rowNums = new TreeSet<>();
            for (int num : row)
                if (num != 0)
                    if (rowNums.contains(num))
                        return false;
                    else
                        rowNums.add(num);
        }

        // checks if all the columns in the puzzle are valid
        int[][] transposedPuzzle = transpose(puzzle);
        for (int[] col : transposedPuzzle)
        {
            TreeSet<Integer> colNums = new TreeSet<>();
            for (int num : col)
                if (num != 0)
                    if (colNums.contains(num))
                        return false;
                    else
                        colNums.add(num);
        }

        // checks if all the boxes in the puzzle are valid
        for (int row = 0; row < 3; row += 3)
            for (int col = 0; col < 3; col += 3)
            {
                TreeSet<Integer> boxNums = new TreeSet<>();
                for (int i = (row / 3) * 3; i < ((row / 3) + 1) * 3; i++)
                    for (int j = (col / 3) * 3; j < ((col / 3) + 1) * 3; j++)
                        if (puzzle[i][j] != 0)
                            if (boxNums.contains(puzzle[i][j]))
                                return false;
                            else
                                boxNums.add(puzzle[i][j]);
            }

        return true;
    }

    /**
     * Transposes the 2D array representing the sudoku puzzle. Returns the transposed sudoku puzzle.
     *
     * @param puzzle 2D array representing the current state of the sudoku puzzle
     * @return the transposed sudoku puzzle
     */
    public static int[][] transpose(int[][] puzzle)
    {
        int[][] transpose = new int[puzzle.length][puzzle[0].length];
        for (int i = 0; i < transpose.length; i++)
            for (int j = 0; j < transpose[0].length; j++)
                transpose[i][j] = puzzle[j][i];

        return transpose;
    }

    /**
     * Prints the sudoku puzzle.
     *
     * @param puzzle 2D array representing the current state of the sudoku puzzle
     */
    public static void printSudoku(int[][] puzzle)
    {
        for (int i = 0; i < puzzle.length; i++)
        {
            if (i % 3 == 0)
                System.out.println("--------+-------+--------");
            for (int j = 0; j < puzzle[0].length; j++)
            {
                if (j % 3 == 0)
                    System.out.print("| ");
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.print("|\n");
        }
        System.out.println("--------+-------+--------");
        System.out.println();
    }
}
