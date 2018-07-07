package io.aigar.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.aigar.game.models.CellActions;
import io.aigar.game.models.Game;
import io.aigar.game.models.GameState;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiTest {
    private MockWebServer server = new MockWebServer();

    @BeforeEach
    void setUp() throws IOException {
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void testFetchGame() throws IOException, NetworkException {
        String gameJson = IOUtils.toString(this.getClass().getResourceAsStream("/Game.json"), StandardCharsets.UTF_8);
        server.enqueue(new MockResponse().setBody(gameJson));

        Api api = new Api(1, "", server.url("").toString());

        GameState gameState = api.fetchGameState(0);

        assertNotNull(gameState);
    }

    @Test
    void testSendAction() throws InterruptedException, NetworkException {
        server.enqueue(new MockResponse().setBody("{\"data\":\"ok\"}"));

        List<CellActions> actions = new ArrayList<>();
        actions.add(new CellActions(0).withBurst(true));

        Api api = new Api(1, "", server.url("").toString());

        boolean isOk = api.sendActions(0, actions);

        assertEquals(true, isOk);
        RecordedRequest request = server.takeRequest();
        assertEquals("/api/1/game/0/action", request.getPath());
    }

    @Test
    void testCreateGame() throws NetworkException {
        server.enqueue(new MockResponse().setBody("{\"data\":{\"id\": 0}}"));

        Api api = new Api(1, "", server.url("").toString());

        Game game = api.createPrivate();
        assertEquals(0, game.getId());
    }

}
