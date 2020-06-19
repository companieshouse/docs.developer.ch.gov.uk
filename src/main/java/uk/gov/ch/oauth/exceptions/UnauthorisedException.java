package uk.gov.ch.oauth.exceptions;

public class UnauthorisedException extends Exception {

    public UnauthorisedException(String error) {
        super(error);
    }
}