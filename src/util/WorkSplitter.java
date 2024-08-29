package util;

public class WorkSplitter {
    public int capacity;
    private final int size;
    public WorkSplitter(int capacity, int size) {
        this.capacity = capacity;
        this.size = size;
    }

    public int getStart(int rank) {
        int chunk = calculateChunkSize();
        System.out.println("chunk size is for start"+chunk);
        return rank * chunk;
    }

    public int getEnd(int rank) {
        int chunk = calculateChunkSize();
        //System.out.println("End for rank "+rank+ " "+ Math.min(capacity, (rank + 1) * chunk));
        System.out.println("chunk size is for end"+chunk);

        return Math.min(capacity-1, (rank + 1) * chunk);
    }

    public void setSize(int capacity){
        this.capacity = capacity;
    }

    public int getMinChunkSize(){
        int chunk = calculateChunkSize();
        return Math.min(capacity,size*chunk) - (size-1)*chunk;
    }


    private int calculateChunkSize() {
        System.out.println("size "+size);
        System.out.println("capacity pop size "+capacity);
        System.out.println("chunkic "+ (int) Math.ceil((double) capacity / size));
        return (int) Math.ceil((double) capacity / size);
    }
}
