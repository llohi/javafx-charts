package com.fluentt.javafxcharts.data;

import java.util.Arrays;

/**
 * This class represents the data fetched from the Finnish Meteorological Institute API.
 * @author J.L.
 */
public class BsWfsElement {

    private double[] pos;
    private String time, parameter_name;
    private double parameter_value;

    public BsWfsElement() {}


    public BsWfsElement(double[] pos, String time, String parameter_name, double parameter_value) {
        this.pos = pos;
        this.time = time;
        this.parameter_name = parameter_name;
        this.parameter_value = parameter_value;
    }

    public double[] getPos() {
        return pos;
    }

    public void setPos(double[] pos) {
        this.pos = pos;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getParameter_name() {
        return parameter_name;
    }

    public void setParameter_name(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    public double getParameter_value() {
        return parameter_value;
    }

    public void setParameter_value(double parameter_value) {
        this.parameter_value = parameter_value;
    }

    @Override
    public String toString() {
        return "BsWfsElement{" +
                "pos=" + Arrays.toString(pos) +
                ", time='" + time + '\'' +
                ", parameter_name='" + parameter_name + '\'' +
                ", parameter_value=" + parameter_value +
                '}';
    }
}