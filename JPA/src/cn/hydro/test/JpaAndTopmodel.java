package cn.hydro.test;

import cn.hydro.jpa.DownUp;
import cn.hydro.jpa.Jpa;

/**
 * @ClassName JpaAndTopmodel
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 22:00
 * @Version 1.0
 */
public class JpaAndTopmodel {
    public static void main(String[] args) {
        DownUp[] downUps = new DownUp[12];
//		private double T0; // 饱和导水率(m/h),取值范围[0.01m/h,20m/h]
        downUps[0] = new DownUp(0.01, 20);
//		private double td; // 时间参数(h/m),取值范围 [0,100h/m]
        downUps[1] = new DownUp(0.00, 100);
//		private double Szm;// 非饱和区最大蓄水深度(m),取值范围[0.0001,0.5]
        downUps[2] = new DownUp(0.0001, 0.5);
//		private double Srmax;// 根系区最大容水量(m),取值范围[0.001,0.5]\
        downUps[3] = new DownUp(0.001, 0.5);
//		private double Qb1; // 初始壤中流(m^3/s),取值范围[0,50]
        downUps[4] = new DownUp(0.00, 50);
//		private double SR0;// 根系层初始含水量(m) ,取值范围[0,0.5]
        downUps[5] = new DownUp(0.0, 0.5);

//		private double Suz0;// 非饱和层初始含水量(m)[0,1]
        downUps[6] = new DownUp(0.0, 1);
//		private double Kc; // 蒸发折算系数 ,取值范围 [0.1,2]
        downUps[7] = new DownUp(0.1, 2);
//		private double Cr;// 汇流比[0,1]
        downUps[8] = new DownUp(0.0, 1);
//		private double MF;// 度日因子(mm/(d·℃))[1.0,1.9]
        downUps[9] = new DownUp(1, 2.9);
//		private double Tm;// 温度阈值[0.1,1]
        downUps[10] = new DownUp(0.1, 1);

//		private double b;// 非线性指数[0,1]
        downUps[11] = new DownUp(0., 1);

        Jpa jgap = new Jpa(100,100, downUps);

        jgap.run();
    }
}
