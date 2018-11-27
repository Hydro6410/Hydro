package cn.hydro.jpa;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.Serializable;

/**
 * @ClassName Parameter
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 21:16
 * @Version 1.0
 */
public class Parameter  implements Cloneable, Serializable {
    private long value;
    private final double max;
    private final double min;
    private double realValue;

    public Parameter(double max, double min) {
        this.max = max;
        this.min = min;
        this.value = new RandomDataGenerator().nextLong(0, Long.MAX_VALUE);
        this.realValue = (double) this.value/ (double)Long.MAX_VALUE *(max-min) + min;
    }

    public void setValue(long value) {
        this.value = value;
        this.realValue = (double) this.value/ (double)Long.MAX_VALUE *(max-min) + min;
    }

    public long getValue() {
        return value;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getRealValue() {
        return realValue;
    }

    @Override
    protected Object clone() {
        Parameter parameter = null;
        try {
            parameter = (Parameter) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return parameter;
    }
}
