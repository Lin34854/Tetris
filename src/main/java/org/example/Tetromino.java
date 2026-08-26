package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tetromino extends GameObject {

    private int[][] shape;
    private final Color color;
    private final int blockSize;
    private double smoothOffset;

    public Tetromino(
            double x,
            double y,
            int[][] shape,
            Color color,
            int blockSize
    ) {
        super(x, y);
        this.shape = shape;
        this.color = color;
        this.blockSize = blockSize;
    }

    public void setPosition(
            double x,
            double y
    ) {
        this.x = x;
        this.y = y;
    }

    public void setShape(int[][] shape) {
        this.shape = shape;
    }

    public void setSmoothOffset(
            double smoothOffset
    ) {
        this.smoothOffset = smoothOffset;
    }

    @Override
    public void draw(GraphicsContext gc) {

        gc.setFill(color);

        for (int r = 0;
             r < shape.length;
             r++) {

            for (int c = 0;
                 c < shape[r].length;
                 c++) {

                if (shape[r][c] == 0) {
                    continue;
                }

                double drawX =
                        (x + c) *
                                blockSize;

                double drawY =
                        (y + r + smoothOffset) *
                                blockSize;

                gc.fillRect(
                        drawX + 1,
                        drawY + 1,
                        blockSize - 2,
                        blockSize - 2
                );

                gc.setStroke(Color.WHITE);

                gc.strokeRect(
                        drawX + 1,
                        drawY + 1,
                        blockSize - 2,
                        blockSize - 2
                );
            }
        }
    }
}
