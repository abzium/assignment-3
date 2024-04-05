import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TempReadingModule {
    public static void main(String[] args) {
        int numHours = 5;
        int numThreads = 8;
        int frequency = 60;
        int topNReadings = 5;
        int[] shared = new int[numThreads * frequency];

        MarsRover rover = new MarsRover(frequency, shared, numHours);
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

            Report report = new Report(shared, hours, numThreads, topNReadings);
            report.printReport();
        }
    }
}

class MarsRover implements Runnable {
    int freq;
    int[] shared;
    int numHours;
    AtomicInteger counter;

    public MarsRover(int freq, int[] shared, int numHours) {
        this.freq = freq;
        this.shared = shared;
        this.numHours = numHours;
        counter = new AtomicInteger();
    }

    public void run() {
        for (int i = 0; i < freq; i++) {
            int temp = getRandomTemp();
            shared[counter.getAndIncrement()] = temp;
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
    int[] shared;
    int[] lows;
    int[] highs;
    int largestDiffFrom;
    int largestDiffTo;
    int largestDiff;
    int largestDiffFirst;
    int largestDiffSecond;
    int topNReadings;

    public Report(int[] shared, int hour, int numThreads, int topNReadings) {

        this.hour = hour;
        this.numThreads = numThreads;
        this.shared = shared;
        this.topNReadings = topNReadings;

        largestDiff = 0;

        for (int i = 0; i < shared.length; i++) {
            for (int j = i; j < shared.length && j < i + 10; j++) {
                int firstReading = shared[i];
                int secondReading = shared[j];
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

        Arrays.sort(shared);
        int len = shared.length;

        lows = new int[topNReadings];
        for (int i = 0; i < topNReadings; i++) {
            lows[i] = shared[i];
        }

        highs = new int[topNReadings];
        for (int i = 0; i < topNReadings; i++) {
            highs[i] = shared[len - i - 1];
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