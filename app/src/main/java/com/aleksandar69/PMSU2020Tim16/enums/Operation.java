package com.aleksandar69.PMSU2020Tim16.enums;

public enum Operation {
    MOVE("MOVE"),
    COPY("COPY"),
    DELETE("DELETE");

    private String operationString;

    Operation(String operationString){
        this.operationString = operationString;
    }

    public String toString(){
        return operationString;
    }

}
