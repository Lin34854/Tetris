package org.example;

import javafx.scene.paint.Color;

import java.util.Random;

public class GameModel {

    private static final int DEFAULT_BLOCK = 30;

    private int rows = 20;
    private int cols = 10;
    private int blockSize = DEFAULT_BLOCK;

    private int[][] board =
            new int[rows][cols];

    private int[][] currentPiece;
    private int currentPieceType;
    private Tetromino currentTetromino;

    private int pieceRow;
    private int pieceCol;

    private boolean paused;
    private boolean gameOver;

    private long lastDropTime;
    private double smoothOffset;

    private int score;
    private int linesErased;
    private int level = 1;

    private final Random random =
            new Random();

    public void reset(GameConfig config) {

        cols = config.fieldWidth();
        rows = config.fieldHeight();
        level = config.level();
        blockSize = calculateBlockSize(rows);

        board =
                new int[rows][cols];

        paused = false;
        gameOver = false;
        lastDropTime = 0;
        smoothOffset = 0;
        score = 0;
        linesErased = 0;

        spawnPiece();
    }

    public void update(long now) {

        if (paused || gameOver) {
            return;
        }

        if (lastDropTime == 0) {
            lastDropTime = now;
        }

        long elapsed =
                now - lastDropTime;

        if (canMove(
                currentPiece,
                pieceRow + 1,
                pieceCol
        )) {

            smoothOffset =
                    Math.min(
                            1.0,
                            (double) elapsed /
                                    getDropInterval()
                    );

            if (elapsed >= getDropInterval()) {

                pieceRow++;

                currentTetromino.setPosition(
                        pieceCol,
                        pieceRow
                );

                smoothOffset = 0;
                lastDropTime = now;
            }

        } else {

            smoothOffset = 0;

            if (elapsed >= getDropInterval()) {

                lockPiece();

                int cleared =
                        clearFullRows();

                addScore(cleared);
                spawnPiece();

                lastDropTime = now;
            }
        }
    }

    public void togglePause() {

        if (gameOver) {
            return;
        }

        paused = !paused;
        lastDropTime = 0;
    }

    public void moveHorizontal(int direction) {

        if (paused || gameOver) {
            return;
        }

        int newCol =
                pieceCol + direction;

        if (canMove(
                currentPiece,
                pieceRow,
                newCol
        )) {

            pieceCol = newCol;

            currentTetromino.setPosition(
                    pieceCol,
                    pieceRow
            );
        }
    }

    public void manualMoveDown() {

        if (paused || gameOver) {
            return;
        }

        if (canMove(
                currentPiece,
                pieceRow + 1,
                pieceCol
        )) {

            pieceRow++;

            currentTetromino.setPosition(
                    pieceCol,
                    pieceRow
            );

            smoothOffset = 0;
            lastDropTime = 0;

        } else {

            lockPiece();

            int cleared =
                    clearFullRows();

            addScore(cleared);
            spawnPiece();
        }
    }

    public void rotatePiece() {

        if (paused || gameOver) {
            return;
        }

        int pieceRows =
                currentPiece.length;

        int pieceCols =
                currentPiece[0].length;

        int[][] rotated =
                new int[pieceCols][pieceRows];

        for (int r = 0;
             r < pieceRows;
             r++) {

            for (int c = 0;
                 c < pieceCols;
                 c++) {

                rotated[c][pieceRows - 1 - r] =
                        currentPiece[r][c];
            }
        }

        if (canMove(
                rotated,
                pieceRow,
                pieceCol
        )) {

            currentPiece = rotated;

            currentTetromino.setShape(
                    currentPiece
            );
        }
    }

