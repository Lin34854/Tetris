package org.example;

import javafx.scene.paint.Color;

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
    private boolean aiControlled;
    private boolean aiMoveComplete;

    private long lastDropTime;
    private double smoothOffset;

    private int score;
    private int linesErased;
    private int level = 1;
    private int pieceIndex;

    private PieceSequence pieceSequence =
            new PieceSequence();

    public void reset(GameConfig config) {
        reset(config, new PieceSequence(), false);
    }

    public void reset(
            GameConfig config,
            PieceSequence sequence,
            boolean aiControlled
    ) {
        cols = config.fieldWidth();
        rows = config.fieldHeight();
        level = config.level();
        blockSize = calculateBlockSize(rows);

        board =
                new int[rows][cols];

        paused = false;
        gameOver = false;
        this.aiControlled = aiControlled;
        aiMoveComplete = false;
        lastDropTime = 0;
        smoothOffset = 0;
        score = 0;
        linesErased = 0;
        pieceIndex = 0;
        pieceSequence = sequence;

        spawnPiece();
    }

    public void update(long now) {

        if (paused || gameOver) {
            return;
        }

        if (aiControlled) {
            aiPlay();
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

    public void setPaused(boolean paused) {
        if (gameOver) {
            return;
        }

        this.paused = paused;
        lastDropTime = 0;
    }

    public void moveHorizontal(int direction) {

        if (paused || gameOver || aiControlled) {
            return;
        }

        moveHorizontalInternal(direction);
    }

    private void moveHorizontalInternal(int direction) {

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

        if (paused || gameOver || aiControlled) {
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

        if (paused || gameOver || aiControlled) {
            return;
        }

        int[][] rotated =
                rotateMatrix(currentPiece);

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

        aiMoveComplete = false;

        int type =
                pieceSequence.getPieceType(
                        pieceIndex++
                );

        currentPieceType =
                type + 1;

        currentPiece =
                createPiece(type);

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

        smoothOffset = 0;
        lastDropTime = 0;

        if (!canMove(
                currentPiece,
                pieceRow,
                pieceCol
        )) {
            gameOver = true;
        }
    }

    private int[][] createPiece(int type) {
        return switch (type) {
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

        return Math.max(
                100_000_000L,
                800_000_000L -
                        (level - 1) *
                                70_000_000L
        );
    }

    private int[][] rotateMatrix(int[][] piece) {

        int rows =
                piece.length;

        int cols =
                piece[0].length;

        int[][] rotated =
                new int[cols][rows];

        for (int r = 0;
             r < rows;
             r++) {

            for (int c = 0;
                 c < cols;
                 c++) {

                rotated[c][rows - 1 - r] =
                        piece[r][c];
            }
        }

        return rotated;
    }

    private int[][] copyPiece(int[][] piece) {

        int[][] copy =
                new int[piece.length][];

        for (int r = 0;
             r < piece.length;
             r++) {

            copy[r] =
                    piece[r].clone();
        }

        return copy;
    }

    private void aiPlay() {

        if (
                !aiControlled ||
                        aiMoveComplete ||
                        gameOver ||
                        currentPiece == null
        ) {
            return;
        }

        int bestScore =
                Integer.MIN_VALUE;

        int bestCol =
                pieceCol;

        int[][] bestPiece =
                copyPiece(currentPiece);

        for (int rotation = 0;
             rotation < 4;
             rotation++) {

            int[][] testPiece =
                    copyPiece(currentPiece);

            for (int r = 0;
                 r < rotation;
                 r++) {

                testPiece =
                        rotateMatrix(testPiece);
            }

            int maxCol =
                    cols - testPiece[0].length;

            for (int col = 0;
                 col <= maxCol;
                 col++) {

                int testRow = 0;

                if (!canMove(
                        testPiece,
                        testRow,
                        col
                )) {
                    continue;
                }

                while (canMove(
                        testPiece,
                        testRow + 1,
                        col
                )) {
                    testRow++;
                }

                int positionScore =
                        evaluatePosition(
                                testPiece,
                                testRow,
                                col
                        );

                if (positionScore > bestScore) {

                    bestScore = positionScore;
                    bestCol = col;
                    bestPiece =
                            copyPiece(testPiece);
                }
            }
        }

        currentPiece =
                copyPiece(bestPiece);

        currentTetromino.setShape(
                currentPiece
        );

        while (pieceCol < bestCol) {
            moveHorizontalInternal(1);
        }

        while (pieceCol > bestCol) {
            moveHorizontalInternal(-1);
        }

        aiMoveComplete = true;
    }

    private int evaluatePosition(
            int[][] piece,
            int row,
            int col
    ) {

        int[][] testBoard =
                new int[rows][cols];

        for (int r = 0;
             r < rows;
             r++) {

            System.arraycopy(
                    board[r],
                    0,
                    testBoard[r],
                    0,
                    cols
            );
        }

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
                        row + r;

                int boardCol =
                        col + c;

                if (
                        boardRow >= 0 &&
                                boardRow < rows &&
                                boardCol >= 0 &&
                                boardCol < cols
                ) {
                    testBoard[boardRow][boardCol] = 1;
                }
            }
        }

        int evaluation = 0;
        int completedRows = 0;

        for (int r = 0;
             r < rows;
             r++) {

            boolean full = true;

            for (int c = 0;
                 c < cols;
                 c++) {

                if (testBoard[r][c] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {
                completedRows++;
            }
        }

        evaluation +=
                completedRows * 1000;

        int[] heights =
                new int[cols];

        for (int c = 0;
             c < cols;
             c++) {

            for (int r = 0;
                 r < rows;
                 r++) {

                if (testBoard[r][c] != 0) {
                    heights[c] = rows - r;
                    break;
                }
            }
        }

        int holes = 0;

        for (int c = 0;
             c < cols;
             c++) {

            boolean foundBlock = false;

            for (int r = 0;
                 r < rows;
                 r++) {

                if (testBoard[r][c] != 0) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }

        evaluation -= holes * 100;

        int totalHeight = 0;
        int maxHeight = 0;

        for (int height : heights) {
            totalHeight += height;
            maxHeight =
                    Math.max(
                            maxHeight,
                            height
                    );
        }

        evaluation -= totalHeight * 3;
        evaluation -= maxHeight * 5;

        int bumpiness = 0;

        for (int c = 0;
             c < cols - 1;
             c++) {

            bumpiness +=
                    Math.abs(
                            heights[c] -
                                    heights[c + 1]
                    );
        }

        evaluation -= bumpiness * 8;

        return evaluation;
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
            default -> Color.WHITE;
        };
    }

    public int[][] getBoard() {
        return board;
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public int[][] getNextPieceShape() {
        return createPiece(
                pieceSequence.getPieceType(pieceIndex)
        );
    }

    public Color getNextPieceColor() {
        int nextType =
                pieceSequence.getPieceType(pieceIndex) + 1;

        return getPieceColor(nextType);
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

    public boolean isAiControlled() {
        return aiControlled;
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
