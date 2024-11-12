import java.util.Arrays;

public class SingleLayerPerceptron {
    private double[][] weights; // 100 входов x 10 выходов
    private double[] bias; // Смещение для каждого выхода
    private double learningRate;

    // Конструктор инициализации перцептрона
    public SingleLayerPerceptron(int inputSize, int outputSize, double learningRate) {
        this.weights = new double[inputSize][outputSize];
        this.bias = new double[outputSize];
        this.learningRate = learningRate;

        // Инициализация весов случайными значениями
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weights[i][j] = Math.random() * 2 - 1; // случайное число от -1 до 1
            }
        }
        Arrays.fill(bias, 0);
    }

    // Метод для предсказания (выбираем индекс с максимальным значением)
    public int predict(int[] inputs) {
        double[] output = new double[bias.length];
        
        // Вычисляем взвешенную сумму для каждого выходного нейрона
        for (int j = 0; j < bias.length; j++) {
            output[j] = bias[j];
            for (int i = 0; i < inputs.length; i++) {
                output[j] += inputs[i] * weights[i][j];
            }
        }

        // Находим индекс с максимальным значением (предсказанная цифра)
        int predictedDigit = 0;
        for (int j = 1; j < output.length; j++) {
            if (output[j] > output[predictedDigit]) {
                predictedDigit = j;
            }
        }
        return predictedDigit;
    }

    // Метод для обучения перцептрона
    public void train(int[][] trainingData, int[][] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < trainingData.length; i++) {
                int[] inputs = trainingData[i];
                int[] expectedOutput = labels[i];

                // Прогнозируем на основе текущих весов
                double[] output = new double[bias.length];
                for (int j = 0; j < bias.length; j++) {
                    output[j] = bias[j];
                    for (int k = 0; k < inputs.length; k++) {
                        output[j] += inputs[k] * weights[k][j];
                    }
                }

                // Обновление весов по правилу обучения перцептрона
                for (int j = 0; j < bias.length; j++) {
                    double error = expectedOutput[j] - (output[j] >= 0 ? 1 : 0);
                    for (int k = 0; k < inputs.length; k++) {
                        weights[k][j] += learningRate * error * inputs[k];
                    }
                    bias[j] += learningRate * error;
                }
            }
        }
    }

    public static void main(String[] args) {
        // Параметры
        int epochs = 100;
        double learningRate = 0.1;
        int inputSize = 100; // 10x10 пикселей
        int outputSize = 10; // Классы от 0 до 9

        // Инициализируем однослойный перцептрон
        SingleLayerPerceptron perceptron = new SingleLayerPerceptron(inputSize, outputSize, learningRate);

        // Заглушка данных для примера (должны быть реальные данные, представляющие цифры)
        int[][] trainingData = new int[10][inputSize]; // Например, 10 примеров (по одному на каждую цифру)
        int[][] labels = new int[10][outputSize];
        
        // Заполните trainingData и labels вашими примерами данных

        // Обучение перцептрона
        perceptron.train(trainingData, labels, epochs);

        // Проверка предсказания
        int[] testSample = new int[inputSize]; // Образец, который необходимо проверить
        System.out.println("Предсказанная цифра: " + perceptron.predict(testSample));
    }
}
