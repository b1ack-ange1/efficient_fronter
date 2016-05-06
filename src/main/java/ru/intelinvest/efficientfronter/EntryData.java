/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.intelinvest.efficientfronter;

import java.io.Serializable;

/**
 *
 * @author nedelko
 */
public class EntryData implements Serializable {

    private double x;
    private double y;
    private double v1;
    private double v2;

    public EntryData(double x, double y, double v1, double v2) {
        this.x = x;
        this.y = y;
        this.v1 = v1;
        this.v2 = v2;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getV1() {
        return v1;
    }

    public void setV1(double v1) {
        this.v1 = v1;
    }

    public double getV2() {
        return v2;
    }

    public void setV2(double v2) {
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return "EntryData{" + "x=" + x + ", y=" + y + ", v1=" + v1 + ", v2=" + v2 + '}';
    }
}
