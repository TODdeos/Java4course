import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class GeneticPerceptron {

    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 1000;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;

    private static Random random = new Random();

    // Класс для представления индивидуума (набор весов перцептрона)
    static class Individual {
        double[] weights; // Веса перцептрона
        double fitness;

        Individual(int numWeights) {
            this.weights = new double[numWeights];
            for (int i = 0; i < numWeights; i++) {
                weights[i] = random.nextDouble() * 2 - 1; // Случайные веса [-1, 1]
            }
            this.fitness = 0.0;
        }
    }

    // Оценка фитнеса: правильная классификация данных
    private static double evaluateFitness(Individual individual, double[][] inputs, int[] targets) {
        int correct = 0;
        for (int i = 0; i < inputs.length; i++) {
            double output = predict(inputs[i], individual.weights);
            if ((output >= 0.5 ? 1 : 0) == targets[i]) {
                correct++;
            }
        }
        return (double) correct / inputs.length;
    }

    // Прямое распространение для однослойного перцептрона
    private static double predict(double[] input, double[] weights) {
        double sum = weights[0]; // Смещение (bias)
        for (int i = 0; i < input.length; i++) {
            sum += input[i] * weights[i + 1];
        }
        return sigmoid(sum);
    }

    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Селекция методом рулетки
    private static ArrayList<Individual> rouletteSelection(ArrayList<Individual> population) {
        ArrayList<Individual> selected = new ArrayList<>();
        double totalFitness = population.stream().mapToDouble(i -> i.fitness).sum();
        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
            double rand = random.nextDouble() * totalFitness;
            double cumulativeFitness = 0.0;
            for (Individual individual : population) {
                cumulativeFitness += individual.fitness;
                if (cumulativeFitness >= rand) {
                    selected.add(individual);
                    break;
                }
            }
        }
        return selected;
    }

    // Кроссовер (одна точка)
    private static Individual crossover(Individual parent1, Individual parent2) {
        Individual child = new Individual(parent1.weights.length);
        int crossoverPoint = random.nextInt(parent1.weights.length);
        for (int i = 0; i < parent1.weights.length; i++) {
            child.weights[i] = (i < crossoverPoint) ? parent1.weights[i] : parent2.weights[i];
        }
        return child;
    }

    // Мутация
    private static void mutate(Individual individual) {
        for (int i = 0; i < individual.weights.length; i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                individual.weights[i] += random.nextGaussian() * 0.1; // Случайная мутация
            }
        }
    }

    // Генетический алгоритм для обучения перцептрона
    private static Individual runGA(double[][] inputs, int[] targets, int numWeights) {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new Individual(numWeights));
        }

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Оценка фитнеса
            for (Individual individual : population) {
                individual.fitness = evaluateFitness(individual, inputs, targets);
            }

            // Селекция
            ArrayList<Individual> selected = rouletteSelection(population);

            // Создание нового поколения
            ArrayList<Individual> newGeneration = new ArrayList<>(selected);
            while (newGeneration.size() < POPULATION_SIZE) {
                Individual parent1 = selected.get(random.nextInt(selected.size()));
                Individual parent2 = selected.get(random.nextInt(selected.size()));
                Individual child = crossover(parent1, parent2);
                mutate(child);
                newGeneration.add(child);
            }

            population = newGeneration;

            // Лучшая особь текущего поколения
            Individual best = population.stream().max(Comparator.comparingDouble(i -> i.fitness)).orElse(null);
            if (best != null && best.fitness == 1.0) {
                return best; // Ранний выход, если достигнута максимальная точность
            }
        }

        // Вернуть лучшую особь
        return population.stream().max(Comparator.comparingDouble(i -> i.fitness)).orElse(null);
    }

    public static void main(String[] args) {
        // Пример данных для обучения: XOR
        double[][] inputs = {
            {0, 0}, {0, 1}, {1, 0}, {1, 1}
        };
        int[] targets = {0, 1, 1, 0};

        // Для однослойного перцептрона: число весов = число входов + 1 (bias)
        int numWeights = inputs[0].length + 1;

        Individual bestIndividual = runGA(inputs, targets, numWeights);
        System.out.println("Лучшие веса: " + Arrays.toString(bestIndividual.weights));
        System.out.println("Точность: " + bestIndividual.fitness);
    }
}
