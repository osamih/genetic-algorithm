package com.slimanice.distributedgeneticalgorthm.utils;

import jade.core.AID;

public class Solution {
    private String localName;
    private String solution;
    private int fitness;
    private AID aid;

    public Solution(AID aid, String solution, int fitness) {
        this.aid = aid;
        this.solution = solution;
        this.fitness = fitness;
        this.localName = aid.getLocalName();
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
        this.localName = aid.getLocalName();
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "aid=" + aid.getLocalName() +
                ", solution='" + solution + '\'' +
                ", fitness=" + fitness +
                '}';
    }
}
