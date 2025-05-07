package it.unipi.healthhub.util;

import it.unipi.healthhub.model.mongo.Appointment;
import java.time.LocalDate;

public class FakeMailSender{
    private static FakeMailSender instance;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PERSONAl_EMAIL = "customer.support@healthhub.com";

    private FakeMailSender(){
        System.out.println("MailService initialized: " + PERSONAl_EMAIL);
    }

    // Method to get the singleton instance
    public static FakeMailSender getInstance() {
        if (instance == null) {
            instance = new FakeMailSender();
        }
        return instance;
    }

    public static boolean sendDeletedAppointmentMailByDoctor(Appointment appointment){
        String subject = "Healthhub - Deleted Appointment";

        // For the gender of the patient
        String salutation;
        switch(appointment.getPatient().getGender().toLowerCase()){
            case "male":
                salutation = "Mr.";
                break;
            case "female":
                salutation = "Mrs.";
                break;
            default:
                salutation = "Mx.";
                break;
        }

        String text = "Hi " + salutation + " " + appointment.getPatient().getName() + ". \n" +
                "Your appointment with Dr. " + appointment.getDoctor().getName() +
                " set for the " + appointment.getDate() +
                " has been deleted. We apologize for the inconvenience. \n"  +
                "Please. Book again on our website or contact your doctor. " +
                "Alternatively you can contact the doctor at this email address: " +
                appointment.getDoctor().getEmail() + "\n" +
                "We wish you a good day.";

        return fakeSendEmail(appointment.getPatient().getEmail(), subject, text);
    }

    public static boolean sendDeletedAppointmentMailByPatient(Appointment appointment){
        String subject = "Healthhub - Deleted Appointment";

        // For the gender of the patient
        String salutation;
        switch(appointment.getPatient().getGender().toLowerCase()){
            case "male":
                salutation = "Mr.";
                break;
            case "female":
                salutation = "Mrs.";
                break;
            default:
                salutation = "Mx.";
                break;
        }

        String text = "Hi Dr. " + appointment.getDoctor().getName() + ". \n" +
                "Your appointment with " + salutation + appointment.getPatient().getName() +
                " set for the " + appointment.getDate() +
                " has been deleted. We apologize for the inconvenience. \n" +
                "You can contact the patient at this email address: " + appointment.getPatient().getEmail() + " \n" +
                "We wish you a good day.";

        return fakeSendEmail(appointment.getPatient().getEmail(), subject, text);
    }

    public static boolean fakeSendEmail(String email, String subject, String text){
        if(getInstance().emailValidator(email)){
            System.out.println("Sending email to: " + email + " with " + PERSONAl_EMAIL);
            System.out.println("Email content:" + subject);
            System.out.println("--------------------------------------------------");
            System.out.println(text);
            System.out.println("--------------------------------------------------");
            System.out.println("Email sent successfully.");
            return true;
        }
        else{
            System.out.println("Failed to send email: invalid user or missing email.");
            return false;
        }
    }

    private boolean emailValidator(String email){
        return (email != null && email.matches(EMAIL_REGEX));
    }
}
