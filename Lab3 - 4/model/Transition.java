package model;

public class Transition {
    String t1, t2, res;

    public Transition(String from, String to, String label) {
        this.t1 = from;
        this.t2 = to;
        this.res = label;
    }

    @Override
    public String toString() {
        return t1 + " -> " + t2 + " : " + res;
        }

    public String getT1() {
        return t1;
    }

    public String getT2() {
        return t2;
    }

    public String getRes() {
        return res;
    }
}