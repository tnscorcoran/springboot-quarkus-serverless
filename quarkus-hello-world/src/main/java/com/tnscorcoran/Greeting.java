package com.tnscorcoran;

public class Greeting {

    private String message;
    private final String DESCRIPTION = "Demo: Quarkus Hello World";

    public Greeting(String name) {
        if (name !=null && name.length()>0){
            message = DESCRIPTION + " - for " +name;
        }
        else{
            message = "June 12 - " + DESCRIPTION ;
        }
        
    }

    public String getMessage() {
        return message;
    }

}
