package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieceSequence {

    private final Random random = new Random();
    private final List<Integer> sequence = new ArrayList<>();

    public int getPieceType(int index) {
        while (sequence.size() <= index) {
            sequence.add(random.nextInt(7));
        }

        return sequence.get(index);
    }
}
