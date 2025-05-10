// src/main/java/it/unipi/healthhub/dto/PasswordChangeRequest.java
package it.unipi.healthhub.dto;

public class PasswordChangeDTO{
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword(){
        return currentPassword;
    }
    public void setCurrentPassword(String currentPassword){
        this.currentPassword = currentPassword;
    }
    public String getNewPassword(){
        return newPassword;
    }
    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
    }
}
