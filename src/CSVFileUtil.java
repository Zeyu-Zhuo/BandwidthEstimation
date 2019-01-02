import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.IOException;
import java.util.ArrayList;

public class CSVFileUtil {
    private String filepath;
    private ArrayList<ArrayList<Double>> CSVal = new ArrayList<>();
    private CsvReader cr;

    public CSVFileUtil(String path,String filename) throws IOException {
        this.filepath = path +"\\"+ filename;
        cr = new CsvReader(this.filepath);
        setAlandOffset();
    }
    private void setAlandOffset() throws IOException {
        cr.readHeaders();
        int a = cr.getHeaders().length;
        ArrayList<Double> temp;
        while (cr.readRecord()){
            // 读一整行
            temp = new ArrayList<>(a);
            for(int i = 0;i<a;i++){
                temp.add(Double.parseDouble(cr.get(i)));
            }
            this.CSVal.add(temp);
        }

    }

    public ArrayList<ArrayList<Double>> getAl(){
        return this.CSVal;
    }
    public static void CSVWrite(String filepath,String[] content) throws IOException {
            CsvWriter csvWriter = new CsvWriter(filepath);
            csvWriter.writeRecord(content);
    }


}
