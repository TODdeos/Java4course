public class Perceptron {
    private double[] weights;
    private double bias;
    private double learningRate;

    // Конструктор инициализации перцептрона
    public Perceptron(int inputSize, double learningRate) {
        this.weights = new double[inputSize];
        this.bias = 0;
        this.learningRate = learningRate;

        // Инициализация весов случайными значениями
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.random() * 2 - 1; // случайное число от -1 до 1
        }
    }

    // Метод активации
    private int activate(double sum) {
        return sum >= 0 ? 1 : -1; // Пороговая функция
    }

    // Метод для предсказания
    public int predict(int[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return activate(sum);
    }

    // Метод для обучения
    public void train(int[][] trainingData, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < trainingData.length; i++) {
                int prediction = predict(trainingData[i]);
                int error = labels[i] - prediction;

                // Обновление весов и смещения на основе ошибки
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * trainingData[i][j];
                }
                bias += learningRate * error;
            }
        }
    }

    public static void main(String[] args) {
        // Параметры
        int epochs = 100;
        double learningRate = 0.1;

        // Логическая функция И
        int[][] andData = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        int[] andLabels = { 1, -1, -1, -1 };

        Perceptron andPerceptron = new Perceptron(2, learningRate);
        andPerceptron.train(andData, andLabels, epochs);

        System.out.println("Таблица истинности И:");
        for (int[] data : andData) {
            System.out.println(data[0] + " И " + data[1] + " = " + andPerceptron.predict(data));
        }

        // Логическая функция ИЛИ
        int[][] orData = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        int[] orLabels = { 1, 1, 1, -1 };

        Perceptron orPerceptron = new Perceptron(2, learningRate);
        orPerceptron.train(orData, orLabels, epochs);

        System.out.println("Таблица истинности ИЛИ:");
        for (int[] data : orData) {
            System.out.println(data[0] + " ИЛИ " + data[1] + " = " + orPerceptron.predict(data));
        }

        // Логическая функция НЕ (только один вход)
        int[][] notData = { { 1 }, { -1 } };
        int[] notLabels = { -1, 1 };

        Perceptron notPerceptron = new Perceptron(1, learningRate);
        notPerceptron.train(notData, notLabels, epochs);

        System.out.println("Таблица истинности НЕ:");
        for (int[] data : notData) {
            System.out.println("НЕ " + data[0] + " = " + notPerceptron.predict(data));
        }
    }
}
