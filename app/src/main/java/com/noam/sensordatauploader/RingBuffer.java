package com.noam.sensordatauploader;

/**
 * Simple power of 2 ring buffer, tailored for single read
 * multithread safe: NO
 */
public class RingBuffer {

    private Object[] elements;

    private int capacity;
    private int writeCounter = 0;
    private int readCounter = 0;
    private int bitmask;

    public RingBuffer(int capacity_log2) {
        if (BuildConfig.DEBUG && !(capacity_log2 > 2 && (capacity_log2 < 16)))
            throw new RuntimeException("capacity out of range");
        capacity = 1 << capacity_log2;
        bitmask = capacity - 1;
        elements = new Object[capacity];
    }

    public void reset() {
        writeCounter = 0;
        readCounter = 0;
    }

    public int available() {
        // This is correct only if writeCounter did not wrap (at 2**sizeof(integer)) and no reading
        // writeCounter < capacity  IFF flipped == false
        return writeCounter < capacity ? writeCounter - readCounter : capacity;
    }

    public void put(Object element) {
        int writePos = writeCounter & bitmask;
        elements[writePos] = element;
        writeCounter++;
    }


    /**
     * take all the elements in fifo order.
     *
     * @return number of elements retrieved
     */
    public int take(Object[] into, int length) {
        int intoWritePos = 0;
        boolean flipped = writeCounter >= capacity;
        if (!flipped) {
            int endPos = Math.min(writeCounter, readCounter + length);
            for (; readCounter < endPos; readCounter++) {
                into[intoWritePos++] = elements[readCounter & bitmask];
            }
            return intoWritePos;
        } else {
            //readPos higher than writePos - available sections are
            //top + bottom of elements array

            int writePos = writeCounter & bitmask;
            //copy from top
            for (int r = writePos; r < capacity; r++) {
                into[intoWritePos++] = elements[r];
            }
            //copy from bottom
            for (int r = 0; r < writePos; r++) {
                into[intoWritePos++] = elements[r];
            }
            return capacity;
        }
    }
}
