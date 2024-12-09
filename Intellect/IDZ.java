import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 1000;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;

    private static Random random = new Random();

    // Класс для хранения особей
    static class Individual {
        double x, y;
        double fitness;

        Individual(double x, double y) {
            this.x = x;
            this.y = y;
            this.fitness = evaluateFitness(x, y);
        }

        static double evaluateFitness(double x, double y) {
            return 1.0 / (1 + x * x + y * y);
        }
    }

    // Инициализация популяции
    private static ArrayList<Individual> initializePopulation() {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double x = random.nextDouble() * 10 - 5; // Диапазон [-5, 5]
            double y = random.nextDouble() * 10 - 5;
            population.add(new Individual(x, y));
        }
        return population;
    }

    // Селекция методом элит
    private static ArrayList<Individual> elitSelection(ArrayList<Individual> population) {
        population.sort(Comparator.comparingDouble(i -> -i.fitness)); // Сортировка по убыванию фитнеса
        return new ArrayList<>(population.subList(0, POPULATION_SIZE / 2)); // Сохраняем половину лучших
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
        if (random.nextDouble() < CROSSOVER_RATE) {
            double x = (parent1.x + parent2.x) / 2;
            double y = (parent1.y + parent2.y) / 2;
            return new Individual(x, y);
        }
        return random.nextBoolean() ? new Individual(parent1.x, parent1.y) : new Individual(parent2.x, parent2.y);
    }

    // Мутация
    private static void mutate(Individual individual) {
        if (random.nextDouble() < MUTATION_RATE) {
            individual.x += random.nextGaussian();
            individual.y += random.nextGaussian();
            individual.fitness = Individual.evaluateFitness(individual.x, individual.y);
        }
    }

    // Генетический алгоритм
    private static Individual run(boolean useElitSelection) {
        ArrayList<Individual> population = initializePopulation();

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Селекция
            ArrayList<Individual> selected = useElitSelection ? elitSelection(population) : rouletteSelection(population);

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
        }

        // Найти лучшую особь
        return population.stream().max(Comparator.comparingDouble(i -> i.fitness)).orElse(null);
    }

    public static void main(String[] args) {
        // Метод элит
        Individual bestElit = run(true);
        System.out.printf("Метод элит: x=%.5f, y=%.5f, f(x, y)=%.5f%n", bestElit.x, bestElit.y, bestElit.fitness);

        // Метод рулетки
        Individual bestRoulette = run(false);
        System.out.printf("Метод рулетки: x=%.5f, y=%.5f, f(x, y)=%.5f%n", bestRoulette.x, bestRoulette.y, bestRoulette.fitness);
    }
}
