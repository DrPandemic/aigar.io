package io.aigar;

import io.aigar.config.Config;
import io.aigar.config.ConfigReader;
import io.aigar.game.Api;
import io.aigar.game.NetworkException;
import io.aigar.game.models.Cell;
import io.aigar.game.models.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Play {
    private static final String BASE_GAME_URL = "web/index.html";
    private static final int RANKED_GAME_ID = -1;
    private static final int UPDATES_PER_SECOND = 3; // how many times we should contact the server per second
    private static final Logger logger = LoggerFactory.getLogger(Play.class);

    public static void main(String[] args) throws IOException, InterruptedException, NetworkException, URISyntaxException {
        // Read input parameters
        boolean createPrivate = false;
        boolean joinPrivate = false;

        for (String argument : args) {
            if (argument.equals("--create-private") || argument.equals("-c"))
                createPrivate = true;
            if (argument.equals("--join-private") || argument.equals("-j"))
                joinPrivate = true;
        }

        // Read config
        ConfigReader configReader = new ConfigReader();
        Config config = configReader.readConfig();

        // Setup game
        Api api = new Api(config.getPlayerId(), config.getPlayerSecret(), config.getApiUrl());
        Ai ai = new Ai();
        int previousTick = -1;

        int gameId = RANKED_GAME_ID;
        if (joinPrivate) {
            gameId = config.getPlayerId();
            logger.info("Joining private game, id: {}", gameId);
        }

        if (createPrivate) {
            gameId = api.createPrivate().getId();
            logger.info("Private game created, id: {}", gameId);

            // This is useful since the game creation is not instant
            Thread.sleep(500);
        }

        if (Desktop.isDesktopSupported())
            Desktop.getDesktop().browse(new URI(config.getApiUrl().concat(BASE_GAME_URL.concat("?gameId=" + gameId))));

        // Game loop
        while (true) {
            GameState gameState = api.fetchGameState(gameId);

            // After a game reset, it reinstanciates the AI object
            if (gameState.getTick() < previousTick)
                ai = new Ai();
            previousTick = gameState.getTick();

            // Actual loop
            ai.step(gameState);
            api.sendActions(gameId, gameState.getMe()
                    .getCells()
                    .stream()
                    .map(Cell::getActions)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList()));

            Thread.sleep(1000 / UPDATES_PER_SECOND);
        }
    }
}
