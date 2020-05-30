package com.tnscorcoran;

public class Greeting {

    private String message;
    private final String DESCRIPTION = "Demo: Spring Boot Hello World";

    public Greeting(String name) {
        if (name !=null && name.length()>0){
            message = DESCRIPTION + " - for " +name;
        }
        else{
            message = DESCRIPTION ;
        }
        
    }

    public String getMessage() {
        return message;
    }

}
