package exceptions;

// runtime because not all algorithms currently handle it.
// This may not be the correct way to approach it.
public class NoSpaceLeftException extends Exception {

    // Constructor without a message
    public NoSpaceLeftException() {
        super("No space left to schedule tasks in the weekly calendar.");
    }

    // Constructor with a custom message
    public NoSpaceLeftException(String message) {
        super(message);
    }

    // Constructor with a cause (another exception that caused this one)
    public NoSpaceLeftException(Throwable cause) {
        super("No space left to schedule tasks in the weekly calendar.", cause);
    }

    // Constructor with both a custom message and a cause
    public NoSpaceLeftException(String message, Throwable cause) {
        super(message, cause);
    }
}
