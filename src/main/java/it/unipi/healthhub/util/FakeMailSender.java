package it.unipi.healthhub.util;

import it.unipi.healthhub.model.mongo.Appointment;
import org.springframework.stereotype.Service;

@Service("fakeMailSender")
public class FakeMailSender implements MailSenderService {
    // private final JavaMailSender mailSender;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PERSONAL_EMAIL = "customer.support@healthhub.com";

    private boolean emailValidator(String email){
        return email != null && email.matches(EMAIL_REGEX);
    }

    @Override
    public boolean sendDeletedAppointmentMailByDoctor(Appointment appointment){
        String subject = "Healthhub - Deleted Appointment";

        String salutation = switch (appointment.getPatient().getGender().toLowerCase()){
            case "male" -> "Mr.";
            case "female" -> "Mrs.";
            default -> "Mx.";
        };

        String text = "Hi " + salutation + " " + appointment.getPatient().getName() + ". \n" +
                "Your appointment with Dr. " + appointment.getDoctor().getName() +
                " set for the " + appointment.getDate() +
                " has been deleted. We apologize for the inconvenience. \n" +
                "Please book again on our website. " +
                "Alternatively you can contact the doctor at this email address: " +
                appointment.getDoctor().getEmail() + "\n" +
                "We wish you a good day.";

        return fakeSendEmail(appointment.getPatient().getEmail(), subject, text);
    }

    @Override
    public boolean sendDeletedPastAppointmentMailByDoctor(Appointment appointment){
        String subject = "Healthhub - Deleted Appointment";

        String salutation = switch (appointment.getPatient().getGender().toLowerCase()){
            case "male" -> "Mr.";
            case "female" -> "Mrs.";
            default -> "Mx.";
        };

        String text = "Hi " + salutation + " " + appointment.getPatient().getName() + ". \n" +
                "Your appointment with Dr. " + appointment.getDoctor().getName() +
                " set for the " + appointment.getDate() +
                " has been deleted. This is probably because the visit did not take place. \n" +
                "If there is an error contact your doctor at this email address:" +
                appointment.getDoctor().getEmail() + "\n" +
                "We wish you a good day.";

        return fakeSendEmail(appointment.getPatient().getEmail(), subject, text);
    }

    @Override
    public boolean sendDeletedAppointmentMailByPatient(Appointment appointment){
        String subject = "Healthhub - Deleted Appointment";

        String salutation = switch (appointment.getPatient().getGender().toLowerCase()){
            case "male" -> "Mr.";
            case "female" -> "Mrs.";
            default -> "Mx.";
        };

        String text = "Hi Dr. " + appointment.getDoctor().getName() + ". \n" +
                "Your appointment with " + salutation + " " + appointment.getPatient().getName() +
                " set for the " + appointment.getDate() +
                " has been deleted. We apologize for the inconvenience. \n" +
                "You can contact the patient at this email address: " +
                appointment.getPatient().getEmail() + "\n" +
                "We wish you a good day.";

        return fakeSendEmail(appointment.getDoctor().getEmail(), subject, text);
    }

    /**
     * Send a password‑reset link to the given email.
     * @param to       the recipient email address
     * @param resetLink the full URL the user clicks to reset their password
     * @return true if “sent” successfully
     */
    @Override
    public boolean sendPasswordResetLink(String to, String resetLink){
        if(!emailValidator(to)){
            System.out.println("Failed to send password‑reset email: invalid address.");
            return false;
        }

        String subject = "HealthHub - Password Reset";
        String text =  "Hello,\n\n"
                + "We received a request to reset your password. "
                + "Please click the link below to choose a new password:\n\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, you can safely ignore this email.\n\n"
                + "Best regards,\n"
                + "The HealthHub Team";

        return fakeSendEmail(to, subject, text);
    }

    public boolean fakeSendEmail(String to, String subject, String text){
        if(emailValidator(to)){
            System.out.println("Sending email to: " + to + " with " + PERSONAL_EMAIL);
            System.out.println("Email subject: " + subject);
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

    /*
    public boolean sendEmail(String to, String subject, String text) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            mailSender.send(message);
            return true;
        }
        catch(MessagingException e){
            e.printStackTrace();
            return false;
        }
    }
     */
}
