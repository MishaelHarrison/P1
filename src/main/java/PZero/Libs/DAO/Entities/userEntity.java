package PZero.Libs.DAO.Entities;

public class userEntity {
    private int ID;
    private String username;
    private String password;
    private String fname;
    private String lname;

    public userEntity(int ID, String username, String password, String fname, String lname) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}
