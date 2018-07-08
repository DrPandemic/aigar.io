package io.aigar;

import io.aigar.game.models.Cell;
import io.aigar.game.models.GameState;
import org.mini2Dx.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Ai {
    private static final Logger logger = LoggerFactory.getLogger(Ai.class);
    private Random rand = new Random();

    public void step(GameState game) {
        for (Cell cell : game.getMe().getCells()) {
            double distance = cell.getPosition().dst(cell.getTarget());

            if (distance < 10) {
                Vector2 target = new Vector2(rand.nextFloat() * game.getMap().getWidth(), rand.nextFloat() * game.getMap().getHeight());
                cell.move(target);
            }
        }
    }
}
