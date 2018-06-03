package io.aigar.game;

import io.aigar.game.models.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiDefinition {
    String api = "api/1/";

    @GET(api + "game/{gameId}")
    Call<Data<GameState>> getGameState(@Path("gameId") int gameId);

    @POST(api + "game/{gameId}/action")
    Call<Data<String>> postAction(@Path("gameId") int gameId, @Body Action action);

    @POST(api + "game")
    Call<Data<Game>> createGame(@Body ProtectedData protectedData);
}
