package br.com.rafaelmaia.mar_de_beleza_system.services.exceptions;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String msg) {
        super(msg);
    }
}
