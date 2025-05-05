import java.util.*;
import java.util.stream.IntStream;

public class AntColonyOptimization {
    private static final Random random = new Random();
    
    // Параметры алгоритма
    private static final int NUM_ANTS = 50;
    private static final int NUM_ITERATIONS = 200;
    private static final double ALPHA = 1.0; // Вес феромона
    private static final double BETA = 5.0;  // Вес видимости (обратное расстояние)
    private static final double RHO = 0.1;   // Коэффициент испарения феромона
    private static final double Q = 100.0;   // Количество откладываемого феромона
    
    private int numCities;
    private double[][] distances;
    private double[][] pheromones;
    private List<Ant> ants;
    private int[] bestTour;
    private double bestTourLength = Double.MAX_VALUE;
    
    public static void main(String[] args) {
        // Создаем случайные координаты городов
        int numCities = 20;
        double[][] cities = generateRandomCities(numCities, 100, 100);
        
        // Вычисляем матрицу расстояний
        double[][] distances = calculateDistances(cities);
        
        // Запускаем алгоритм
        AntColonyOptimization aco = new AntColonyOptimization(distances);
        aco.solve();
        
        // Выводим результаты
        System.out.println("Лучший маршрут: " + Arrays.toString(aco.getBestTour()));
        System.out.println("Длина маршрута: " + aco.getBestTourLength()));
    }
    
    public AntColonyOptimization(double[][] distances) {
        this.distances = distances;
        this.numCities = distances.length;
        this.pheromones = new double[numCities][numCities];
        initializePheromones();
        initializeAnts();
    }
    
    private static double[][] generateRandomCities(int numCities, int maxX, int maxY) {
        double[][] cities = new double[numCities][2];
        for (int i = 0; i < numCities; i++) {
            cities[i][0] = random.nextInt(maxX);
            cities[i][1] = random.nextInt(maxY);
        }
        return cities;
    }
    
    private static double[][] calculateDistances(double[][] cities) {
        int numCities = cities.length;
        double[][] distances = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    double dx = cities[i][0] - cities[j][0];
                    double dy = cities[i][1] - cities[j][1];
                    distances[i][j] = Math.sqrt(dx*dx + dy*dy);
                } else {
                    distances[i][j] = 0.0;
                }
            }
        }
        return distances;
    }
    
    private void initializePheromones() {
        double initialPheromone = 1.0 / numCities;
        for (int i = 0; i < numCities; i++) {
            Arrays.fill(pheromones[i], initialPheromone);
        }
    }
    
    private void initializeAnts() {
        ants = new ArrayList<>();
        for (int i = 0; i < NUM_ANTS; i++) {
            ants.add(new Ant(numCities));
        }
    }
    
    public void solve() {
        for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
            // Каждый муравей строит маршрут
            for (Ant ant : ants) {
                ant.buildTour(distances, pheromones, ALPHA, BETA);
            }
            
            // Обновляем феромоны
            updatePheromones();
            
            // Проверяем на лучший маршрут
            for (Ant ant : ants) {
                if (ant.getTourLength() < bestTourLength) {
                    bestTourLength = ant.getTourLength();
                    bestTour = ant.getTour().clone();
                }
            }
            
            // Выводим информацию о текущей итерации
            if (iter % 10 == 0) {
                System.out.printf("Итерация %d: Лучшая длина = %.2f%n", iter, bestTourLength);
            }
            
            // Готовим муравьев к следующей итерации
            for (Ant ant : ants) {
                ant.clear();
            }
        }
    }
    
    private void updatePheromones() {
        // Испарение феромона
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] *= (1.0 - RHO);
            }
        }
        
        // Добавление нового феромона
        for (Ant ant : ants) {
            double contribution = Q / ant.getTourLength();
            int[] tour = ant.getTour();
            for (int i = 0; i < numCities - 1; i++) {
                int from = tour[i];
                int to = tour[i + 1];
                pheromones[from][to] += contribution;
                pheromones[to][from] += contribution;
            }
            // Замыкаем цикл (возвращаемся в начальный город)
            int last = tour[numCities - 1];
            int first = tour[0];
            pheromones[last][first] += contribution;
            pheromones[first][last] += contribution;
        }
    }
    
    public int[] getBestTour() {
        return bestTour;
    }
    
    public double getBestTourLength() {
        return bestTourLength;
    }
    
    private static class Ant {
        private int[] tour;
        private boolean[] visited;
        private int currentIndex;
        private double tourLength;
        
        public Ant(int numCities) {
            this.tour = new int[numCities];
            this.visited = new boolean[numCities];
            this.currentIndex = 0;
            this.tourLength = 0.0;
        }
        
        public void buildTour(double[][] distances, double[][] pheromones, double alpha, double beta) {
            // Начинаем со случайного города
            int startCity = random.nextInt(tour.length);
            visitCity(startCity);
            
            // Посещаем остальные города
            while (currentIndex < tour.length) {
                int nextCity = selectNextCity(distances, pheromones, alpha, beta);
                visitCity(nextCity);
            }
            
            // Вычисляем длину маршрута (включая возврат в начальный город)
            tourLength += distances[tour[tour.length - 1]][tour[0]];
        }
        
        private int selectNextCity(double[][] distances, double[][] pheromones, double alpha, double beta) {
            int currentCity = tour[currentIndex - 1];
            
            // Вычисляем вероятности для всех непосещенных городов
            double[] probabilities = new double[tour.length];
            double total = 0.0;
            
            for (int i = 0; i < tour.length; i++) {
                if (!visited[i]) {
                    double pheromone = Math.pow(pheromones[currentCity][i], alpha);
                    double visibility = Math.pow(1.0 / distances[currentCity][i], beta);
                    probabilities[i] = pheromone * visibility;
                    total += probabilities[i];
                }
            }
            
            // Выбираем следующий город методом рулетки
            double rand = random.nextDouble() * total;
            double sum = 0.0;
            for (int i = 0; i < tour.length; i++) {
                if (!visited[i]) {
                    sum += probabilities[i];
                    if (sum >= rand) {
                        return i;
                    }
                }
            }
            
            // Если что-то пошло не так (не должно происходить)
            return -1;
        }
        
        private void visitCity(int city) {
            if (currentIndex > 0) {
                int prevCity = tour[currentIndex - 1];
                tourLength += distances[prevCity][city];
            }
            tour[currentIndex++] = city;
            visited[city] = true;
        }
        
        public void clear() {
            Arrays.fill(visited, false);
            currentIndex = 0;
            tourLength = 0.0;
        }
        
        public int[] getTour() {
            return tour;
        }
        
        public double getTourLength() {
            return tourLength;
        }
    }
}
