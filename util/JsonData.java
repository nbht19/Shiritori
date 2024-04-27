package kobeU.cs.kadaiB.util;

/**
 * 各単語をしりとりゲームにおいて扱いやすい形として保存するデータ形式.
 */
public class JsonData {
    private String lemma;
    private String kana;
    private String pos;
    private String features;

    public JsonData(String lemma, String kana, String pos, String features) {
        this.lemma = lemma;
        this.kana = kana;
        this.pos = pos;
        this.features = features;
    }

    public String getLemma() {
        return lemma;
    }

    public String getKana() {
        return kana;
    }

    public String getPos() {
        return pos;
    }

    public String getFeatures() {
        return features;
    }
}
