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
 *
 *
 */
public class SudokuSolver
{
    public static void main(String[] args) throws FileNotFoundException
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

//                System.out.println("Sudoku Puzzle #" + (i + 1));
//                printSudoku(puzzle);
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

    public static boolean solveSudoku(HashSet<Integer> fixedIndices, int[][] puzzle, int index)
    {
        if (index == 81)
            return true;
        else if (fixedIndices.contains(index))
            return solveSudoku(fixedIndices, puzzle, index + 1);

        int row = index / 9;
        int col = index % 9;
        if (puzzle[row][col] != 0)
        {
            fixedIndices.add(index);
            return solveSudoku(fixedIndices, puzzle, index + 1);
        }
        else
        {
            HashSet<Integer> availableNums = getAvailableNums(puzzle, row, col);
            for (int num : availableNums)
            {
                puzzle[row][col] = num;
                if (solveSudoku(fixedIndices, puzzle, index + 1))
                    return true;
            }
        }

        puzzle[row][col] = 0;
        return false;
    }

    public static HashSet<Integer> getAvailableNums(int[][] puzzle, int row, int col)
    {
        HashSet<Integer> availableNums = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int i = 0; i < puzzle.length; i++)
            for (int j = 0; j < puzzle[0].length; j++)
                if ((i == row && puzzle[i][j] != 0) || (j == col && puzzle[i][j] != 0))
                    availableNums.remove(puzzle[i][j]);

        for (int i = (row / 3) * 3; i < ((row / 3) + 1) * 3; i++)
            for (int j = (col / 3) * 3; j < ((col / 3) + 1) * 3; j++)
                if (puzzle[i][j] != 0)
                    availableNums.remove(puzzle[i][j]);

        return availableNums;
    }

    public static boolean checkIfValidPuzzle(int[][] puzzle)
    {
        TreeSet<Integer> nums = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // Checks if all the rows in the puzzle are valid
        for (int[] row : puzzle)
        {
            TreeSet<Integer> rowNums = new TreeSet<>();
            for (int num : row)
                if (num != 0)
                    rowNums.add(num);

            if (!rowNums.equals(nums))
                return false;
        }

        // checks if all the columns in the puzzle are valid
        int[][] transposedPuzzle = transpose(puzzle);
        for (int[] col : transposedPuzzle)
        {
            TreeSet<Integer> colNums = new TreeSet<>();
            for (int num : col)
                if (num != 0)
                    colNums.add(num);

            if (!colNums.equals(nums))
                return false;
        }

        for (int row = 0; row < 3; row += 3)
            for (int col = 0; col < 3; col += 3)
            {
                TreeSet<Integer> boxNums = new TreeSet<>();
                for (int i = (row / 3) * 3; i < ((row / 3) + 1) * 3; i++)
                    for (int j = (col / 3) * 3; j < ((col / 3) + 1) * 3; j++)
                        if (puzzle[i][j] != 0)
                            boxNums.add(puzzle[i][j]);

                if (!boxNums.equals(nums))
                    return false;
            }

        return true;
    }

    public static int[][] transpose(int[][] puzzle)
    {
        int[][] transpose = new int[puzzle.length][puzzle[0].length];
        for (int i = 0; i < transpose.length; i++)
            for (int j = 0; j < transpose[0].length; j++)
                transpose[i][j] = puzzle[j][i];

        return transpose;
    }

    public static void printSudoku(int[][] puzzle)
    {
        for (int[] row : puzzle)
            System.out.println(Arrays.toString(row));
        System.out.println();
    }
}
