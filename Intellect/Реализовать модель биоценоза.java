abstract class Agent {
    protected double x, y; // Координаты в среде
    protected double energy; // Энергия агента
    protected Environment environment;

    public Agent(double x, double y, double initialEnergy, Environment environment) {
        this.x = x;
        this.y = y;
        this.energy = initialEnergy;
        this.environment = environment;
    }

    // Обновление состояния агента
    public abstract void update();

    // Потратить энергию
    protected void consumeEnergy(double amount) {
        energy -= amount;
        if (energy <= 0) {
            die();
        }
    }

    // Умереть
    protected void die() {
        environment.removeAgent(this);
    }
}

class Plant extends Agent {
    public Plant(double x, double y, Environment environment) {
        super(x, y, 0, environment); // У растений нет метаболизма
    }

    @Override
    public void update() {
        // Растение растет или остается пассивным
    }
}


class Herbivore extends Agent {
    private PerceptronController controller;

    public Herbivore(double x, double y, double initialEnergy, Environment environment) {
        super(x, y, initialEnergy, environment);
        this.controller = new PerceptronController(2, 2); // Пример: движение по x и y
    }

    @Override
    public void update() {
        double[] inputs = {x, y};
        double[] outputs = controller.predict(inputs);

        // Движение
        x += outputs[0];
        y += outputs[1];

        consumeEnergy(1); // Тратим энергию на движение

        // Питание, если находим растение
        Plant plant = environment.findPlantAt(x, y);
        if (plant != null) {
            energy += 10; // Съели растение
            environment.removeAgent(plant);
        }
    }
}

class Predator extends Agent {
    private PerceptronController controller;

    public Predator(double x, double y, double initialEnergy, Environment environment) {
        super(x, y, initialEnergy, environment);
        this.controller = new PerceptronController(2, 2);
    }

    @Override
    public void update() {
        double[] inputs = {x, y};
        double[] outputs = controller.predict(inputs);

        // Движение
        x += outputs[0];
        y += outputs[1];

        consumeEnergy(2); // Тратим энергию на движение

        // Охота на травоядных
        Herbivore herbivore = environment.findHerbivoreAt(x, y);
        if (herbivore != null) {
            energy += 20; // Съели травоядное
            environment.removeAgent(herbivore);
        }
    }
}


import java.util.ArrayList;
import java.util.List;

class Environment {
    private List<Agent> agents = new ArrayList<>();

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void removeAgent(Agent agent) {
        agents.remove(agent);
    }

    public Plant findPlantAt(double x, double y) {
        return agents.stream()
                .filter(a -> a instanceof Plant && a.x == x && a.y == y)
                .map(a -> (Plant) a)
                .findFirst()
                .orElse(null);
    }

    public Herbivore findHerbivoreAt(double x, double y) {
        return agents.stream()
                .filter(a -> a instanceof Herbivore && a.x == x && a.y == y)
                .map(a -> (Herbivore) a)
                .findFirst()
                .orElse(null);
    }

    public void update() {
        for (Agent agent : new ArrayList<>(agents)) {
            agent.update();
        }
    }
}


class PerceptronController {
    private double[] weights;
    private double bias;

    public PerceptronController(int inputSize, int outputSize) {
        this.weights = new double[inputSize];
        this.bias = Math.random();
        for (int i = 0; i < inputSize; i++) {
            weights[i] = Math.random() * 2 - 1; // [-1, 1]
        }
    }

    public double[] predict(double[] inputs) {
        double sum = bias;
        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }
        return new double[]{Math.tanh(sum), Math.tanh(sum)}; // Пример: одинаковые действия
    }
}


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Environment environment = new Environment();

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Инициализация среды
        for (int i = 0; i < 100; i++) {
            environment.addAgent(new Plant(Math.random() * 800, Math.random() * 800, environment));
        }
        for (int i = 0; i < 20; i++) {
            environment.addAgent(new Herbivore(Math.random() * 800, Math.random() * 800, 50, environment));
        }
        for (int i = 0; i < 5; i++) {
            environment.addAgent(new Predator(Math.random() * 800, Math.random() * 800, 100, environment));
        }

        new Thread(() -> {
            while (true) {
                environment.update();

                gc.clearRect(0, 0, 800, 800);
                for (Agent agent : environment.agents) {
                    if (agent instanceof Plant) {
                        gc.setFill(Color.GREEN);
                    } else if (agent instanceof Herbivore) {
                        gc.setFill(Color.BLUE);
                    } else if (agent instanceof Predator) {
                        gc.setFill(Color.RED);
                    }
                    gc.fillOval(agent.x, agent.y, 5, 5);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        primaryStage.setScene(new Scene(new javafx.scene.layout.StackPane(canvas)));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
