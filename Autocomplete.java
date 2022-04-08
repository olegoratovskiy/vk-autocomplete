import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Autocomplete {
    private final ArrayList<PairSI> data; // Массив пар, где пара = [фраза, частота]
    private final SegmentTree tree;       // дерево отрезков merge-sort

    // Получает имя файла
    // Инициализирует массив данных, массив дерева отрезков merge-sort
    Autocomplete(String filename) throws IOException {
        this.data = getData(filename);
        int[] frequency = new int[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            frequency[i] = data.get(i).frequency;
        }
        if (data.isEmpty()) {
            throw new IOException(filename);
        }
        this.tree = new SegmentTree(frequency);
    }

    // Получает строку из фразы и частоты, либо просто фразу
    // Вернет пару из фразы и частоты
    private static PairSI parseLine(String line) {
        StringBuilder expr = new StringBuilder();
        int i = 0;
        while (i < line.length() && line.charAt(i) != ':') {
            expr.append(line.charAt(i));
            ++i;
        }

        if (i == line.length()) {
            return new PairSI(expr.toString().toLowerCase(), 1);
        }

        StringBuilder number = new StringBuilder();
        while (i < line.length()) {
            if (Character.isDigit(line.charAt(i))) {
                number.append(line.charAt(i));
            }
            ++i;
        }
        return new PairSI(expr.toString().toLowerCase(), Integer.parseInt(number.toString()));
    }

    // Получает имя файла
    // Возвращает массив пар [строка, частота]
    private static ArrayList<PairSI> getData(String input) throws IOException {
        try (Scanner in = new Scanner(new File(input), StandardCharsets.UTF_8)) {
            ArrayList<PairSI> data = new ArrayList<>();
            while (in.hasNextLine()) {
                String line = in.nextLine();
                data.add(parseLine(line));
            }
            Collections.sort(data);
            return data;
        }
    }

    // Получает массив пар [строка, частота] и фразу для поиска
    // Возвращает пару [l, r] : все строки в этом промежутке начинаются на word, если таких нет - возвращает [-1, -1]
    private static int[] findBoarders(ArrayList<PairSI> data, String word) {
        // min i: word <= data.get(i).word
        // (l, r]
        int l = -1;
        int r = data.size() - 1;
        while (r - l > 1) {
            int m = (l + r) / 2;
            if (word.compareTo(data.get(m).word) <= 0) {
                r = m;
            } else {
                l = m;
            }
        }
        int resultL = r;

        if (!data.get(resultL).word.startsWith(word)) {
            return new int[]{-1, -1};
        }

        // max i : data.get(i).word that starts with word
        // [l, r)
        l = resultL;
        r = data.size();
        while (r - l > 1) {
            int m = (l + r) / 2;
            if (data.get(m).word.startsWith(word)) {
                l = m;
            } else {
                r = m;
            }
        }

        return new int[]{resultL, l};
    }

    // Получает две строки
    // Возвращает расстояние Левенштейна между двумя строками
    private static int getMinDistBetweenTwoWords(String a, String b) {
        final double K = 0.2;
        int n = a.length() + 1;
        int m = Math.min(b.length() + 1, a.length() + 1 + (int)(a.length() * K));
        int[][] dp = new int[n][m];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                if (i == 0 && j == 0) {
                    dp[i][j] = 0;
                } else if (i > 0 && j == 0) {
                    dp[i][j] = i;
                } else if (i == 0 && j > 0) {
                    dp[i][j] = j;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j] + 1));
                }
            }
        }
        return dp[n - 1][m - 1];
    }

    // Получает фразу для поиска и число релевантных запросов
    // Возвращает массив из <=k номеров строк которые подходят под запрос методом Левенштейна
    private ArrayList<Integer> getLevenshteinK(String word, int k) {
        PriorityQueue<int[]> pq = new PriorityQueue<>
                ((first, second) -> (second[0] - first[0] != 0 ? second[0] - first[0] :
                        data.get(first[1]).frequency - data.get(second[1]).frequency));
        for (int i = 0; i < data.size(); ++i) {
            pq.add(new int[]{getMinDistBetweenTwoWords(word, data.get(i).word), i});
            if (pq.size() > k) {
                pq.poll();
            }
        }
        ArrayList<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll()[1]);
        }
        Collections.reverse(result);
        return result;
    }

    // Получает фразу для поиска и число релевантных запросов
    // Возвращает массив из <=k номеров строк которые подходят под запрос
    public ArrayList<String> getTopK(String word, int k) {
        int[] boarders = findBoarders(data, word);
        ArrayList<Integer> indexesOfStrings;
        if (boarders[0] == -1 || boarders[1] - boarders[0] + 1 < k) {
            indexesOfStrings = getLevenshteinK(word, k);
        } else {
            indexesOfStrings = tree.askTopK(k, boarders[0], boarders[1] + 1);
        }
        ArrayList<String> result = new ArrayList<>();
        for (Integer indexOfString : indexesOfStrings) {
            result.add(data.get(indexOfString).word);
        }
        return result;
    }
}
