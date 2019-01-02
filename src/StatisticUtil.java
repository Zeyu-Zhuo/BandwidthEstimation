import net.sf.javaml.distance.fastdtw.util.Arrays;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;

public class StatisticUtil{
    private ArrayList<ArrayList<Record>> segmentAl;

    public StatisticUtil(ArrayList<ArrayList<Double>> Al) {
        segmentAl = new ArrayList<>(Al.size());
        ArrayList<Record> recordAl = new ArrayList<>(80);
        for(int i = 1;i<Al.size();i++){
            double arrTime = Al.get(i).get(0);
            int Bytes = Al.get(i).get(1).intValue();
            if(Bytes == 0 && i + 2<Al.size()){
                i = i + 2;
                this.segmentAl.add(recordAl);
                recordAl = new ArrayList<>(80);
                continue;
            }
            /*else if(i + 2>=Al.size())
                break;*/
            int viBitrate = Al.get(i).get(2).intValue();
            int segIndex = Al.get(i).get(3).intValue();
            int throWidth = Al.get(i).get(4).intValue();
            double Interval = Al.get(i).get(0) - Al.get(i - 1).get(0);
            Record record = new Record(arrTime,Bytes,viBitrate,segIndex,throWidth,Interval);
            recordAl.add(record);
        }
        segmentAl.add(recordAl);
    }

    public ArrayList<ArrayList<Record>> getSegmentAl() {
        return segmentAl;
    }

    public static double[] getInterval(ArrayList<Record> recordAl){
        double[] Interval = new double[recordAl.size()];
        for(int i = 0;i<recordAl.size();i++){
            Interval[i] = recordAl.get(i).Interval;
        }
        return Interval;
    }

    public static double getMean(double[] Interval){
        double sum = 0;
        for(int i = 0;i<Interval.length;i++){
            sum = sum + Interval[i];
        }
            sum = sum / (Interval.length + 1);
        return sum;
    }

    public static double getStandardDeviation(double[] Interval){
        StandardDeviation standardDeviation = new StandardDeviation();
        return standardDeviation.evaluate(Interval);
    }

    public static double[] culProbability(double Mean,double staDeviation,double[] Interval){
        NormalDistribution normalDistribution  = new NormalDistribution(Mean,staDeviation);
        double[] Probability = new double[Interval.length];
        for(int i = 0;i < Interval.length;i++){
            Probability[i] = Math.abs(normalDistribution.cumulativeProbability(Interval[i])-0.5);
        }
        return Probability;
    }

    public static double culBps
            (ArrayList<Record> Segment,double[] Interval,ArrayList<Double> normInterval){
        ArrayList<Double> temp = new ArrayList<Double>(Arrays.toCollection(Interval));
        int index = 0;
        int sum = 0;
        double totalInterval = 0.0;
        for(int i = 0;i<normInterval.size();i++){
            index = temp.indexOf(normInterval.get(i));
            sum = sum + Segment.get(index).Bytes;
            totalInterval = totalInterval + normInterval.get(i);
        }

        return sum / totalInterval;
    }

    static class Record{
        public double arrTime;
        public int Bytes;
        public int viBitrate;
        public int segIndex;
        public int throWidth;
        public double Interval;
        public Record
        (double arrTime, int bytes, int viBitrate, int segIndex, int throWidth, double interval) {
            this.arrTime = arrTime;
            this.Bytes = bytes;
            this.viBitrate = viBitrate;
            this.segIndex = segIndex;
            this.throWidth = throWidth;
            this.Interval = interval;
        }

        @Override
        public String toString() {
            return "StatisticUtil.Record{" +
                    "arrTime=" + arrTime +
                    ", Bytes=" + Bytes +
                    ", viBitrate=" + viBitrate +
                    ", segIndex=" + segIndex +
                    ", throWidth=" + throWidth +
                    ", Interval=" + Interval +
                    '}';
        }
    }


}

