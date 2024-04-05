import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TempReadingModule {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int numHours = 24;
        int numThreads = 8;
        int frequency = 60;
        int topNReadings = 5;
        int[] temperatures = new int[numThreads * frequency];

        MarsRover rover = new MarsRover(frequency, temperatures, numHours);
        for (int hours = 0; hours < numHours; hours++) {

            Thread sensors[] = new Thread[numThreads];
            for (int i = 0; i < sensors.length; i++) {
                sensors[i] = new Thread(rover);
            }

            for (int i = 0; i < numThreads; i++) {
                sensors[i].start();
                try {
                    sensors[i].join();
                } catch (InterruptedException e) {
                }
            }

            rover.counter.set(0);

            Report report = new Report(temperatures, hours, numThreads, topNReadings);
            report.printReport();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime) + "ms");
    }
}

class MarsRover implements Runnable {
    int freq;
    int[] temperatures;
    int numHours;
    AtomicInteger counter;

    public MarsRover(int freq, int[] temperatures, int numHours) {
        this.freq = freq;
        this.temperatures = temperatures;
        this.numHours = numHours;
        counter = new AtomicInteger();
    }

    public void run() {
        for (int i = 0; i < freq; i++) {
            int temp = getRandomTemp();
            temperatures[counter.getAndIncrement()] = temp;
        }
    }

    public int getRandomTemp() {
        Random rand = new Random();

        return rand.nextInt(-100, 70);
    }
}

class Report {

    int numThreads;
    int hour;
    int[] temperatures;
    int[] lows;
    int[] highs;
    int largestDiffFrom;
    int largestDiffTo;
    int largestDiff;
    int largestDiffFirst;
    int largestDiffSecond;
    int topNReadings;

    public Report(int[] temperatures, int hour, int numThreads, int topNReadings) {

        this.hour = hour;
        this.numThreads = numThreads;
        this.temperatures = temperatures;
        this.topNReadings = topNReadings;

        largestDiff = 0;

        for (int i = 0; i < temperatures.length; i++) {
            for (int j = i; j < temperatures.length && j < i + 10; j++) {
                int firstReading = temperatures[i];
                int secondReading = temperatures[j];
                int diff = firstReading - secondReading;
                if (Math.abs(diff) > largestDiff) {
                    largestDiffFrom = i % 60;
                    largestDiffTo = j % 60;
                    largestDiff = Math.abs(diff);
                    largestDiffFirst = firstReading;
                    largestDiffSecond = secondReading;
                }
            }
        }

        Arrays.sort(temperatures);
        int len = temperatures.length;

        lows = new int[topNReadings];
        for (int i = 0; i < topNReadings; i++) {
            lows[i] = temperatures[i];
        }

        highs = new int[topNReadings];
        for (int i = 0; i < topNReadings; i++) {
            highs[i] = temperatures[len - i - 1];
        }

    }

    public void printReport() {
        System.out.println("Report for hour " + hour + ":");
        System.out.println("The top " + topNReadings + " highest temperatures:");
        for (int i = 0; i < highs.length; i++) {
            System.out.println(highs[i]);
        }
        System.out.println("The top " + topNReadings + " lowest temperatures:");
        for (int i = 0; i < lows.length; i++) {
            System.out.println(lows[i] + " ");
        }
        System.out.println("The largest temperature difference was " + largestDiff + " from " + largestDiffFirst
                + " to " + largestDiffSecond);
        System.out.println("Occurring between minutes " + largestDiffFrom + " and " + largestDiffTo + "\n");

    }

}