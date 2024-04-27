package kobeU.cs.kadaiB.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kobeU.cs.kadaiB.util.JsonData;
import okhttp3.*;

import java.io.IOException;

public class CallCotoha {

    /**
     * アクセストークンを用いてCOTOHにアクセスし,入力単語の解析結果からlemma,カナ,品詞,副品詞(配列)をまとめてJsonData形式で出力する.
     * @param token
     * @param sentense
     * @return
     * @throws IOException
     */
    public static JsonData getJsonResult(String token, String sentense) throws IOException {

        String url = "https://api.ce-cotoha.com/api/dev" + "/nlp/v1/parse";
        String word = sentense;
        String type = "default";
        String accessToken = token;

        Gson gson = new Gson();
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("sentence", word);
        jsonObj.addProperty("type", type);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObj.toString());
        Request request = new Request.Builder() //
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            JsonObject jsonResBody = gson.fromJson(response.body().string(), JsonObject.class);

            int morphemeNum = jsonResBody.get("result").getAsJsonArray().size();

            if(morphemeNum > 1) {
                return new JsonData("", "", "", "");
            }

            JsonElement jsonResult = jsonResBody.get("result").getAsJsonArray().get(0).getAsJsonObject().get("tokens");

            // それぞれダブルクォーテーションを除去する.
            JsonElement lemma = jsonResult.getAsJsonArray().get(0).getAsJsonObject().get("lemma");
            String lemma1 = lemma.toString().substring(1, lemma.toString().length()-1);
            JsonElement kana = jsonResult.getAsJsonArray().get(0).getAsJsonObject().get("kana");
            String kana1 = kana.toString().substring(1, kana.toString().length()-1);
            JsonElement pos = jsonResult.getAsJsonArray().get(0).getAsJsonObject().get("pos");
            String pos1 = pos.toString().substring(1, pos.toString().length()-1);
            JsonElement features = jsonResult.getAsJsonArray().get(0).getAsJsonObject().get("features");

            System.out.println(features.toString());

            return new JsonData(lemma1, kana1, pos1, features.toString());
        }
    }

    /**
     * 入力単語のカナ読みを返すメソッド.カナ読みのみ欲しい場合に使う.
     * @param token
     * @param word
     * @return
     * @throws IOException
     */
    public static String getKana(String token, String word) throws IOException {

        JsonData jsonResult = getJsonResult(token, word);

        return jsonResult.getKana();
    }
}