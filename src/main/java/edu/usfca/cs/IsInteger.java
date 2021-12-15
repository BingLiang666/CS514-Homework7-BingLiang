package edu.usfca.cs;

public class IsInteger {

    /**
     *
     * @param input string given by user
     * @return
     *
     * this returns true if the string given by user can be converted to integer, otherwise returns false
     *
     */
    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}
