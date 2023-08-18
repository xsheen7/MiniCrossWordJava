/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minicrossword;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import grid.Letter;
//import org.json.JSONArray;
//import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ciarwen
 */
public class MiniCrossword {

    //    static ArrayList<String> clues = new ArrayList<>();
    static ArrayList<Double> frc = new ArrayList<>();
    static ArrayList<String> words = new ArrayList<>();
    static ArrayList<String> words3 = new ArrayList<>();
    static ArrayList<String> words4 = new ArrayList<>();
    static ArrayList<String> words5 = new ArrayList<>();
    static ArrayList<String> wordsUsed = new ArrayList<>();

    static ArrayList<BoardExl> allBoardExl = new ArrayList<>();

    static int maxCount = 1999;

    public static void main(String[] args) {

        try {
//            //所有规则
//            Scanner CrosswordClues = new Scanner(new File("543clues"));
//            while (CrosswordClues.hasNext()) {
//                clues.add(CrosswordClues.next());
//            }
//            CrosswordClues.close();


            //所有单词
            //trying to only read in words from the word list that are ever actually used
            Scanner CrosswordWords = new Scanner(new File("543words_frc"));
            while (CrosswordWords.hasNext()) {
                String word = CrosswordWords.next();
                words.add(word);

                if (word.length() == 3) {
                    words3.add(word);
                }

                if (word.length() == 4) {
                    words4.add(word);
                }

                if (word.length() == 5) {
                    words5.add(word);
                }
            }

            CrosswordWords.close();

            //单词词频 用来排序关卡
            Scanner WordsFrc = new Scanner(new File("543frc"));
            while (WordsFrc.hasNext()) {
                String str = WordsFrc.next();
                double value = Double.parseDouble(str);
                frc.add(value);
            }
            WordsFrc.close();

            //使用过的单词
            Scanner usedWord = new Scanner(new File("used_words"));
            while (usedWord.hasNext()) {
                String str = usedWord.next();
                wordsUsed.add(str.toLowerCase());
            }

        } catch (Exception e) {
            System.err.println("Failure:" + e.getMessage());
        }

//        //3x3
//        ArrayList<SaveData> data3x3 = Creat3x3(30);
//        ArrayList<BoardExl> boardExl3x3 = GetBoardExlData(data3x3);
//        boardExl3x3.sort(Comparator.comparingDouble(i -> -i.averageFrc));
//        SaveLevelDataRefreshUsedWords(boardExl3x3);

        //4x4_0
        ArrayList<SaveData> data4x4_0 = Creat4x4(0,25);
        ArrayList<BoardExl> boardExl4x4_0 = GetBoardExlData(data4x4_0);
        boardExl4x4_0.sort(Comparator.comparingDouble(i -> -i.averageFrc));
        SaveLevelDataRefreshUsedWords(boardExl4x4_0);

//        //4x4_1
        ArrayList<SaveData> data4x4_1 = Creat4x4(1,25);
        ArrayList<BoardExl> boardExl4x4_1 = GetBoardExlData(data4x4_1);
        boardExl4x4_1.sort(Comparator.comparingDouble(i -> -i.averageFrc));
        SaveLevelDataRefreshUsedWords(boardExl4x4_1);
//
        //5x5_0
        ArrayList<SaveData> data5x5_0 = Creat5x5(0, 25);
        ArrayList<BoardExl> boardExl5x5_0 = GetBoardExlData(data5x5_0);
        boardExl5x5_0.sort(Comparator.comparingDouble(i -> -i.averageFrc));
        SaveLevelDataRefreshUsedWords(boardExl5x5_0);
//
        //5x5_1
        ArrayList<SaveData> data5x5_1 = Creat5x5(1, 10);
        ArrayList<BoardExl> boardExl5x5_1 = GetBoardExlData(data5x5_1);
        boardExl5x5_1.sort(Comparator.comparingDouble(i -> -i.averageFrc));
        SaveLevelDataRefreshUsedWords(boardExl5x5_1);
//
//        //5x5_2
        ArrayList<SaveData> data5x5_2 = Creat5x5(2, 5);
        ArrayList<BoardExl> boardExl5x5_2 = GetBoardExlData(data5x5_2);
        boardExl5x5_2.sort(Comparator.comparingDouble(i -> -i.averageFrc));
        SaveLevelDataRefreshUsedWords(boardExl5x5_2);

    }

