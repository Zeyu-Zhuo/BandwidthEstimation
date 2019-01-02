import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws Exception {
        /*读入数据，把csv文件每条记录存储为ArrayList<Double>，整个文件的数据存为ArrayList<ArrayList<Double>>*/
        CSVFileUtil cu = new CSVFileUtil("D:\\Thesis","chrome_chunk-stats.csv");
        ArrayList<ArrayList<Double>> Al = cu.getAl();
        /*把得到的文件double数组信息按segment断号重新构建数据结构ArrayList<ArrayList<StatisticUtil.Record>>
        * 每个ArrayList<StatisticUtil.Record>包含一个segment数据段的除去开头两条记录结尾一条记录的所有信息
        * */
        StatisticUtil statisticUtil = new StatisticUtil(Al);
        ArrayList<ArrayList<StatisticUtil.Record>> segmentAl = statisticUtil.getSegmentAl();
       int i = 0;
       /*遍历所有segment进行kmean聚类，并把每次聚类得到的平均时间间隔最小的一类用txt文件存储*/
         for(ArrayList<StatisticUtil.Record> segment : segmentAl) {
            double[] Interval = StatisticUtil.getInterval(segment);
            KmeanUtil kmeans = new KmeanUtil(Interval.length, 3, 200, Interval);
            ArrayList<Double> normalInterval = kmeans.getBestArraylist();
            double res = StatisticUtil.culBps(segment, Interval, normalInterval);
            System.out.println("seg = " + i++ + "      " + res * 8 * 1000);
        }
    }
}
