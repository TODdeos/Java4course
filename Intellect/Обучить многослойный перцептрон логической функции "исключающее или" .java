import java.util.Random;

public class XORNeuralNetwork {
    private double[][] hiddenWeights;  // Веса между входным и скрытым слоем
    private double[] outputWeights;    // Веса между скрытым и выходным слоем
    private double[] hiddenBias;       // Смещение скрытого слоя
    private double outputBias;         // Смещение выходного слоя
    private double learningRate;

    public XORNeuralNetwork(double learningRate) {
        this.learningRate = learningRate;

        // Инициализация весов и смещений случайными значениями
        Random rand = new Random();
        hiddenWeights = new double[2][2];
        outputWeights = new double[2];
        hiddenBias = new double[2];
        outputBias = rand.nextDouble() * 2 - 1;

        for (int i = 0; i < 2; i++) {
            hiddenBias[i] = rand.nextDouble() * 2 - 1;
            outputWeights[i] = rand.nextDouble() * 2 - 1;
            for (int j = 0; j < 2; j++) {
                hiddenWeights[i][j] = rand.nextDouble() * 2 - 1;
            }
        }
    }

    // Сигмоида как активационная функция
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Производная сигмоиды
    private double sigmoidDerivative(double x) {
        return x * (1.0 - x);
    }

    // Прогноз на основе входов
    public double predict(int[] inputs) {
        // Прямое распространение (forward propagation)
        double[] hiddenLayerOutput = new double[2];
        for (int i = 0; i < 2; i++) {
            hiddenLayerOutput[i] = sigmoid(inputs[0] * hiddenWeights[0][i] + inputs[1] * hiddenWeights[1][i] + hiddenBias[i]);
        }
        double output = sigmoid(hiddenLayerOutput[0] * outputWeights[0] + hiddenLayerOutput[1] * outputWeights[1] + outputBias);
        return output;
    }

    // Метод обучения
    public void train(int[][] inputs, int[] outputs, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputs.length; i++) {
                // Прямое распространение
                double[] hiddenLayerOutput = new double[2];
                for (int j = 0; j < 2; j++) {
                    hiddenLayerOutput[j] = sigmoid(inputs[i][0] * hiddenWeights[0][j] + inputs[i][1] * hiddenWeights[1][j] + hiddenBias[j]);
                }
                double predictedOutput = sigmoid(hiddenLayerOutput[0] * outputWeights[0] + hiddenLayerOutput[1] * outputWeights[1] + outputBias);

                // Вычисление ошибок
                double outputError = outputs[i] - predictedOutput;
                double outputDelta = outputError * sigmoidDerivative(predictedOutput);

                double[] hiddenLayerError = new double[2];
                double[] hiddenLayerDelta = new double[2];
                for (int j = 0; j < 2; j++) {
                    hiddenLayerError[j] = outputDelta * outputWeights[j];
                    hiddenLayerDelta[j] = hiddenLayerError[j] * sigmoidDerivative(hiddenLayerOutput[j]);
                }

                // Обновление весов и смещений
                for (int j = 0; j < 2; j++) {
                    outputWeights[j] += learningRate * outputDelta * hiddenLayerOutput[j];
                }
                outputBias += learningRate * outputDelta;

                for (int j = 0; j < 2; j++) {
                    hiddenWeights[0][j] += learningRate * hiddenLayerDelta[j] * inputs[i][0];
                    hiddenWeights[1][j] += learningRate * hiddenLayerDelta[j] * inputs[i][1];
                    hiddenBias[j] += learningRate * hiddenLayerDelta[j];
                }
            }
        }
    }

    public static void main(String[] args) {
        // Определение набора данных XOR
        int[][] inputs = { {0, 0}, {0, 1}, {1, 0}, {1, 1} };
        int[] outputs = {0, 1, 1, 0};

        // Создание и обучение сети
        XORNeuralNetwork xorNN = new XORNeuralNetwork(0.5);
        xorNN.train(inputs, outputs, 10000);

        // Тестирование сети
        System.out.println("Таблица истинности для XOR:");
        for (int[] input : inputs) {
            System.out.printf("%d XOR %d = %.4f%n", input[0], input[1], xorNN.predict(input));
        }
    }
}

