package cn.hydro.model.topModel;


import java.io.*;
import java.util.ArrayList;

/**
 * @ClassName TopModel
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 21:51
 * @Version 1.0
 */
public class TopModel {
    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getMF() {
        return MF;
    }

    public void setMF(double mF) {
        MF = mF;
    }

    public double getTm() {
        return Tm;
    }

    public void setTm(double tm) {
        Tm = tm;
    }

    public double getSuz0() {
        return Suz0;
    }

    public void setSuz0(double suz0) {
        Suz0 = suz0;
    }

    public double getCr() {
        return Cr;
    }

    public void setCr(double cr) {
        Cr = cr;
    }

    public double getKc() {
        return Kc;
    }

    public void setKc(double kc) {
        Kc = kc;
    }

    public double getDc() {
        return dc;
    }

    public void setDc(double dc) {
        this.dc = dc;
    }

    public double getT0() {
        return T0;
    }

    public void setT0(double t0) {
        T0 = t0;
    }

    public double getTd() {
        return td;
    }

    public void setTd(double td) {
        this.td = td;
    }

    public double getSzm() {
        return Szm;
    }

    public void setSzm(double szm) {
        Szm = szm;
    }

    public double getSrmax() {
        return Srmax;
    }

    public void setSrmax(double srmax) {
        Srmax = srmax;
    }

    public double getQb1() {
        return Qb1;
    }

    public void setQb1(double qb1) {
        Qb1 = qb1;
    }

    public double getSR0() {
        return SR0;
    }

    public void setSR0(double sR0) {
        SR0 = sR0;
    }

    private int N;// 模拟时段数
    private int M;// 地形湿度指数分段数
    private double Area;// 流域面积
    //private double Area_glacier;// 冰川面积
    private double dt = 3600.0;// 用于ex_z的更新计算,1day = 24h = 86400.0s,1h=3600.0s;
    // ***************************模型参数**********************************//
    private double T0; // 饱和导水率(m/h),取值范围[0.01m/h,20m/h]
    private double td; // 时间参数(h/m),取值范围 [0,100h/m]
    private double Szm;// 非饱和区最大蓄水深度(m),取值范围[0.0001,0.5]
    private double Srmax;// 根系区最大容水量(m),取值范围[0.001,0.5]
    private double Qb1; // 初始壤中流(m^3/s),取值范围[0,50]
    private double SR0;// 根系层初始含水量(m) ,取值范围[0,0.5]
    private double Suz0;// 非饱和层初始含水量(m)
    private double Kc; // 蒸发折算系数 ,取值范围 [0.1,2]
    private double Cr;// 汇流比
    private double MF;// 度日因子(mm/(d·℃))
    private double Tm;// 温度阈值
    private double b;// 非线性指数

    // ***************************输入输出数据**********************************//
    private double A[]; // M 地貌指数相同的点的面积之和
    private double dmzs[];// M 占不同面积百分比的地貌指数
    private double P[];// N 降雨量
    private double E0[];// N 水面蒸发
    private double Qsim[], Qobs[];// N 总径流模拟值与实测值

    // ***************************某些中间变量**********************************//
    private double lambda = 0.0; // 地貌指数在面积上的加权平均
    private double Ep[], Ea[][];// N, N M日蒸发能力与某时刻段m处的实际蒸发量
    private double E[];// 总蒸发量

    // ***************************根系区**********************************//
    private double Srz[][]; // N M某时刻段m处的根系区缺水量
    private double qr[][]; // N M某时刻段m处的根系区进入非饱和区的水量

    // ****************************非饱和区*****************************//
    private double Suz[][]; // N M某时刻段m处非饱和土壤含水量
    private double qv[][]; // N M某时刻段m处下渗率
    private double Qv[]; // N 某时刻全流域总下渗率
    private double Rs[][];// 蓄满产流深

    // *****************************饱和区*********************************//
    private double SD[][]; // N M某时刻段m处饱和地下水深度，SD=z
    private double ave_z[];// N 某时刻流域平均饱和地下水水面深度
    private double Qb[]; // N 某时刻基流量
    private double Qs[]; // N 饱和坡面流
    private double Q0;// 基流中间计算量

    // *****************************冰川融化*********************************//
    //private double T[];// N 某时刻温度
    //private double MD[];// N 某时刻冰川融化深度
    //private double Qm[];// N 某时刻冰川融化流量
    // *****************************汇流参数**************************************//
    private double Qt[];
    // *****************************nash系数**************************************//
    private double dc;
    // -----------------------------------------------//

