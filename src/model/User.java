package model;

public class User {
    private int usersID;
    private String userName;
    private String password;
    private String address;
    private String email;
    private String phoneNum;
    private String role;

    // Constructor, getters, setters

    public int getUsersID() { return usersID; }
    public void setUsersID(int usersID) { this.usersID = usersID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
