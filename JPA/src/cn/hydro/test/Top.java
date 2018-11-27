package cn.hydro.test;

import cn.hydro.jpa.Ga;
import cn.hydro.model.topModel.TopModel;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName Top
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 22:04
 * @Version 1.0
 */
public class Top implements Runnable{

    private Thread t;
    private String threadName;
    private Ga ga;
    private final CountDownLatch latch;

    public Top(Ga ga, String threadName, final CountDownLatch latch) {
        this.ga = ga;
        this.threadName = threadName;
        this.latch = latch;
    }

    public void start () {
        //System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

//    @Override
    public void run() {
        TopModel ca = new TopModel();

        ca.setT0(ga.getParameters()[0].getRealValue());
        ca.setTd(ga.getParameters()[1].getRealValue());
        ca.setSzm(ga.getParameters()[2].getRealValue());
        ca.setSrmax(ga.getParameters()[3].getRealValue());
        ca.setQb1(ga.getParameters()[4].getRealValue());
        ca.setSR0(ga.getParameters()[5].getRealValue());
        ca.setSuz0(ga.getParameters()[6].getRealValue());
        ca.setKc(ga.getParameters()[7].getRealValue());
        ca.setCr(ga.getParameters()[8].getRealValue());
        ca.setMF(ga.getParameters()[9].getRealValue());
        ca.setTm(ga.getParameters()[10].getRealValue());
        ca.setB(ga.getParameters()[11].getRealValue());
        ca.readData("data_day.txt");
        ca.readTWI("海100类.txt");
        ca.init(322000000);
        ca.runoff2();
        ca.routing();
        ca.toTxt("模型计算结果.txt");

        ga.setDc(ca.getDc());
        latch.countDown();
    }
}
