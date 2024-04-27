package kobeU.cs.kadaiB.game;

import kobeU.cs.BTest.API.CallCotoha;
import kobeU.cs.BTest.API.CotohaAccess;
import kobeU.cs.BTest.util.JsonData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShiritoriMaster {

    public static List<String> used = new ArrayList<>();
    private static String accessToken = null;

    public ShiritoriMaster() { }

    /**
     * 初期化メソッド. 以降で使用するアクセストークンを取得し保存する.
     */
    public static void initialize() {
        accessToken = CotohaAccess.getAccessToken();
    }

    /**
     * 前プレイヤーの末尾文字とターンプレイヤーの入力単語から,しりとりを続行できるか判定するメソッド
     * @param preLastChar
     * @param msg
     * @return
     * @throws IOException
     */
    public static String checkWord(char preLastChar, String msg) throws IOException {

        JsonData result = CallCotoha.getJsonResult(accessToken, msg);

        String lemma = result.getLemma();
        String kana = result.getKana();
        String pos = result.getPos();
        String features = result.getFeatures();

        if(msg.equals("used")) { return msg; }

        // 単語が存在する名詞であるか判定し、単語のlemma
        if(lemma.equals("") || !pos.equals("名詞") || !(features.equals("[]") || features.equals("[\"固有\"]") || features.equals("[\"固有\",\"地\"]")|| features.equals("[\"固有\",\"組織\"]"))) {
            System.out.println("存在する名詞ではありません。やり直し");
            return "noun";
        }

        // 文字が続いているかの判定
        if(kana.charAt(0) != preLastChar) {
            System.out.println("文字が続いていません。やり直し");
            return "follow";
        }

        // 使用済みかどうかの判定
        if(used.contains(lemma)) {
            System.out.println("使用済みです。やり直し");
            return "already";
        }

        // 最後が『ン』かどうかチェック
        if(checkLast(kana)) {
            System.out.println("『ン』がついたので負けです。");
            return "ン";
        }

        return lemma;
    }

    // 入力単語からカナ読みを取得するメソッド.
    public static String getKana(String str) throws IOException {
        return CallCotoha.getKana(accessToken, str);
    }

    /**
     * 単語を使用済みリストに追加するメソッド.
     * @param str
     */
    public static void addUsed(String str) {

        used.add(str);
    }

    /**
     * 単語をしりとり用に処理するメソッド
     * @param str
     * @return
     */
    public static String process(String str) {

        return removeMacron(toBigger(str));
    }

    /**
     * 小文字カタカナを大文字カタカナに変換するメソッド.
     * @param str
     * @return
     */
    private static String toBigger(String str) {

        str = str.replace("ァ","ア");
        str = str.replace("ィ","イ");
        str = str.replace("ゥ","ウ");
        str = str.replace("ェ","エ");
        str = str.replace("ォ","オ");
        str = str.replace("ャ","ヤ");
        str = str.replace("ュ","ユ");
        str = str.replace("ョ","ヨ");

        return str;
    }

    /**
     * 末尾が"ー"の場合に取り除くメソッド.
     * @param str
     * @return
     */
    private static String removeMacron(String str) {

        if(str.length() > 0 && str.charAt(str.length()-1) == 'ー') {
            str = str.substring(0, str.length()-1);
        }

        return str;
    }

    /**
     * 末尾が"ン"かどうか判定するメソッド.
     * @param str
     * @return
     */
    public static boolean checkLast(String str) {

        if(str.charAt(str.length()-1) == 'ン') {
            return true;
        }

        return false;
    }
}