    /**
     * 读取降雨、流量、蒸发、温度数据
     */
    public void readData(String path) {

        ArrayList<Double> p = new ArrayList<Double>();
        ArrayList<Double> e = new ArrayList<Double>();
        ArrayList<Double> q0 = new ArrayList<Double>();
        // ArrayList<Double> t = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            Reader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\t");
                q0.add(Double.parseDouble(s[0]));
                p.add(Double.parseDouble(s[1]));
                e.add(Double.parseDouble(s[2]));
                // t.add(Double.parseDouble(s[3]));
            }
            this.N = p.size();
            this.P = new double[N];
            this.E0 = new double[N];
            this.Qobs = new double[N];
            //this.T = new double[N];
            for (int i = 0; i < N; i++) {
                this.P[i] = p.get(i);
                this.E0[i] = e.get(i);
                this.Qobs[i] = q0.get(i);
                // this.T[i] = t.get(i);
            }
            br.close();
            fr.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * 读取地形指数数据
     */
    public void readTWI(String twiPath) {
        ArrayList<Double> ti = new ArrayList<Double>();
        ArrayList<Double> a = new ArrayList<Double>();
        try {
            File file = new File(twiPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            Reader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\t");
                ti.add(Double.parseDouble(s[0]));
                a.add(Double.parseDouble(s[1]));
            }
            this.M = ti.size();
            this.dmzs = new double[M];
            this.A = new double[M];
            for (int i = 0; i < M; i++) {
                this.dmzs[i] = ti.get(i);
                this.A[i] = a.get(i);
            }
            br.close();
            fr.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * 参数初始化
     */
    public void init(double area) {

        this.Area = area;

        // ********************初始化内存*****************************//
        Qsim = new double[this.N];
        Ep = new double[this.N];
        E = new double[this.N];
        //MD = new double[this.N];
        //Qm = new double[this.N];
        Ea = new double[this.N][this.M];
        Srz = new double[this.N][this.M];
        qr = new double[this.N][this.M];
        Suz = new double[this.N][this.M];
        qv = new double[this.N][this.M];
        Qv = new double[this.N];
        SD = new double[this.N][this.M];
        Rs = new double[this.N][this.M];
        ave_z = new double[this.N];
        Qb = new double[this.N];
        Qs = new double[this.N];
        Qt = new double[this.N];

        // **********************单位转换***************************//
        T0 = T0 / 3600; // 包含导水率由m/小时转换为m/秒
        td = td * 3600;
        for (int m = 0; m < M; m++) {
            lambda += dmzs[m] * A[m];
        }
        lambda = lambda / Area;
        Q0 = Area * T0 * Math.exp(-lambda);// 采用这种算法基流变化不大，存在问题？
        for (int n = 0; n < N; n++) {
            Qv[n] = 0;
            Qs[n] = 0;
            P[n] = P[n] / 1000.0;// 降水量由毫米转换为米
            E0[n] = E0[n] / 1000.0;// 水面蒸发量由毫米转换为米
        }
    }

    /**
     * 产流计算
     */
	/*public void runoff() {
		for (int n = 0; n < this.N; n++) {
			Ep[n] = Kc * E0[n];
			// 计算地下平均水位
			if (n == 0) {
				ave_z[n] = -Szm * Math.log(Qb1 / Q0);
			} else {
				ave_z[n] = ave_z[n - 1] - (Qv[n - 1] - Qb[n - 1]) / Area * dt;
			}
			for (int m = 0; m < M; m++) {
				// 计算根系区缺水量
				if (n == 0) {
					Ea[n][m] = Ep[n] * (1 - (Srmax - SR0 - P[n]) / Srmax);
					if (Ea[n][m] > (SR0 + P[n])) {
						Ea[n][m] = SR0 + P[n];
					}
					if ((Srmax - SR0 + Ea[n][m] - P[n]) < 0) {
						qr[n][m] = SR0 + P[n] - Ea[n][m] - Srmax;
						Srz[n][m] = 0;
					} else {
						qr[n][m] = 0;
						Srz[n][m] = Srmax - SR0 + Ea[n][m] - P[n];
					}
				} else {
					Ea[n][m] = Ep[n] * (1 - (Srz[n - 1][m] - P[n]) / Srmax);
					if (Ea[n][m] > (Srmax - Srz[n - 1][m] + P[n])) {
						Ea[n][m] = Srmax - Srz[n - 1][m] + P[n];
					}
					if ((Srz[n - 1][m] + Ea[n][m] - P[n]) < 0) {
						qr[n][m] = -Srz[n - 1][m] + P[n] - Ea[n][m];
						Srz[n][m] = 0;
					} else {
						qr[n][m] = 0;
						Srz[n][m] = Srz[n - 1][m] + Ea[n][m] - P[n] + qr[n][m];
					}
				}
				// 计算蒸发量
				E[n] += Ea[n][m] * A[m] / Area;
				// 计算饱和地下水深
				// SD[n][m] = ave_z[n] - Szm * (dmzs[m] - lambda);

				if (n == 0) {
					// 假设经过一段时间的干旱后，流域出流只有壤中流，非饱和区含水量为0

					if (Suz0 <= 0) {
						qv[n][m] = 0;
						// Rs[n][m] = Math.max(Suz0 + qr[n][m] -
						// Math.max(SD[n][m], 0), 0);
						Rs[n][m] = -Suz0;

					} else {
						Rs[n][m] = 0;
						qv[n][m] = (Suz0 + qr[n][m] - Rs[n][m]) / (Suz0 * td);
						if (qv[n][m] * dt > (Suz0 + qr[n][m] - Rs[n][m])) {
							qv[n][m] = (Suz0 + qr[n][m] - Rs[n][m]) / dt;
						}
					}
					Suz[n][m] = Suz0 - qr[n][m] + Rs[n][m] + qv[n][m];
				} else {

					if (SD[n][m] <= 0) {
						qv[n][m] = 0;
						Rs[n][m] = -Suz[n][m];
						Suz[n][m] = 0;
					} else {
						Rs[n][m] = 0;
						qv[n][m] = (Suz[n - 1][m] + qr[n][m] - Rs[n][m]) / (SD[n - 1][m] * td);
						if (qv[n][m] * dt > (Suz[n - 1][m] + qr[n][m] - Rs[n][m])) {
							qv[n][m] = (Suz[n - 1][m] + qr[n][m] - Rs[n][m]) / dt;
						}
						Suz[n][m] = Suz[n - 1][m] - qr[n][m] + Rs[n][m] + qv[n][m];
					}
					// Suz[n][m] = Suz[n-1][m] -qr[n][m] +Rs[n][m] + qv[n][m];

				}
				SD[n][m] = Suz[n][m];
				// System.out.print(SD[n][m]+"\t"+Rs[n][m]+"\t"+qv[n][m]+"\t"+Suz[n][m]+"\t"+qr[n][m]);
				// System.out.println("\t"+Suz[n][m]);
				// 下渗量
				Qv[n] += qv[n][m] * A[m];// 叠加qv*A得Qv
				// 饱和坡面流
				Qs[n] += Rs[n][m] * A[m] / dt;
			}
			// 计算基流
			Qb[n] = Q0 * Math.exp(-ave_z[n] / Szm);// 基流的计算有问题？
			// System.out.println(Qb[n]+"\t"+Qv[n]);
		}
	}*/

    public void runoff2() {
        for (int n = 0; n < this.N; n++) {

            Ep[n] = Kc * E0[n];

            if (n == 0)
                ave_z[n] = -Szm * Math.log(Qb1 / Q0);
            else
                ave_z[n] = ave_z[n - 1] - (Qv[n - 1] - Qb[n - 1]) / Area * dt;

            if (ave_z[n] < 0)
                ave_z[n] = 0;

            for (int m = 0; m < M; m++) {
                // 先计算根系区缺水量,降水在时段初
                if (n == 0)
                    Srz[n][m] = Srmax - SR0 - P[n];
                else
                    Srz[n][m] = Srz[n - 1][m] - P[n];

                // 缺水量范围限定及进入非饱和区水量计算
                if (Srz[n][m] < 0) {
                    qr[n][m] = -Srz[n][m];
                    Srz[n][m] = 0;
                } else
                    qr[n][m] = 0;

                // 计算非饱和区含水量
                // 假设经过一段时间的干旱后，流域出流只有壤中流，非饱和区含水量为0
                if (n == 0)
                    Suz[n][m] = 0;
                else
                    Suz[n][m] = Suz[n - 1][m] + qr[n][m];

                // 计算饱和地下水深
                SD[n][m] = ave_z[n] - Szm * (dmzs[m] - lambda);

				/*if (SD[n][m] < 0) {

					SD[n][m] = 0;
				}*/

                // 计算蓄满产流
                if (Suz[n][m] > SD[n][m]) {
                    Rs[n][m] = Suz[n][m] - SD[n][m];
                    Suz[n][m] = SD[n][m];

                } else {
                    Rs[n][m] = 0;
                }
                Qs[n] += Rs[n][m] * A[m] / dt;

                // 计算下渗
                if (SD[n][m] > 0) {
                    qv[n][m] = Suz[n][m] / (SD[n][m] * td);

                    if ((qv[n][m] * dt) > Suz[n][m])
                        qv[n][m] = Suz[n][m] / dt;

                    Suz[n][m] = Suz[n][m] - qv[n][m] * dt;

                } else {
                    qv[n][m] = 0;
                }
                Qv[n] += qv[n][m] * A[m];// 叠加qv*A得Qv

                Ea[n][m] = 0.0;
                if (Ep[n] > 0) {
                    Ea[n][m] = Ep[n] * (1 - Srz[n][m] / Srmax);
                    if (Ea[n][m] > (Srmax - Srz[n][m]))
                        Ea[n][m] = (Srmax - Srz[n][m]);
                }

                // 减去蒸发，更新根系区缺水量
                Srz[n][m] = Srz[n][m] + Ea[n][m];

            }
            // 计算基流
            Qb[n] = Q0 * Math.exp(-ave_z[n] / Szm);
            // System.out.println(Qb[n] + Qs[n]);
        }
    }

    /**
     * 计算冰川融化流量
     */
    // public void melt(double area_glacier) {
    // 	this.Area_glacier = area_glacier;
    // 	for (int i = 0; i < this.N; i++) {
    // 		if (T[i] > Tm) {
    // 			// MD[i]=MF*(T[i]-Tm);
    // 			MD[i] = MF * Math.pow(T[i] - Tm, b);
    // 		} else {
    // 			MD[i] = 0;
    // 		}
    // 		Qm[i] = MD[i] * Area_glacier / 1000 / dt;
    // 	}
    // }

    /**
     * 汇流计算
     */
    public void routing()
    {

        // int ntimestep = this.N;

        for (int i = 0; i < this.N; i++) {
            Qsim[i] = Qs[i] + Qb[i] ;
            // Qsim[i] = Qs[i] + Qb[i];
            // Qsim[i] =Qm[i];
            // 加上汇流过程
        }
        Qt[0] = Qsim[0];
        for (int i = 1; i < this.N; i++) {
            Qt[i] = Qsim[i] * Cr + Qsim[i - 1] * (1 - Cr);
        }
        // 汇流过于简化，应采用更加精确的汇流，待修改+fex[i]*Area/dt
        this.dc = nash(this.Qobs, this.Qsim);

        // System.out.println("加入汇流过程 nash: " + dc);



    }

    /**
     * 纳什效率系数计算
     */
    private double nash(double[] Qobs, double[] Qsim) {

        double meanF = 0.0;

        for (int i = 0; i < this.N; i++) {

            meanF = meanF + Qobs[i];
        }
        meanF = meanF / this.N;

        double VsimCumu = 0.0;
        double VobsCumu = 0.0;
        for (int i = 0; i < this.N; i++) {
            VsimCumu = VsimCumu + (Qsim[i] - Qobs[i]) * (Qsim[i] - Qobs[i]);
            VobsCumu = VobsCumu + (Qobs[i] - meanF) * (Qobs[i] - meanF);
        }

		/*for (int i = 0; i < this.N; i++) {
			meanF += Math.abs(Qsim[i] - Qobs[i]) / Qobs[i];
		}*/

        return 1 - VsimCumu / VobsCumu;
        //return 1.0 / meanF;
    }

    public void toTxt(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            String s = "模拟流量" + "\t" + "实测流量" + "\t" + "降雨" + "\t"  + "坡面流" + "\t" + "基流" + "\t" + "蒸发";
            bw.write(s);
            bw.newLine();
            for (int i = 0; i < this.N; i++) {
                s = Qt[i] + "\t" + Qobs[i] + "\t" + P[i] * 1000 +  "\t" + Qs[i] + "\t" + Qb[i] + "\t"
                        + E[i] * 1000;
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            fw.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public static void main(String[] args) {
        TopModel ca = new TopModel();
		ca.setT0(16.468735734435754);
		ca.setTd(95.86794547617808);
		ca.setSzm(0.009077095182371125);
		ca.setSrmax(0.18094147576925806);
		ca.setQb1(4.818252106954927);
		ca.setSR0(0.3802873826459887);
		ca.setSuz0(0.6105480761789053);


        ca.setKc(0.3916714446005215);
		ca.setCr(0.9367056187170248);
		ca.setMF(1.7043592513235497);
		ca.setTm(0.36429300775615836);
		ca.setB(0.39564475753463607);
		ca.readData("data_day.txt");
		ca.readTWI("海100类.txt");
		ca.init(322000000);
		ca.runoff2();
		ca.routing();
		ca.toTxt("模型计算结果.txt");
		System.out.println(ca.getDc());
    }
}