    private void spawnPiece() {

        int type =
                random.nextInt(7);

        currentPieceType =
                type + 1;

        currentPiece =
                switch (type) {

                    case 0 -> new int[][]{
                            {1, 1, 1, 1}
                    };

                    case 1 -> new int[][]{
                            {1, 1},
                            {1, 1}
                    };

                    case 2 -> new int[][]{
                            {0, 1, 0},
                            {1, 1, 1}
                    };

                    case 3 -> new int[][]{
                            {1, 0, 0},
                            {1, 1, 1}
                    };

                    case 4 -> new int[][]{
                            {0, 0, 1},
                            {1, 1, 1}
                    };

                    case 5 -> new int[][]{
                            {0, 1, 1},
                            {1, 1, 0}
                    };

                    default -> new int[][]{
                            {1, 1, 0},
                            {0, 1, 1}
                    };
                };

        pieceRow = 0;

        pieceCol =
                (cols - currentPiece[0].length) / 2;

        currentTetromino =
                new Tetromino(
                        pieceCol,
                        pieceRow,
                        currentPiece,
                        getPieceColor(
                                currentPieceType
                        ),
                        blockSize
                );

        if (!canMove(
                currentPiece,
                pieceRow,
                pieceCol
        )) {
            gameOver = true;
        }
    }

    private boolean canMove(
            int[][] piece,
            int newRow,
            int newCol
    ) {

        for (int r = 0;
             r < piece.length;
             r++) {

            for (int c = 0;
                 c < piece[r].length;
                 c++) {

                if (piece[r][c] == 0) {
                    continue;
                }

                int boardRow =
                        newRow + r;

                int boardCol =
                        newCol + c;

                if (
                        boardRow < 0 ||
                                boardRow >= rows ||
                                boardCol < 0 ||
                                boardCol >= cols
                ) {
                    return false;
                }

                if (board[boardRow][boardCol] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private void lockPiece() {

        for (int r = 0;
             r < currentPiece.length;
             r++) {

            for (int c = 0;
                 c < currentPiece[r].length;
                 c++) {

                if (currentPiece[r][c] == 0) {
                    continue;
                }

                int row =
                        pieceRow + r;

                int col =
                        pieceCol + c;

                if (
                        row >= 0 &&
                                row < rows &&
                                col >= 0 &&
                                col < cols
                ) {
                    board[row][col] =
                            currentPieceType;
                }
            }
        }
    }

    private int clearFullRows() {

        int clearedRows = 0;

        for (int row =
             rows - 1;
             row >= 0;
             row--) {

            boolean full = true;

            for (int col = 0;
                 col < cols;
                 col++) {

                if (board[row][col] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {

                clearedRows++;

                for (int r = row;
                     r > 0;
                     r--) {

                    for (int c = 0;
                         c < cols;
                         c++) {

                        board[r][c] =
                                board[r - 1][c];
                    }
                }

                for (int c = 0;
                     c < cols;
                     c++) {
                    board[0][c] = 0;
                }

                row++;
            }
        }

        linesErased += clearedRows;

        return clearedRows;
    }

    private void addScore(int clearedRows) {

        score +=
                switch (clearedRows) {
                    case 1 -> 100;
                    case 2 -> 300;
                    case 3 -> 600;
                    case 4 -> 1000;
                    default -> 0;
                };
    }

    private int calculateBlockSize(int fieldHeight) {

        return switch (fieldHeight) {
            case 30 -> 22;
            case 24 -> 25;
            default -> DEFAULT_BLOCK;
        };
    }

    private long getDropInterval() {

        return 800_000_000L -
                (level - 1) *
                        70_000_000L;
    }

    public Color getPieceColor(int type) {

        return switch (type) {
            case 1 -> Color.CYAN;
            case 2 -> Color.GOLD;
            case 3 -> Color.MEDIUMPURPLE;
            case 4 -> Color.DODGERBLUE;
            case 5 -> Color.ORANGE;
            case 6 -> Color.LIMEGREEN;
            case 7 -> Color.RED;
            default -> Color.GRAY;
        };
    }

    public int[][] getBoard() {
        return board;
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public double getSmoothOffset() {
        return smoothOffset;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public int getLinesErased() {
        return linesErased;
    }

    public int getLevel() {
        return level;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
