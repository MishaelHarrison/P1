package Exceptions;

public class BusinessException extends Exception{
    public BusinessException(String message){
        this(new Throwable(message));
    }
    public BusinessException(Throwable e){
        super("Error in data access, contact sysadmin", e);
    }
}
