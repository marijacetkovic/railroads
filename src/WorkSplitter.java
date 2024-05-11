public class WorkSplitter {
    private final int capacity;
    private final int size;
    private final int numChunks;

    public WorkSplitter(int capacity, int size) {
        this.capacity = capacity;
        this.size = size;
        this.numChunks = (int) Math.ceil((double) capacity / size);
    }

    public int calculateStart(int rank) {
        int chunk = calculateChunkSize();
        return rank * chunk;
    }

    public int calculateEnd(int rank) {
        int chunk = calculateChunkSize();
        return Math.min(capacity, (rank + 1) * chunk);
    }


    private int calculateChunkSize() {
        return (int) Math.ceil((double) capacity / size);
    }
}