    private static void SaveLevelDataRefreshUsedWords(ArrayList<BoardExl> exls){
        allBoardExl.addAll(exls);
        String exlJson = GetJson(allBoardExl);
        SaveText(exlJson, "boardJson4.json");

        // 使用 try-with-resources 来自动关闭流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("used_words"))) {
            // 遍历数组并逐行写入文件
            for (var value : wordsUsed) {
                writer.write(value);
                writer.newLine(); // 写入换行符
            }
            System.out.println("使用过单词库更新");
        } catch (IOException e) {
            System.out.println("使用过单词库更新发生错误：" + e.getMessage());
        }
    }

    private static void AddUsedWords(SaveData data) {
        for (WordInfo wordInfo : data.words) {
            String word = wordInfo.word.toLowerCase();
            if(!wordsUsed.contains(word)){
                wordsUsed.add(word);
            }
        }
    }

    private static String[] RemoveUsedWord() {

        for (String word : wordsUsed) {
            words.remove(word);
            if(word.length() == 3){
                words3.remove(word);
            }
            if(word.length() == 4){
                words4.remove(word);
            }
            if(word.length() == 5){
                words5.remove(word);
            }
        }
        String[] leftWords = new String[words.size()];
        return words.toArray(leftWords);
    }

    private static ArrayList<SaveData> Creat3x3(int count) {
        // 创建日期对象
        Date currentDate = new Date();

        // 创建日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化日期对象为字符串
        String formattedDate = dateFormat.format(currentDate);
        System.out.println("--Start--" + formattedDate);
        Random random = new Random();

        ArrayList<SaveData> datas = new ArrayList<>();
        String[] leftWords;

        int creat = 0;
        int tryCount =0;
        while (creat < count) {

            tryCount++;
            if(tryCount>=maxCount)
                break;

            leftWords = RemoveUsedWord();

            Crossword mine = new Crossword(3, 3, leftWords);
            int count3 = words3.size();
            int index = random.nextInt(count3);
            String word = words3.get(index);
            mine.setWord(0, word, true);
            mine = Searcher.Search(mine);

            if (mine != null) {
                System.out.println(mine);
                creat++;
                SaveData data = SaveCrossWord(mine);
                AddUsedWords(data);
                if (data != null) {
                    datas.add(data);
                }
            }
        }

        currentDate = new Date();
        formattedDate = dateFormat.format(currentDate);
        System.out.println("Success:" + formattedDate);

        return datas;
    }

    //createType 0满的
    private static ArrayList<SaveData> Creat4x4(int creatType, int count) {

        // 创建日期对象
        Date currentDate = new Date();

        // 创建日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化日期对象为字符串
        String formattedDate = dateFormat.format(currentDate);
        System.out.println("--Start--" + formattedDate);
        Random random = new Random();

        ArrayList<SaveData> datas = new ArrayList<>();
        Crossword mine = null;
        int count4;
        int index4;
        String word4;
        String[] leftWords;

        int creat = 0;

        int hasBlack = random.nextInt(4) + 1;//1234 1234四个角

        int tryCount=0;
        while (creat < count) {
            System.out.println("blank:" + hasBlack);

            tryCount++;
            if(tryCount>=maxCount)
                break;

            //设置提示的单词
            leftWords = RemoveUsedWord();
            count4 = words4.size();
            index4 = random.nextInt(count4);
            word4 = words4.get(index4);

            switch (creatType) {
                case 0:
                    mine = new Crossword(4, 4, leftWords);
                    mine.setWord(0, word4, true);
                    mine = Searcher.Search(mine);
                    break;
                case 1:
                    switch (hasBlack) {
                        case 1:
                            mine = new Crossword(4, 4, leftWords);
                            mine.setLetter(0, 0, Letter.BLACK);
                            mine.setWord(3, word4, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 2:
                            mine = new Crossword(4, 4, RemoveUsedWord());
                            mine.setLetter(0, 3, Letter.BLACK);
                            mine.setWord(3, word4, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 3:
                            mine = new Crossword(4, 4, RemoveUsedWord());
                            mine.setLetter(3, 3, Letter.BLACK);
                            mine.setWord(0, word4, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 4:
                            mine = new Crossword(4, 4, RemoveUsedWord());
                            mine.setLetter(3, 0, Letter.BLACK);
                            mine.setWord(0, word4, true);
                            mine = Searcher.Search(mine);
                            break;
                    }
                    break;
            }

            if (mine != null) {
                System.out.println(mine);

                creat++;
                hasBlack = random.nextInt(4) + 1;

                SaveData data = SaveCrossWord(mine);
                AddUsedWords(data);

                if (data != null) {
                    datas.add(data);
                }
            }
        }

        currentDate = new Date();
        formattedDate = dateFormat.format(currentDate);
        System.out.println("Success:" + formattedDate);
        return datas;
    }

    //creatType 0满的，1横着两个方块
    private static ArrayList<SaveData> Creat5x5(int creatType, int count) {
        // 创建日期对象
        Date currentDate = new Date();

        // 创建日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化日期对象为字符串
        String formattedDate = dateFormat.format(currentDate);
        System.out.println("--Start--" + formattedDate);
        Random random = new Random();

        ArrayList<SaveData> datas = new ArrayList<>();
        Crossword mine = null;
        int count5;
        int index5;
        String word5;
        String[] leftWords;

        int creat = 0;

        int hasBlack1 = random.nextInt(4) + 1;//1234 四个角 一个空
        int hasBlackRow2 = random.nextInt(4) + 1;//1234 横着两个空
        int hasBlackCol2 = random.nextInt(4) + 1;//1234 竖着两个空
        int hasBlack3 = random.nextInt(4) + 1;//1234 三角空

        int tryCount =0;
        while (creat < count) {
            System.out.println(String.format("blank1:%s blankRow2:%s blankCol2:%s blank3:%s", hasBlack1, hasBlackRow2, hasBlackCol2, hasBlack3));

            tryCount++;
            if(tryCount>=maxCount)
                break;

            //设置提示的单词
            leftWords = RemoveUsedWord();
            count5 = words5.size();
            index5 = random.nextInt(count5);
            String word = words5.get(index5);

            switch (creatType) {
                case 0:
                    mine = new Crossword(5, 5, leftWords);
                    mine.setWord(0, word, true);
                    mine = Searcher.Search(mine);
                    break;
                case 1:
                    //一个空一个角
                    switch (hasBlack1) {
                        case 1:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(0, 0, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 2:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(0, 4, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 3:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(4, 4, Letter.BLACK);
                            mine.setWord(0, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 4:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(4, 0, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                    }
                    break;
                case 2:
                    //两个横空一个角
                    switch (hasBlackRow2) {
                        case 1:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(0, 0, Letter.BLACK);
                            mine.setLetter(0, 1, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 2:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(0, 4, Letter.BLACK);
                            mine.setLetter(0, 3, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 3:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(4, 4, Letter.BLACK);
                            mine.setLetter(4, 3, Letter.BLACK);
                            mine.setWord(0, word, true);
                            mine = Searcher.Search(mine);
                            break;
                        case 4:
                            mine = new Crossword(5, 5, RemoveUsedWord());
                            mine.setLetter(4, 0, Letter.BLACK);
                            mine.setLetter(4, 1, Letter.BLACK);
                            mine.setWord(3, word, true);
                            mine = Searcher.Search(mine);
                            break;
                    }
                    break;
            }

            if (mine != null) {
                System.out.println(mine);

                creat++;
                hasBlack1 = random.nextInt(4) + 1;//1234 四个角 一个空
                hasBlackRow2 = random.nextInt(4) + 1;//01234 横着两个空
                hasBlackCol2 = random.nextInt(4) + 1;//01234 竖着两个空
                hasBlack3 = random.nextInt(4) + 1;//01234 三角空

                SaveData data = SaveCrossWord(mine);
                AddUsedWords(data);

                if (data != null) {
                    datas.add(data);
                }
            }
        }

        currentDate = new Date();
        formattedDate = dateFormat.format(currentDate);
        System.out.println("Success:" + formattedDate);
        return datas;
    }

    private static SaveData SaveCrossWord(Crossword crossword) {
        SaveData data = new SaveData();
        int row = crossword.getGrid().length;
        int col = crossword.getGrid()[0].length;
        data.row = row;
        data.col = col;
        data.words = new ArrayList<WordInfo>();
        data.board = new ArrayList<char[]>();


        for (int i = 0; i < row; i++) {
            char[] rowChars = new char[col];
            for (int j = 0; j < col; j++) {
                rowChars[j] = crossword.getGrid()[i][j].getChar();
            }
            data.board.add(rowChars);
        }

        //行单词
        for (int i = 0; i < row; i++) {

            char[] rowChars = new char[col];
            int startCol = -1;
            for (int j = 0; j < col; j++) {
                char c = crossword.getGrid()[i][j].getChar();
                if (c != Letter.BLACK.display && startCol == -1) {
                    startCol = j;
                }
                rowChars[j] = c;
            }
            String word = new String(rowChars);
            word = word.replace("$", "").toLowerCase();
            WordInfo wordInfo = new WordInfo();
            wordInfo.word = word;
            wordInfo.index = words.indexOf(word);
            wordInfo.cross = true;
            wordInfo.startRow = row - 1 - i;
            wordInfo.startCol = startCol;
            data.words.add(wordInfo);
        }

        //列单词
        for (int i = 0; i < col; i++) {

            char[] rowChars = new char[col];
            int startRow = -1;
            for (int j = 0; j < row; j++) {
                char c = crossword.getGrid()[j][i].getChar();
                if (c != Letter.BLACK.display && startRow == -1) {
                    startRow = j;
                }
                rowChars[j] = c;
            }
            String word = new String(rowChars);
            word = word.replace("$", "").toLowerCase();
            WordInfo wordInfo = new WordInfo();
            wordInfo.word = word;
            wordInfo.index = words.indexOf(word);
            wordInfo.cross = false;
            wordInfo.startRow = row - 1 - startRow;
            wordInfo.startCol = i;
            data.words.add(wordInfo);
        }

        return data;
    }

    private static ArrayList<BoardExl> GetBoardExlData(ArrayList<SaveData> datas) {
        ArrayList<BoardExl> boardExls = new ArrayList<>();
        for (SaveData data : datas) {
            BoardExl boardExl = new BoardExl();
            double allFrc = 0;
            for (WordInfo wordInfo : data.words) {
                if (wordInfo.index >= frc.size())
                    continue;
                double wordFrc = frc.get(wordInfo.index);
                allFrc += wordFrc;
            }
            boardExl.averageFrc = allFrc / data.words.size();
            boardExl.crossStr = data.GetCrossStr();
            boardExl.downStr = data.GetDownStr();
            boardExl.boardJson = GetJson(data);
            boardExls.add(boardExl);
        }
        return boardExls;
    }

    public static String GetJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            System.err.println("exl json failed:" + e.getMessage());
        }

        return null;
    }

    public static <T> T GetJsonObj(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    public static <T> T GetJsonObj(String json, TypeReference<T> typeReference) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, typeReference);
    }

    public static void SaveText(String str, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<SaveData> GetDatasFromExl(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            List<BoardExl> exls = GetJsonObj(json, new TypeReference<List<BoardExl>>() {
            });
            List<SaveData> datas = new ArrayList<>();

            int index = 0;
            for (BoardExl boardExl : exls) {
                SaveData data = GetJsonObj(boardExl.boardJson, SaveData.class);
                datas.add(data);
            }

            return datas;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}