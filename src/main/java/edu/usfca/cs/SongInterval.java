package edu.usfca.cs;

public class SongInterval {
    private int length;

    /**
     *
     * @param l song length
     *
     * This is a constructor.
     *
     */
    public SongInterval(int l) {
        this.length = l;
    }

    /**
     * This is a constructor.
     */
    public SongInterval() {}

    /**
     *
     * @return string represented for song length
     *
     * This returns a string represented for song length.
     *
     */
    public String toString() {
        int minutes = length / 60;
        int seconds = length % 60;
        return String.format("%d:%d", minutes, seconds);
    }
}
