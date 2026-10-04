package hu.oh.vote.exception.szavazas;


public class SzavazasNotFoundException extends RuntimeException {

    public SzavazasNotFoundException(String message) {
        super(message);
    }
}