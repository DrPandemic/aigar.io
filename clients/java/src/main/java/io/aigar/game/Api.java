package io.aigar.game;

import io.aigar.game.models.*;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Api {
    private static final String OK = "ok";

    private int playerId;
    private String playerSecret;
    private ApiDefinition client;

    public Api(int playerId, String playerSecret, String apiUrl) {
        this.playerId = playerId;
        this.playerSecret = playerSecret;

        Retrofit builder = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(getUnsafeOkHttpClient())
                .build();

        client = builder.create(ApiDefinition.class);
    }

    public GameState fetchGameState(final int gameId) throws NetworkException {
        GameState state = executeCall(() -> client.getGameState(gameId));

        state.setMe(state.getPlayers().stream().filter(p -> p.getId() == playerId).findFirst().orElse(null));
        state.setEnemies(state.getPlayers().stream().filter(p -> p.getId() != playerId).collect(Collectors.toList()));

        return state;
    }

    public boolean sendActions(int gameId, List<CellActions> actions) throws NetworkException {
        String status = executeCall(() -> client.postAction(gameId, new Action(playerSecret, actions)));
        return OK.equals(status);
    }

    public Game createPrivate() throws NetworkException {
        return executeCall(() -> client.createGame(new ProtectedData().withPlayerSecret(playerSecret)));
    }

    private <T> T executeCall(Supplier<Call<Data<T>>> supplier) throws NetworkException {
        try {
            Call<Data<T>> call = supplier.get();

            Response<Data<T>> response = call.execute();
            if(response.isSuccessful())
                return response.body().getData();

            throw new NetworkException(response.code(), response.errorBody().string());
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
