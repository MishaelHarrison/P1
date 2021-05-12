package Models;

public class ErrorResponse {
    private Throwable error;
    private String message;

    public ErrorResponse(Throwable error, String message) {
        this.error = error;
        this.message = message;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
