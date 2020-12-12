package com.example.thetrempiada.driverActivities;

public class MyTrempObj extends Tremp{
    public int freePlaces;

    public MyTrempObj(Tremp t,int freePlaces) {
        super(t);
        this.freePlaces = freePlaces;
    }

    public int getFreePlaces() {
        return freePlaces;
    }

    public void setFreePlaces(int freePlaces) {
        this.freePlaces = freePlaces;
    }

    public String toString(){
        String s = super.toString();
        s+="\nfree spaces: "+this.freePlaces;
        return s;
    }

}
