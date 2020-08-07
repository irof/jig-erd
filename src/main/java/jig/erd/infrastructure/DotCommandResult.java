package jig.erd.infrastructure;


public class DotCommandResult {

    Status status;
    String message;

    public DotCommandResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static DotCommandResult success() {
        return new DotCommandResult(Status.SUCCESS, "");
    }

    public static DotCommandResult failure() {
        return new DotCommandResult(Status.FAILURE, "");
    }

    public boolean succeed() {
        return this.status == Status.SUCCESS;
    }

    public boolean failed() {
        return !succeed();
    }

    public DotCommandResult withMessage(String message) {
        return new DotCommandResult(this.status, message);
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "DotCommandResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    enum Status {
        SUCCESS,
        FAILURE;
    }
}
