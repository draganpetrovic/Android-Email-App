package com.aleksandar69.PMSU2020Tim16.models;

import com.aleksandar69.PMSU2020Tim16.enums.Condition;
import com.aleksandar69.PMSU2020Tim16.enums.Operation;

public class Rule {
    private int id;
    private Condition condition;
    private String conditonTxt;
    private Operation operation;
    private int folder_id;

    public Rule(Condition condition,String conditonTxt ,Operation operation) {
        this.condition = condition;
        this.conditonTxt = conditonTxt;
        this.operation = operation;
    }

    public Rule(int id, Condition condition,String conditonTxt ,Operation operation) {
        this.id = id;
        this.condition = condition;
        this.conditonTxt = conditonTxt;
        this.operation = operation;
    }

    public Rule(){

    }

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getConditonTxt() {
        return conditonTxt;
    }

    public void setConditonTxt(String conditonTxt) {
        this.conditonTxt = conditonTxt;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
