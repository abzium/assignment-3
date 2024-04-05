import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class MinotaurPresents {

    public static void main(String[] args) {

        int numPresents = 500000;
        int numServants = 4;

        LockFreeList presentsChain = new LockFreeList();
        AtomicInteger addCounter = new AtomicInteger(0);
        AtomicInteger removeCounter = new AtomicInteger(0);
        ArrayList<Integer> presentsBag = new ArrayList<>();

        for (int i = 0; i < numPresents; i++) {
            presentsBag.add(i);
        }
        Collections.shuffle(presentsBag);

        Thread servants[] = new Thread[numServants];
        for (int i = 0; i < numServants; i++) {
            servants[i] = new Thread(new Servant(presentsChain, presentsBag, addCounter, removeCounter));
        }
        for (int i = 0; i < numServants; i++) {
            servants[i].start();
        }
        try {
            for (int i = 0; i < numServants; i++) {
                servants[i].join();
            }
        } catch (Exception e) {
        }

        System.out.println("\n --- All done! ---");
        System.out.println("Presents taken: " + addCounter);
        System.out.println("Thank you notes written: " + removeCounter);
    }
}
