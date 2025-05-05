import java.util.Random;

public class SimulatedAnnealing {
    
    // Целевая функция, которую мы хотим максимизировать
    public static double targetFunction(double x, double y) {
        return 1.0 / (1.0 + x*x + y*y);
    }
    
    public static void main(String[] args) {
        // Параметры алгоритма
        double initialTemp = 1000.0;
        double coolingRate = 0.003;
        double minTemp = 1.0;
        int maxIterations = 10000;
        
        // Генератор случайных чисел
        Random random = new Random();
        
        // Начальное решение (случайная точка в диапазоне [-10, 10])
        double currentX = -10 + 20 * random.nextDouble();
        double currentY = -10 + 20 * random.nextDouble();
        double currentEnergy = targetFunction(currentX, currentY);
        
        // Лучшее найденное решение
        double bestX = currentX;
        double bestY = currentY;
        double bestEnergy = currentEnergy;
        
        double temp = initialTemp;
        
        for (int i = 0; i < maxIterations && temp > minTemp; i++) {
            // Генерация нового соседнего решения
            double newX = currentX + (random.nextDouble() - 0.5) * temp/10;
            double newY = currentY + (random.nextDouble() - 0.5) * temp/10;
            double newEnergy = targetFunction(newX, newY);
            
            // Разница энергий (мы максимизируем функцию)
            double deltaEnergy = newEnergy - currentEnergy;
            
            // Если новое решение лучше, принимаем его
            // Если хуже, принимаем с некоторой вероятностью
            if (deltaEnergy > 0 || 
                random.nextDouble() < Math.exp(deltaEnergy / temp)) {
                currentX = newX;
                currentY = newY;
                currentEnergy = newEnergy;
            }
            
            // Обновляем лучшее решение
            if (currentEnergy > bestEnergy) {
                bestX = currentX;
                bestY = currentY;
                bestEnergy = currentEnergy;
            }
            
            // Охлаждение
            temp *= 1 - coolingRate;
            
            // Вывод прогресса
            if (i % 100 == 0) {
                System.out.printf("Iteration: %d, Temp: %.2f, Current: (%.4f, %.4f) = %.6f, Best: (%.4f, %.4f) = %.6f%n",
                                 i, temp, currentX, currentY, currentEnergy, bestX, bestY, bestEnergy);
            }
        }
        
        System.out.println("\nOptimization completed!");
        System.out.printf("Best solution found: (%.4f, %.4f)%n", bestX, bestY);
        System.out.printf("Maximum value: %.6f%n", bestEnergy);
    }
}
