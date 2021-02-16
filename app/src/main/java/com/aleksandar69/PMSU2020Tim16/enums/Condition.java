package com.aleksandar69.PMSU2020Tim16.enums;

public enum Condition {
    TO("TO"),
    FROM("FROM"),
    CC("CC"),
    SUBJECT("SUBJECT");

    private String conditionString;

    Condition(String conditionString){
        this.conditionString = conditionString;
    }

    @Override
    public String toString(){
        return conditionString;
    }




}
