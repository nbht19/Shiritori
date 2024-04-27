package kobeU.cs.kadaiB.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class CotohaAccess {

    /**
     * Gson,okhttp3を用いてURL,ID,パスワードからCOTOHAのアクセストークンを取得する.
     * @return
     */
    public static String getAccessToken() {

        String url = "https://api.ce-cotoha.com/v1/oauth/accesstokens";
        String clientId = "********************";
        String clientSecret = "**********";

        Gson gson = new Gson();
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("grantType", "client_credentials");
        jsonObj.addProperty("clientId", clientId);
        jsonObj.addProperty("clientSecret", clientSecret);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObj.toString());
        Request request = new Request.Builder() //
                .url(url) //
                .post(body) //
                .build();

        try (Response response = client.newCall(request).execute()) {
            int responseCode = response.code();
            String originalResponseBody = response.body().string();
            String accesToken = gson.fromJson(originalResponseBody, JsonObject.class).get("access_token").toString();
            // ダブルクォーテーションを除去する.
            return accesToken.substring(1, accesToken.length()-1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}