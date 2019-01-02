import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KmeanUtil
{
    // 用来聚类的点集
    public List<Point> points;
    // 将聚类结果保存到文件
    FileWriter out = null;

    // 格式化double类型的输出，保留两位小数
    DecimalFormat dFormat = new DecimalFormat("00.00");

    // 具体执行聚类的对象
    public KMeansCluster kMeansCluster;

    // 簇的数量，迭代次数
    public int numCluster = 5;
    public int numIterator = 200;

    // 点集的数量，生成指定数量的点集
    public int numPoints = 50;

    //聚类结果保存路径
    public static final String FILEPATH="E:\\JavaProject\\BandwidthEstimation\\res.txt";

    public KmeanUtil(int numPoints, int cluster_number, int iterrator_number,double[] Interval) {

        this.numPoints = numPoints;
        this.numCluster = cluster_number;
        this.numIterator = iterrator_number;
        init(Interval);
        //使用KMeansCluster对象进行聚类
        runKmeans();
        //printRes();
        saveResToFile(KmeanUtil.FILEPATH);

    }

    public void init(double[] Interval)
    {
        this.initPoints(Interval);
        kMeansCluster = new KMeansCluster(numCluster, numIterator, points);
    }

    public void runKmeans()
    {
        kMeansCluster.runKmeans();
    }

    // 初始化点集
    public void initPoints(double[] Interval)
    {
        points = new ArrayList<>(numPoints);
        Point temPoint;
        for(int i = 0;i<Interval.length;i++){
            temPoint = new Point(Interval[i],0.0);
            points.add(temPoint);
        }
        /*KmeanUtil.Point tmpPoint;

        for (int i = 0; i < numPoints; i++)
        {
            tmpPoint = new KmeanUtil.Point(Math.random() * 150, Math.random() * 100);
            points.add(tmpPoint);
        }*/
    }

    public void printRes()
    {

        System.out.println("==================Centers-I====================");
        for (Point center : kMeansCluster.centers)
        {
            System.out.println(center.toString());
        }

        System.out.println("==================Points====================");

        for (Point point : points)
        {
            System.out.println(point.toString());
        }
    }

    public void saveResToFile(String filePath)
    {
        try
        {
            out = new FileWriter(new File(filePath),true);

            for (Point point : points)
            {
                out.write(String.valueOf(point.getClusterID()));
                out.write("  ");

                out.write(dFormat.format(point.getX()));
                out.write("  ");
                //out.write(dFormat.format(point.getY()));
                out.write("\r\n");
            }
            for(int i = 0;i<3;i++){
                out.write("\n");
            }
            out.flush();
            out.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<Double> getBestArraylist(){
        ArrayList<ArrayList<Double>> al = getArraryList();
        double temp = 999999999;
        int index = 0;
        for(int i = 0;i<3;i++){
            double sum = 0.0;
            for(int j = 0;j<al.get(i).size();j++){
                sum = al.get(i).get(j) + sum;
            }
            sum = sum /al.get(i).size();
            if(sum < temp){
                index = i;
                temp = sum;
            }
        }
        return al.get(index);
/*        ArrayList<Integer> index = new ArrayList<>(3);
        int temp = 0;
        for(int i = 0;i<3;i++){
            if(al.get(i).size()>=temp){
                temp = al.get(i).size();
                index.add(i);
            }
        }
        if(index.size()==1){
            return al.get(index.get(0));
        }
        else if(index.size() == 2){
            return getMaxNormalDistribution(al.get(index.get(0)),al.get(index.get(1)));
        }
        else {
            return getMaxNormalDistribution(al.get(index.get(0)), al.get(index.get(1)), al.get(index.get(2)));
        }*/
    }

    private ArrayList<Double> getMaxNormalDistribution(ArrayList<Double> al1,ArrayList<Double> al2){
        StandardDeviation standardDeviation = new StandardDeviation();
        double[] temp1 = listToDoubles(al1);
        double[] temp2 = listToDoubles(al2);
        double res1 = standardDeviation.evaluate(temp1);
        double res2 = standardDeviation.evaluate(temp2);
        if(res1>res2)
            return al2;
        else
            return al1;
    }
    private ArrayList<Double> getMaxNormalDistribution(ArrayList<Double> al1,ArrayList<Double> al2,ArrayList<Double> al3){
        ArrayList<Double> temp = getMaxNormalDistribution(al1,al2);
        return getMaxNormalDistribution(temp,al3);
    }

    private double[] listToDoubles(ArrayList<Double> al){
        double[] res = new double[al.size()];
        for(int i = 0;i<al.size();i++){
            res[i] = al.get(i);
        }
        return res;
    }

    private ArrayList<ArrayList<Double>> getArraryList(){
        ArrayList<Double> al1 = new ArrayList<>();
        ArrayList<Double> al2 = new ArrayList<>();
        ArrayList<Double> al3 = new ArrayList<>();
        ArrayList<ArrayList<Double>> al = new ArrayList<>();
        for(Point point : points){
            if(point.clusterID==1)
                al1.add(point.x);
            else if(point.clusterID==2)
                al2.add(point.x);
            else
                al3.add(point.x);
        }
        al.add(al1);
        al.add(al2);
        al.add(al3);
        return al;
    }

    public static class KMeansCluster
    {
        // 聚类中心数
        public int k = 5;

        // 迭代最大次数
        public int maxIter = 50;

        // 测试点集
        public List<Point> points;

        // 中心点
        public List<Point> centers;

        public static final double MINDISTANCE = 10000.00;

        public KMeansCluster(int k, int maxIter, List<Point> points) {
            this.k = k;
            this.maxIter = maxIter;
            this.points = points;

            //初始化中心点
            initCenters();
        }

        /*
         * 初始化聚类中心
         * 这里的选取策略是，从点集中按序列抽取K个作为初始聚类中心
         */
        public void initCenters()
        {
            centers = new ArrayList<>(k);

            for (int i = 0; i < k; i++)
            {
                Point tmPoint = points.get(i * 2);
                Point center = new Point(tmPoint.getX(), tmPoint.getY());
                center.setClusterID(i + 1);
                centers.add(center);
            }
        }


        /*
         * 停止条件是满足迭代次数
         */
        public void runKmeans()
        {
            // 已迭代次数
            int count = 1;

            while (count++ <= maxIter)
            {
                // 遍历每个点，确定其所属簇
                for (Point point : points)
                {
                    assignPointToCluster(point);
                }

                //调整中心点
                adjustCenters();
            }
        }



        /*
         * 调整聚类中心，按照求平衡点的方法获得新的簇心
         */
        public void adjustCenters()
        {
            double sumx[] = new double[k];
            double sumy[] = new double[k];
            int count[] = new int[k];

            // 保存每个簇的横纵坐标之和
            for (int i = 0; i < k; i++)
            {
                sumx[i] = 0.0;
                sumy[i] = 0.0;
                count[i] = 0;
            }

            // 计算每个簇的横纵坐标总和、记录每个簇的个数
            for (Point point : points)
            {
                int clusterID = point.getClusterID();

                // System.out.println(clusterID);
                sumx[clusterID - 1] += point.getX();
                sumy[clusterID - 1] += point.getY();
                count[clusterID - 1]++;
            }

            // 更新簇心坐标
            for (int i = 0; i < k; i++)
            {
                Point tmpPoint = centers.get(i);
                tmpPoint.setX(sumx[i] / count[i]);
                tmpPoint.setY(sumy[i] / count[i]);
                tmpPoint.setClusterID(i + 1);

                centers.set(i, tmpPoint);
            }
        }


        /*划分点到某个簇中，欧式距离标准
         * 对传入的每个点，找到与其最近的簇中心点，将此点加入到簇
         */
        public void assignPointToCluster(Point point)
        {
            double minDistance = MINDISTANCE;

            int clusterID = -1;

            for (Point center : centers)
            {
                double dis = EurDistance(point, center);
                if (dis < minDistance)
                {
                    minDistance = dis;
                    clusterID = center.getClusterID();
                }
            }
            point.setClusterID(clusterID);

        }

        //欧式距离，计算两点距离
        public double EurDistance(Point point, Point center)
        {
            double detX = point.getX() - center.getX();
            double detY = point.getY() - center.getY();

            return Math.sqrt(detX * detX + detY * detY);
        }
    }

    public static class Point
    {
        // 点的坐标
        private Double x;
        private Double y;

        // 所在类ID
        private int clusterID = -1;

        public Point(Double x, Double y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString()
        {
            return String.valueOf(getClusterID()) + " " + String.valueOf(this.x) + " " + String.valueOf(this.y);
        }

        public Double getX()
        {
            return x;
        }

        public void setX(Double x)
        {
            this.x = x;
        }

        public Double getY()
        {
            return y;
        }

        public void setY(Double y)
        {
            this.y = y;
        }

        public int getClusterID()
        {
            return clusterID;
        }

        public void setClusterID(int clusterID)
        {
            this.clusterID = clusterID;
        }
    }
}

