package Exceptions;

public class BadLogin extends Exception{
    public BadLogin(){
        super("Improper provided login credentials");
    }
}
