package cn.hydro.jpa;

/**
 * @ClassName DownUp
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 21:17
 * @Version 1.0
 */
public class DownUp {
    private double min;
    private double max;

    public DownUp(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
