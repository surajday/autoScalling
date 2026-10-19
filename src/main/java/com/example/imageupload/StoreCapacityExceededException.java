package com.example.imageupload;

public class StoreCapacityExceededException extends RuntimeException {

    private final long capacityBytes;
    private final long usedBytes;
    private final long requestedBytes;

    public StoreCapacityExceededException(long capacityBytes, long usedBytes, long requestedBytes) {
        super("This instance does not have enough image storage capacity");
        this.capacityBytes = capacityBytes;
        this.usedBytes = usedBytes;
        this.requestedBytes = requestedBytes;
    }

    public long getCapacityBytes() { return capacityBytes; }
    public long getUsedBytes() { return usedBytes; }
    public long getRequestedBytes() { return requestedBytes; }
}
