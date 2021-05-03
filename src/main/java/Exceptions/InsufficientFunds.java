package Exceptions;

public class InsufficientFunds extends Throwable{
    public InsufficientFunds (){
        super("Error: insufficient funds");
    }
}
