import java.util.ArrayList;
import java.util.PriorityQueue;

public class SegmentTree {
    private final int size;                         // Количество всех фраз
    private final ArrayList<ArrayList<int[]>> tree; // tree[v] = массив пар, где первое число пары - частота,
                                                    // а второе - индекс фраз в массиве
    // Получает массив частот фраз
    // Инициализирует размер, массив дерева отрезков merge-sort
    public SegmentTree(int[] frequency) {
        this.size = frequency.length;
        this.tree = new ArrayList<>(4 * size);
        for (int i = 0; i < 4 * size; ++i) {
            this.tree.add(new ArrayList<>());
        }
        build(frequency, 0, 0, size);
    }

    // Получает массив частот фраз, текущую вершину, отрезок [l, r) текущей вершины
    // Строит дерево отрезков merge-sort чтобы искать k максимумом на отрезке
    private void build(int[] frequency, int v, int l, int r) {
        if (r - l == 1) {
            tree.get(v).add(new int[]{frequency[l], l});
            return;
        }
        int m = (l + r) / 2;
        build(frequency, 2 * v + 1, l, m);
        build(frequency, 2 * v + 2, m, r);
        tree.set(v, merge(tree.get(2 * v + 1), tree.get(2 * v + 2)));
    }

    // Получает 2 массива пар, где элемент пары = [частота_фразы, индекс_фразы]
    // Возвращает смердженный массив пар, где элемент пары = [частота_фразы, индекс_фразы]
    private static ArrayList<int[]> merge(ArrayList<int[]> one, ArrayList<int[]> two) {
        ArrayList<int[]> result = new ArrayList<>(one.size() + two.size());
        int pointerOne = 0;
        int pointerTwo = 0;
        while (pointerOne < one.size() && pointerTwo < two.size()) {
            if (one.get(pointerOne)[0] >= two.get(pointerTwo)[0]) {
                result.add(one.get(pointerOne));
                ++pointerOne;
            } else {
                result.add(two.get(pointerTwo));
                ++pointerTwo;
            }
        }
        while (pointerOne < one.size()) {
            result.add(one.get(pointerOne));
            ++pointerOne;
        }
        while (pointerTwo < two.size()) {
            result.add(two.get(pointerTwo));
            ++pointerTwo;
        }
        return result;
    }

    // Получает нужное количество релевантных запросов, диапазон [askL, askR) в котором их искать
    // Возвращает массив из <=k номеров строк которые подходят под запрос методом Дерева отрезков
    public ArrayList<Integer> askTopK(int k, int askL, int askR) {
        ArrayList<Integer> vertexes = new ArrayList<>();
        askMax(0, 0, size, askL, askR, vertexes);
        PriorityQueue<int[]> pq = new PriorityQueue<>
                ((second, first) -> (tree.get(first[0]).get(first[1])[0] - tree.get(second[0]).get(second[1])[0]));
        for (Integer vertex : vertexes) {
            pq.add(new int[]{vertex, 0});
        }
        ArrayList<Integer> result = new ArrayList<>();
        while (!pq.isEmpty() && result.size() < k) {
            int[] max = pq.poll();
            result.add(tree.get(max[0]).get(max[1])[1]);
            if (max[1] + 1 < tree.get(max[0]).size()) {
                pq.add(new int[]{max[0], max[1] + 1});
            }
        }
        return result;
    }

    // Получает текущую вершину, ее диазапоз [l, r), диапазон запроса [askL, askR), массив вершин
    // Заполняет массив вершин теми вершинами, которые полностью покрывают запрос
    private static void askMax(int v, int l, int r, int askL, int askR, ArrayList<Integer> vertexes) {
        if (l >= askR || r <= askL) {
            return;
        }
        if (l >= askL && r <= askR) {
            vertexes.add(v);
            return;
        }
        int m = (l + r) / 2;
        askMax(2 * v + 1, l, m, askL, askR, vertexes);
        askMax(2 * v + 2, m, r, askL, askR, vertexes);
    }
}
