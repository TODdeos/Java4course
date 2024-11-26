import java.util.Random;

public class Cognitron {
    private double[][][] filters; // Фильтры для каждого слоя [слой][фильтр][размер фильтра]
    private int numLayers; // Количество слоев
    private int numFilters; // Количество фильтров на слой
    private int filterSize; // Размер фильтра (например, 3x3)
    private double learningRate;

    public Cognitron(int numLayers, int numFilters, int filterSize, double learningRate) {
        this.numLayers = numLayers;
        this.numFilters = numFilters;
        this.filterSize = filterSize;
        this.learningRate = learningRate;

        // Инициализация фильтров случайными значениями
        filters = new double[numLayers][numFilters][filterSize * filterSize];
        Random rand = new Random();

        for (int l = 0; l < numLayers; l++) {
            for (int f = 0; f < numFilters; f++) {
                for (int i = 0; i < filterSize * filterSize; i++) {
                    filters[l][f][i] = rand.nextDouble() * 2 - 1; // от -1 до 1
                }
            }
        }
    }

    // Применение фильтра к входу (операция свертки)
    private double applyFilter(double[][] input, double[] filter, int x, int y) {
        double sum = 0;
        int halfSize = filterSize / 2;
        for (int i = -halfSize; i <= halfSize; i++) {
            for (int j = -halfSize; j <= halfSize; j++) {
                int ix = x + i;
                int iy = y + j;
                if (ix >= 0 && ix < input.length && iy >= 0 && iy < input[0].length) {
                    sum += input[ix][iy] * filter[(i + halfSize) * filterSize + (j + halfSize)];
                }
            }
        }
        return sum;
    }

    // Прямое распространение через слой
    private double[][] forward(double[][] input, int layer) {
        int size = input.length;
        double[][] output = new double[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double maxActivation = Double.NEGATIVE_INFINITY;
                for (int f = 0; f < numFilters; f++) {
                    double activation = applyFilter(input, filters[layer][f], x, y);
                    if (activation > maxActivation) {
                        maxActivation = activation;
                    }
                }
                output[x][y] = Math.max(0, maxActivation); // Активация ReLU
            }
        }
        return output;
    }

    // Обучение фильтров через самоорганизацию
    public void train(double[][][] inputs, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (double[][] input : inputs) {
                double[][] currentInput = input;
                for (int layer = 0; layer < numLayers; layer++) {
                    double[][] nextInput = new double[currentInput.length][currentInput[0].length];
                    for (int x = 0; x < currentInput.length; x++) {
                        for (int y = 0; y < currentInput[0].length; y++) {
                            for (int f = 0; f < numFilters; f++) {
                                double activation = applyFilter(currentInput, filters[layer][f], x, y);
                                if (activation > 0) { // Условие обучения
                                    for (int i = 0; i < filterSize * filterSize; i++) {
                                        filters[layer][f][i] += learningRate * currentInput[x][y];
                                    }
                                }
                            }
                        }
                    }
                    currentInput = forward(currentInput, layer);
                }
            }
        }
    }

    // Распознавание образа
    public double[][] recognize(double[][] input) {
        double[][] currentInput = input;
        for (int layer = 0; layer < numLayers; layer++) {
            currentInput = forward(currentInput, layer);
        }
        return currentInput;
    }

    public static void main(String[] args) {
        // Пример бинарных образов (10x10)
        double[][][] trainingData = {
            {
                {1, -1, -1, 1, /* ... */ -1},
                {-1, 1, 1, -1, /* ... */ 1},
                // Другие строки
            },
            // Добавьте другие примеры
        };

        // Создание и обучение когнитрона
        Cognitron cognitron = new Cognitron(3, 4, 3, 0.01);
        cognitron.train(trainingData, 100);

        // Тестирование когнитрона
        double[][] testInput = {
            {1, -1, -1, 1, /* ... */ -1},
            {-1, 1, 1, -1, /* ... */ 1},
            // Другие строки
        };

        double[][] result = cognitron.recognize(testInput);
        System.out.println("Распознанный образ:");
        for (double[] row : result) {
            System.out.println(Arrays.toString(row));
        }
    }
}
