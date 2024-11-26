import java.util.Arrays;
import java.util.Random;

public class CounterPropagationNetwork {
    private double[][] kohonenWeights; // Веса для слоя Кохонена (inputSize x numClusters)
    private double[][] grossbergWeights; // Веса для слоя Гроссберга (numClusters x outputSize)
    private int inputSize;
    private int numClusters;
    private int outputSize;
    private double learningRate;

    public CounterPropagationNetwork(int inputSize, int numClusters, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.numClusters = numClusters;
        this.outputSize = outputSize;
        this.learningRate = learningRate;

        // Инициализация весов случайными значениями
        Random rand = new Random();
        this.kohonenWeights = new double[inputSize][numClusters];
        this.grossbergWeights = new double[numClusters][outputSize];

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < numClusters; j++) {
                kohonenWeights[i][j] = rand.nextDouble();
            }
        }

        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < outputSize; j++) {
                grossbergWeights[i][j] = rand.nextDouble();
            }
        }
    }

    // Нахождение победителя в слое Кохонена
    private int findWinner(double[] input) {
        int winner = 0;
        double maxDotProduct = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < numClusters; i++) {
            double dotProduct = 0;
            for (int j = 0; j < inputSize; j++) {
                dotProduct += input[j] * kohonenWeights[j][i];
            }
            if (dotProduct > maxDotProduct) {
                maxDotProduct = dotProduct;
                winner = i;
            }
        }
        return winner;
    }

    // Обучение CPN
    public void train(double[][] inputs, double[][] targets, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int sample = 0; sample < inputs.length; sample++) {
                double[] input = inputs[sample];
                double[] target = targets[sample];

                // Найти победителя в слое Кохонена
                int winner = findWinner(input);

                // Обновить веса слоя Кохонена
                for (int i = 0; i < inputSize; i++) {
                    kohonenWeights[i][winner] += learningRate * (input[i] - kohonenWeights[i][winner]);
                }

                // Обновить веса слоя Гроссберга
                for (int i = 0; i < outputSize; i++) {
                    grossbergWeights[winner][i] += learningRate * (target[i] - grossbergWeights[winner][i]);
                }
            }
        }
    }

    // Прогнозирование выхода
    public double[] predict(double[] input) {
        int winner = findWinner(input);
        return grossbergWeights[winner];
    }

    public static void main(String[] args) {
        // Параметры сети
        int inputSize = 100; // Размер входного вектора (10x10 изображение)
        int numClusters = 10; // Число кластеров (10 цифр)
        int outputSize = 10; // Число выходных классов (цифры 0-9)
        double learningRate = 0.1;

        // Пример данных (упрощенные образы цифр)
        double[][] inputs = {
            {1, -1, 1, -1, /* ... другие пиксели */ 1, -1},
            {1, 1, -1, -1, /* ... другие пиксели */ -1, 1},
            // Добавить еще примеры
        };

        // Целевые метки
        double[][] targets = {
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Цифра 0
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, // Цифра 1
            // Добавить еще метки
        };

        // Создание и обучение сети CPN
        CounterPropagationNetwork cpn = new CounterPropagationNetwork(inputSize, numClusters, outputSize, learningRate);
        cpn.train(inputs, targets, 1000);

        // Тестирование сети
        double[] testInput = {1, -1, 1, -1, /* ... другие пиксели */ 1, -1};
        double[] output = cpn.predict(testInput);

        System.out.println("Распознанный выход: " + Arrays.toString(output));
    }
}
