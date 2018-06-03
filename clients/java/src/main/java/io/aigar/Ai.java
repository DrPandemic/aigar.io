package io.aigar;

import io.aigar.game.models.Cell;
import io.aigar.game.models.Coordinate;
import io.aigar.game.models.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ai {
    private static final Logger logger = LoggerFactory.getLogger(Ai.class);

    public void step(GameState game) {
        List<Coordinate> resources = game.getResources().getAllResources();

        for (Cell cell : game.getMe().getCells()) {
            Coordinate target = findClosest(resources, cell.getPosition());

            cell.move(target);
            cell.split();
        }
    }

    private Coordinate findClosest(List<Coordinate> coordinates, Coordinate currentPosition) {
        return coordinates.stream()
                .collect(Collectors.toMap(currentPosition::distanceTo, r -> r))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
