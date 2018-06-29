package io.aigar;

import io.aigar.game.models.Cell;
import io.aigar.game.models.Coordinate;
import io.aigar.game.models.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Ai {
    private static final Logger logger = LoggerFactory.getLogger(Ai.class);
    private Random rand = new Random();

    public void step(GameState game) {
        for (Cell cell : game.getMe().getCells()) {
            double distance = cell.getPosition().distanceTo(cell.getTarget());

            if (distance < 10) {
                Coordinate target = new Coordinate(rand.nextDouble() * game.getMap().width, rand.nextDouble() * game.getMap().height);
                cell.move(target);
            }
        }
    }
}
