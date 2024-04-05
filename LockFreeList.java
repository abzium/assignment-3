import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList {
    public final Node head;
    public final Node tail;

    public LockFreeList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next.set(tail, false);
    }

    public boolean isEmpty() {
        if (head.next.getReference() == tail) {
            return true;
        } else {
            return false;
        }
    }

    public int peek() {
        return head.next.getReference().tagNumber;
    }

    private static class Node {
        // A present on the chain.
        // Has a tag number, and a pointer to the next present
        int tagNumber;
        AtomicMarkableReference<Node> next;

        public Node(int tagNumber) {
            this.tagNumber = tagNumber;
            this.next = new AtomicMarkableReference<LockFreeList.Node>(null, false);
        }
    }

    static class Window {
        // Inner class to help navigation
        public Node pred, curr; // predecessor and current nodes

        public Window(Node pred, Node curr) {
            this.pred = pred;
            this.curr = curr;
        }
    }

    public Window find(int tagNumber) {
        // succ = successor node
        Node pred = null, curr = null, succ = null;
        boolean[] marked = { false };
        boolean snip;
        retry: while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(marked);
                while (marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip)
                        continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if (curr.tagNumber >= tagNumber)
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    public boolean add(int tagNumber) {
        while (true) {
            Window window = find(tagNumber);
            Node pred = window.pred;
            Node curr = window.curr;
            if (curr.tagNumber == tagNumber) {
                return false;
            } else {
                Node node = new Node(tagNumber);
                node.next = new AtomicMarkableReference<Node>(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(int tagNumber) {
        boolean snip;
        while (true) {
            Window window = find(tagNumber);
            Node pred = window.pred, curr = window.curr;
            if (curr.tagNumber != tagNumber) {
                return false;
            } else {
                Node succ = curr.next.getReference();
                snip = curr.next.attemptMark(succ, true);
                if (!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(int tagNumber) {
        boolean[] marked = { false };
        Node curr = head;
        while (curr.tagNumber < tagNumber) {
            curr = curr.next.getReference();
            // Node succ = curr.next.get(marked);
        }
        return (curr.tagNumber == tagNumber && !marked[0]);
    }

}
