import java.util.Arrays;

public class HopfieldNetwork {
    private int[][] weights; // Матрица весов (100x100)
    private int size; // Количество нейронов

    public HopfieldNetwork(int size) {
        this.size = size;
        this.weights = new int[size][size];
    }

    // Обучение сети на наборе паттернов
    public void train(int[][] patterns) {
        for (int[] pattern : patterns) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        weights[i][j] += pattern[i] * pattern[j];
                    }
                }
            }
        }
    }

    // Восстановление поврежденного паттерна
    public int[] recover(int[] input, int maxIterations) {
        int[] state = Arrays.copyOf(input, input.length);
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < size; i++) {
                int sum = 0;
                for (int j = 0; j < size; j++) {
                    sum += weights[i][j] * state[j];
                }
                state[i] = sum >= 0 ? 1 : -1; // Пороговая активация
            }
        }
        return state;
    }

    public static void main(String[] args) {
        // Пример паттернов (10x10 изображений цифр, упрощенные до массивов из +1 и -1)
        int[][] patterns = {
            {1, 1, -1, -1, 1, -1, -1, 1, 1, 1, /* ... другие пиксели */ -1, -1, -1},
            {1, -1, 1, -1, 1, 1, -1, 1, -1, 1, /* ... другие пиксели */ 1, 1, -1},
            // Добавьте паттерны для других цифр
        };

        // Создаем сеть Хопфилда и обучаем ее
        HopfieldNetwork hopfield = new HopfieldNetwork(100);
        hopfield.train(patterns);

        // Поврежденный паттерн (например, цифра с шумом)
        int[] damagedPattern = {1, 1, -1, -1, 1, -1, -1, -1, 1, 1, /* ... другие пиксели */ -1, -1, 1};

        // Восстанавливаем паттерн
        int[] recoveredPattern = hopfield.recover(damagedPattern, 100);

        // Вывод результата
        System.out.println("Поврежденный паттерн: " + Arrays.toString(damagedPattern));
        System.out.println("Восстановленный паттерн: " + Arrays.toString(recoveredPattern));
    }
}

