import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Servant implements Runnable {
    LockFreeList presentChain;
    ArrayList<Integer> presentBag;
    AtomicInteger presentsAdded;
    AtomicInteger thankYouNotes;
    int numPresents;
    // private Object lock;

    public Servant(LockFreeList presentChain, ArrayList<Integer> presentsBag, AtomicInteger presentsAdded,
            AtomicInteger thankYouNotes) {
        this.presentChain = presentChain;
        this.presentBag = presentsBag;
        this.presentsAdded = presentsAdded;
        this.thankYouNotes = thankYouNotes;
        numPresents = presentBag.size();
        // this.lock = lock;
    }

    public void run() {
        // Alternate between adding, removing, and checking
        // Initial mode
        String mode = "add";
        // For checking random present tags in the chain
        Random rand = new Random();

        // If both the bag and chain are empty, we're done
        while (presentBag.size() > 0 || !presentChain.isEmpty()) {
            if (mode == "add") {
                // take a present from the bag if it has any, and add it to the chain
                try {
                    if (presentBag.size() > 0) {
                        int presentTag = presentBag.remove(0);
                        presentChain.add(presentTag);
                        System.out.println("Added " + presentTag + " to chain.");
                        presentsAdded.incrementAndGet();
                    }
                } catch (Exception e) {

                }

                mode = "remove";

            } else if (mode == "remove") {
                // if the chain has something, take the first present and remove it
                int tag = presentChain.peek();
                if (tag != Integer.MAX_VALUE) {
                    presentChain.remove(tag);
                    System.out.println("Wrote a thank you note for " + tag);
                    thankYouNotes.incrementAndGet();
                }
                mode = "scan";

            } else {
                // as per the minotaur's request, check if a present is in the chain
                int checkPresent = rand.nextInt(numPresents);
                if (presentChain.contains(checkPresent)) {
                    System.out.println("Present " + checkPresent + " was found!");
                } else {
                    System.out.println("Present " + checkPresent + " was not found!");
                }
                mode = "add";
            }
        }

    }
}