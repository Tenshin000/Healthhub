package it.unipi.healthhub.exception;

import javax.print.Doc;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(){
        super("Doctor not found");
    }

    public DoctorNotFoundException(String message) {
        super(message);
    }

    public DoctorNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
