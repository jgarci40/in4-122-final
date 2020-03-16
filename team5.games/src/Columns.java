import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Columns extends Game {
    private boolean changed;
    private boolean isMatched;
    private boolean frozen;
    private ColumnsPiece faller;
    private ColumnsGUI GUI;

    public Columns()
    {
        super(13, 6);
        changed = false;
        isMatched = false;
        frozen = false;
        faller = null;
        GUI = new ColumnsGUI();
        initializeBoard();
    }

    @Override
    public void initializeBoard()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                board[i][j] = EMPTY;
            }
        }
    }

    @Override
    public void checkMatch()
    {
        handleHorizontalMatch();
        handleRightDiagonalMatch();
        handleVerticalMatch();
        handleLeftDiagonalMatch();
    }

    @Override
    public void run()
    {
        GUI.runGUI();
    }

    @Override
    public void printBoard()
    {
        int num_spaces = 0;
        String dash = "-";
        for (int i = 0; i < rows; i++)
        {
            System.out.print("|");
            for (int j = 0; j < cols; j++)
            {
                num_spaces = 3 * cols;
                if (!hasMatch())
                {
                    printUnmatchedCells(i, j);
                }
                else if (hasMatch())
                {
                    printMatchedCells(i, j);
                }
            }
            System.out.println("|");
        }
        dash = dash.repeat(num_spaces);
        System.out.println(" " + dash + " ");
    }

    public boolean isFrozen()
    {
        return frozen;
    }

    public void initializeFaller(int col)
    {
        frozen = false;
        faller = new ColumnsPiece(col-1);
        board[0][col-1] = faller.getBottomColor();
    }

    public void dropFaller()
    {
        if (fallerExists())
        {
            if (isFalling())
            {
                faller.row++;
                board[faller.row][faller.col] = faller.getBottomColor();
                board[faller.row-1][faller.col] = faller.getMiddleColor();
                if (isValidRow(faller.row-2))
                {
                    board[faller.row-2][faller.col] = faller.getTopColor();
                }
                if (isValidRow(faller.row-3))
                {
                    board[faller.row-3][faller.col] = EMPTY;
                }
            }
            else if (hasLanded())
            {
                freezeFaller();
            }
        }
    }

    public void rotateFaller()
    {
        if (fallerExists())
        {
            if (isValidRow(faller.row-2))
            {
                faller.rotate();
                int tempColor = board[faller.row][faller.col];
                board[faller.row][faller.col] = board[faller.row-1][faller.col];
                board[faller.row-1][faller.col] = board[faller.row-2][faller.col];
                board[faller.row-2][faller.col] = tempColor;
            }
        }
    }

    public void moveFallerLeft()
    {
        if (fallerExists())
        {
            if (isValidRow(faller.row-2))
            {
                if (isValidCol(faller.col - 1)) {
                    if (board[faller.row][faller.col - 1] == EMPTY) {
                        board[faller.row][faller.col - 1] = faller.getBottomColor();
                        emptyMovedCell(faller.col, faller.getBottomColor());
                        board[faller.row - 1][faller.col - 1] = faller.getMiddleColor();
                        emptyMovedCell(faller.col, faller.getMiddleColor());
                        board[faller.row - 2][faller.col - 1] = faller.getTopColor();
                        emptyMovedCell(faller.col, faller.getTopColor());
                        faller.col--;
                        if (hasLanded()) {
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    public void moveFallerRight()
    {
        if (fallerExists())
        {
            if (isValidRow(faller.row-2))
            {
                if (isValidCol(faller.col + 1)) {
                    if (board[faller.row][faller.col + 1] == EMPTY) {
                        board[faller.row][faller.col + 1] = faller.getBottomColor();
                        emptyMovedCell(faller.col, faller.getBottomColor());
                        board[faller.row - 1][faller.col + 1] = faller.getMiddleColor();
                        emptyMovedCell(faller.col, faller.getMiddleColor());
                        board[faller.row - 2][faller.col + 1] = faller.getTopColor();
                        emptyMovedCell(faller.col, faller.getTopColor());
                        faller.col++;
                        if (hasLanded()) {
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    public void deleteMatched()
    {
        if (hasMatch())
        {
            score += (10 * matched.size());
            for (ArrayList<Integer> match: matched)
            {
                int mRow = match.get(0);
                int mCol = match.get(1);
                for (int i = 0; i < rows; i++)
                {
                    for (int j = 0; j < cols; j++)
                    {
                        if (i == mRow && j == mCol)
                        {
                            board[i][j] = EMPTY;
                        }
                        movePiecesDown();
                        freezeFaller();
                        isMatched = false;
                    }
                }
            }
        }
        System.out.print("MATCHED: ");
        for (int i = 0; i < matched.size(); i++)
        {
            System.out.print(matched.get(i).get(0) + "," + matched.get(i).get(1) + " | ");
        }
        System.out.println();

        matched.clear();
    }

    public boolean isGameOver()
    {
        if (fallerExists() && hasLanded())
        {
            if (isValidRow(faller.row+1))
            {
                return (board[faller.row+1][faller.col] != EMPTY
                        && !isValidRow(faller.row-2));
            }
        }
        return false;
    }

    public void handleGameOver()
    {
        if (isGameOver())
        {
            freezeFaller();
        }
    }

    private int matchFound(int row, int col, int rowdelta, int coldelta)
    {
        int start_cell = board[row][col];
        int counter = 0;
        if (start_cell == EMPTY)
        {
            return counter;
        }
        else
        {
            while (true)
            {
                if (!isValidRow(row + rowdelta * counter) ||
                        !isValidCol(col + coldelta * counter) ||
                        board[row + rowdelta * counter][col + coldelta * counter] != start_cell ||
                        isFalling())
                {
                    break;
                }
                else
                {
                    counter += 1;
                }
            }
        }
        return counter;
    }

    private void handleHorizontalMatch()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                int tempRow = i;
                int tempCol = j;
                if (matchFound(i, j, 0, 1) >= 3)
                {
                    for (int k = 0; k < matchFound(i, j , 0, 1); k++)
                    {
                        ArrayList<Integer> matchPair = new ArrayList<Integer>();
                        matchPair.add(tempRow);
                        matchPair.add(tempCol);
                        matched.add(matchPair);
                        tempCol++;
                    }
                    tempRow = i;
                    tempCol = j;
                    isMatched = true;
                    return;
                }
            }
        }
    }

    private void handleRightDiagonalMatch()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                int tempRow = i;
                int tempCol = j;
                if (matchFound(i, j, 1, 1) >= 3)
                {
                    for (int k = 0; k < matchFound(i, j , 1, 1); k++)
                    {
                        ArrayList<Integer> matchPair = new ArrayList<Integer>();
                        matchPair.add(tempRow);
                        matchPair.add(tempCol);
                        matched.add(matchPair);
                        tempRow++;
                        tempCol++;
                    }
                    tempRow = i;
                    tempCol = j;
                    isMatched = true;
                    return;
                }
            }
        }
    }

    private void handleVerticalMatch()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                int tempRow = i;
                int tempCol = j;
                if (matchFound(i, j, 1, 0) >= 3)
                {
                    for (int k = 0; k < matchFound(i, j , 1, 0); k++)
                    {
                        ArrayList<Integer> matchPair = new ArrayList<Integer>();
                        matchPair.add(tempRow);
                        matchPair.add(tempCol);
                        matched.add(matchPair);
                        tempRow++;
                    }
                    tempRow = i;
                    tempCol = j;
                    isMatched = true;
                    return;
                }
            }
        }
    }

    private void handleLeftDiagonalMatch()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                int tempRow = i;
                int tempCol = j;
                if (matchFound(i, j, 1, -1) >= 3)
                {
                    for (int k = 0; k < matchFound(i, j , 1, -1); k++)
                    {
                        ArrayList<Integer> matchPair = new ArrayList<Integer>();
                        matchPair.add(tempRow);
                        matchPair.add(tempCol);
                        matched.add(matchPair);
                        tempRow++;
                        tempCol--;
                    }
                    tempRow = i;
                    tempCol = j;
                    isMatched = true;
                    return;
                }
            }
        }
    }

    private void movePiecesDown()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (isValidRow(i+1))
                {
                    if (board[i+1][j] == EMPTY)
                    {
                        board[i+1][j] = board[i][j];
                        board[i][j] = EMPTY;

                        if (isValidRow(i-1))
                        {
                            if (board[i-1][j] != EMPTY)
                            {
                                board[i][j] = board[i-1][j];
                                board[i-1][j] = EMPTY;
                            }
                        }
                    }
                }
            }
        }
    }

    private void freezeFaller()
    {
        if (fallerExists())
        {
            if (hasLanded() && !hasChanged())
            {
                addFallerToBoard();
                faller = null;
                frozen = true;
            }
            if (hasLanded() && hasChanged())
            {
                addFallerToBoard();
                faller = null;
                frozen = true;
            }
        }
    }


    private void addFallerToBoard()
    {
        if (isValidRow(faller.row))
        {
            board[faller.row][faller.col] = faller.getBottomColor();
        }
        if (isValidRow(faller.row-1))
        {
            board[faller.row-1][faller.col] = faller.getMiddleColor();
        }
        if (isValidRow(faller.row-2))
        {
            board[faller.row-2][faller.col] = faller.getTopColor();
        }
    }

    private void emptyMovedCell(int col, int color)
    {
        if (board[faller.row][col] == color)
        {
            board[faller.row][col] = EMPTY;
            board[faller.row-1][col] = EMPTY;
            board[faller.row-2][col] = EMPTY;
        }
    }

    private boolean fallerExists()
    {
        return (faller != null);
    }

    private boolean isFalling()
    {
        if (fallerExists())
        {
            if (isValidRow(faller.row+1))
            {
                return (board[rows-1][faller.col] == EMPTY ||
                        board[faller.row+1][faller.col] == EMPTY);
            }
        }
        return false;
    }

    private boolean hasChanged()
    {
        return changed;
    }

    private boolean hasMatch()
    {
        return isMatched;
    }

    private boolean hasLanded()
    {
        if (!hasMatch())
        {
            return (!isFalling());
        }
        return false;
    }

    private boolean isValidRow(int row)
    {
        return (row >= 0 && row < rows);
    }

    private boolean isValidCol(int col)
    {
        return (col >= 0 && col < cols);
    }

    //UI METHODS:

    private void printUnmatchedCells(int row, int col)
    {
        String chars = "   ";
        if (board[row][col] == EMPTY)
        {
            System.out.print(chars);
        }
        else if (board[row][col] != EMPTY)
        {
            int cell = board[row][col];
            if (fallerExists())
            {
                if (col == faller.col && row <= faller.row)
                {
                    if (isFalling())
                    {
                        chars = "[]";
                    }
                    else if (hasLanded())
                    {
                        chars = "||";
                    }
                }
            }
            System.out.print(chars.charAt(0) + Integer.toString(cell) + chars.charAt(1));
        }
    }

    private void printMatchedCells(int row, int col)
    {
        if (board[row][col] == getMatchedCell())
        {
            int cell = board[row][col];
            System.out.print('*' + Integer.toString(cell) + '*');
        }
        else
        {
            printUnmatchedCells(row, col);
        }
    }

    private int getMatchedCell()
    {
        int cell = 0;
        if (hasMatch())
        {
            for (ArrayList<Integer> match: matched)
            {
                cell = board[match.get(0)][match.get(1)];
            }
        }
        return cell;
    }

    private void playConsoleGame()
    {
        Scanner input = new Scanner(System.in);
        System.out.println("WELCOME TO COLUMNS");
        printBoard();
        System.out.println("Press 'F' to create faller,\n'R' to rotate,\n'<' to move left," +
                "\n'>' to move right,\n'ENTER' to drop:");
        while (true)
        {
            String command = input.nextLine();

            if (command.isEmpty())
            {
                if (matched.size() > 0)
                {
                    deleteMatched();
                    printBoard();
                }
                else
                {
                    dropFaller();
                    checkMatch();
                    printBoard();
                }
            }
            else if (!command.isEmpty())
            {
                if (command.equalsIgnoreCase("F")) {
                    Random rand = new Random();
                    int randCol = rand.nextInt(cols) + 1;
                    initializeFaller(randCol);
                    printBoard();
                }
                if (command.equalsIgnoreCase("R"))
                {
                    rotateFaller();
                    printBoard();
                }
                if (command.equalsIgnoreCase("<"))
                {
                    moveFallerLeft();
                    printBoard();
                }
                if (command.equalsIgnoreCase(">"))
                {
                    moveFallerRight();
                    printBoard();
                }
            }

            if (isGameOver())
            {
                handleGameOver();
                printBoard();
                System.out.println("GAME OVER");
                break;
            }
        }
    }
}