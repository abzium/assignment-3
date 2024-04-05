# assignment-3
COP4520 Assignment 3 \
Abdul Muiz

# Problem 1: The Birthday Presents Party

## Compiling and Running

To compile:
```
javac MinotaurPresents.java
```
To run:
```
java MinotaurPresents
```

## Implementation
This program adapts an implementation of the Lock Free List from the book _The Art of Multicore Programming_, which defines a concurrent linked list and has methods to add, remove, and search nodes within the list. I also added the method `isEmpty()` to check if the list is empty, and  `peek()` to return the first element of the linked list. 

In our main class, `MinotaurPresents`, we initialize the lock free list as the chain of presents, an `ArrayList` as the bag of unordered presents, and shared atomic counters to count how many presents were added to the chain and how many were taken out. The four threads are also initialized and ran here. When the four threads are done, we print out the values of the counters.

The threads are based on the `Servant` class. Each thread alternates between adding presents to the chain, removing presents from the chain, and checking the chain for a random present. 
- Adding presents removes the item from the present bag array, and adds it to the presents chain, and increments the present counter. 
- Removing presents gets the first node and checks to see if it points to the tail (signifying that it is empty). If there is a present, the thread removes it and increments the thank you note counter. 
- When the thread scans the chain, it gets a random tag number and checks the chain for it, printing whether or not it found that present in the chain.

When both the present bag and present chain are empty, then the threads conclude.


## Experimental Evaluation
### Correctness
Here is a sample from the end:
```
Present 485030 was not found!

 --- All done! ---
Presents taken: 500125
Thank you notes written: 495446
```
There is some slight miscounting still happening. This could potentially be fixed if the Minotaur is convinced to let the servant add all the presents first before removing them.
### Efficiency
The average time needed to run the program for 500000 presents turned out to be 135209ms.

# Problem 2: Atmospheric Temperature Reading Module
## Compiling and Running

To compile:
```
javac TempReadingModule.java
```
To run:
```
java TempReadingModule
```

## Implementation
This program uses a shared array to keep track of recorded temperatures per minute. In the main function, constants are initialized and threads are ran for every hour. After the threads have recorded temperatures for the hour, a report is generated. This report checks every 10 minute interval every hour for the largest temperature difference. Once the relevant values are found, the array of temperatures is sorted and the highest five and lowest five temperatures are recorded in a separate array. All this information is then printed to the screen. 

In each thread, the temperature array is updated with a random value between -100 and 70 along with incrementing an index counter.


## Experimental Evaluation
### Correctness
Here is a sample report:
```
Report for hour 23:
The top 5 highest temperatures:
69
69
69
68
68
The top 5 lowest temperatures:
-100
-100
-100
-99
-99
The largest temperature difference was 169 from 69 to -100
Occurring between minutes 48 and 55
```

### Efficiency
The average time needed to run the program for 24 hour readings turned out to be about 120ms.
