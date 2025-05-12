package it.unipi.healthhub.exception;

public class ScheduleAlreadyExistsException extends RuntimeException{
    public ScheduleAlreadyExistsException(){
        super("Schedule already exists.");
    }

    public ScheduleAlreadyExistsException(String message){
        super(message);
    }

    public ScheduleAlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }
}

