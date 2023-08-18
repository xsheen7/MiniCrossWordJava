package minicrossword;

import java.util.ArrayList;

public class SaveData {
    public int row;
    public int col;
    public ArrayList<WordInfo> words;
    public ArrayList<char[]> board;

    public String GetCrossStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <row; i++) {
            String word = new String(board.get(i));
            sb.append(word+"  ");
        }
        return sb.toString();
    }

    public String GetDownStr(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <col; i++) {
            char[] colChars =new char[row];
            for (int j = row-1;j>=0;j--){
                char cr = board.get(j)[i];
                colChars[j] = cr;
            }
            String word = new String(colChars);
            sb.append(word+"  ");
        }
        return sb.toString();
    }
}
