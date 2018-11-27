package cn.hydro.jpa;

import java.io.Serializable;

/**
 * @ClassName Ga
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 21:17
 * @Version 1.0
 */
public class Ga   implements Comparable<Ga>, Cloneable, Serializable {
    private Parameter[] parameters;
    private Double dc;

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Double getDc() {
        return dc;
    }

    public void setDc(double dc) {
        this.dc = dc;
    }


    public int compareTo(Ga o) {
        if(Double.isNaN(o.getDc())){
            return -1;
        }else if (Double.isNaN(this.getDc())){
            return 1;
        }else{
            return -this.getDc().compareTo(o.getDc());
        }

    }

    @Override
    protected Object clone() {
        Ga ga = null;
        try {
            ga = (Ga) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ga;
    }

}
