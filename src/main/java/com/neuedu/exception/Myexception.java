package com.neuedu.exception;

public class Myexception extends RuntimeException {
    private String director;
    public Myexception(String msg, String director){
        super(msg);
        this.director=director;
    }
    public Myexception(String msg){
        super(msg);
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}
