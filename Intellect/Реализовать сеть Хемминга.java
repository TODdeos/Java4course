import java.util.Arrays;

public class HammingNetwork {
    private double[][] weights; // Матрица весов для первого слоя
    private double[] biases;    // Смещение для первого слоя
    private int numPatterns;    // Количество эталонных образов
    private int inputSize;      // Размер входного вектора

    public HammingNetwork(int[][] patterns) {
        this.numPatterns = patterns.length;
        this.inputSize = patterns[0].length;
        this.weights = new double[numPatterns][inputSize];
        this.biases = new double[numPatterns];

        // Инициализация весов и смещений
        for (int i = 0; i < numPatterns; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights[i][j] = patterns[i][j];
            }
            biases[i] = inputSize / 2.0; // Смещение для устранения отрицательных значений
        }
    }

    // Метод классификации входного вектора
    public int classify(int[] input) {
        // Первый слой: вычисление расстояний до эталонов
        double[] outputs = new double[numPatterns];
        for (int i = 0; i < numPatterns; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weights[i][j];
            }
            outputs[i] = sum;
        }

        // Второй слой: соревновательный выбор
        int winner = 0;
        for (int i = 1; i < numPatterns; i++) {
            if (outputs[i] > outputs[winner]) {
                winner = i;
            }
        }

        return winner;
    }

    public static void main(String[] args) {
        // Эталонные образы (например, 10x10 цифры, упрощенные до 1D массивов)
        int[][] patterns = {
            {1, -1, 1, -1, 1, -1, 1, -1, 1, -1, /* другие пиксели */ -1, 1, -1},
            {-1, 1, -1, 1, -1, 1, -1, 1, -1, 1, /* другие пиксели */ 1, -1, 1},
            // Добавьте эталоны для других классов
        };

        // Инициализация сети Хемминга
        HammingNetwork hammingNetwork = new HammingNetwork(patterns);

        // Поврежденный образ, предварительно восстановленный сетью Хопфилда
        int[] restoredPattern = {1, -1, 1, -1, 1, -1, 1, -1, 1, -1, /* другие пиксели */ -1, 1, -1};

        // Классификация образа
        int result = hammingNetwork.classify(restoredPattern);
        System.out.println("Распознанный класс: " + result);
    }
}
