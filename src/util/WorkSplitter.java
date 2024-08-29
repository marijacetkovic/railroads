package util;

public class WorkSplitter {
    public int capacity;
    private final int size;
    public WorkSplitter(int capacity, int size) {
        if(capacity!=1000){
            System.out.println("Vasilije turcin");
        }
        this.capacity = capacity;
        this.size = size;
    }

    public int getStart(int rank) {
        int chunk = calculateChunkSize();
        return rank * chunk;
    }

    public int getEnd(int rank) {
        int chunk = calculateChunkSize();
        return Math.min(capacity-1, (rank + 1) * chunk);
    }

    public void setSize(int capacity){
        this.capacity = capacity;
    }

    public int getMinChunkSize(){
        int chunk = calculateChunkSize();
        System.out.println("in minchunksize size"+ size);
        System.out.println("capacity "+capacity);
        return Math.min(capacity,size*chunk) - (size-1)*chunk;
    }


    private int calculateChunkSize() {
        return (int) Math.ceil((double) capacity / size);
    }
}
