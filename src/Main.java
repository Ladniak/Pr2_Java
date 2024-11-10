import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        int minValue = 0;
        int maxValue = 1000;
        int arraySize = new Random().nextInt(21) + 40;
        int partSize = 10;

        // Генерація масиву випадкових чисел
        int[] array = new Random().ints(arraySize, minValue, maxValue + 1).toArray();

        // Використання ExecutorService для керування потоками
        ExecutorService executor = Executors.newFixedThreadPool(arraySize / partSize);
        List<Future<Double>> futures = new ArrayList<>();
        Set<Double> uniqueAverages = new CopyOnWriteArraySet<>();

        long startTime = System.currentTimeMillis();

        // Розбиття масиву на частини та запуск обробки кожної частини в окремому потоці
        for (int i = 0; i < array.length; i += partSize) {
            int start = i;
            int end = Math.min(i + partSize, array.length);

            // Завдання для обчислення середнього значення частини масиву
            Callable<Double> task = () -> {
                int sum = 0;
                for (int j = start; j < end; j++) {
                    sum += array[j];
                }
                double average = sum / (double) (end - start);
                uniqueAverages.add(average);
                return average;
            };


            futures.add(executor.submit(task));
        }

        // Отримання результатів і перевірка станів завдань
        for (Future<Double> future : futures) {
            try {
                if (!future.isCancelled()) {
                    double result = future.get();
                    System.out.println("Середнє значення частини масиву: " + result);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Завдання було перервано або не завершилося.");
            }
        }

        long endTime = System.currentTimeMillis();
        executor.shutdown();


        System.out.println("Час виконання програми: " + (endTime - startTime) + " мс");


        System.out.println("Унікальні середні значення: " + uniqueAverages);
    }
}
