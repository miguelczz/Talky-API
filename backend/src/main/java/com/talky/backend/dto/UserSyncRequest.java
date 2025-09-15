package com.talky.backend.dto;

// Este DTO representa los datos que llegan desde el frontend
public class UserSyncRequest {
    private String sub; // Identificador único de Cognito
    private String email;
    private String name;
    private String role;
    private String phoneNumber;
    private String birthdate;
    private String gender;

    // getters y setters
    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
