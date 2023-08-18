package minicrossword;

import java.util.ArrayList;
import java.util.List;

public class ToUnityConfig {
    public static void main(String[] args) {
        List<SaveData> datas = MiniCrossword.GetDatasFromExl("boardJson4.json");

        List<UnitySaveData> unitySaveDatas = new ArrayList<>();
        for (SaveData data : datas) {
            UnitySaveData unitySaveData = new UnitySaveData();
            unitySaveData.row = data.row;
            unitySaveData.col = data.col;
            unitySaveData.words = new ArrayList<>();
            for (WordInfo wi : data.words) {
                unitySaveData.words.add(wi.word.toLowerCase());
            }
            unitySaveData.board = data.board;
            unitySaveDatas.add(unitySaveData);
        }
        String json = MiniCrossword.GetJson(unitySaveDatas);
        MiniCrossword.SaveText(json, "level_config1.json");
        System.out.println("to unity json Success");
    }
}
