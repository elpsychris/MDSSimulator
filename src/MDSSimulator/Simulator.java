package MDSSimulator;

import sun.security.krb5.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Simulator {
    private final static String CONFIG = "config.ini";
    private static Map<String, String> CONFIG_SETTING = new HashMap<>();
    public static void main(String[] args) throws InterruptedException {
        String sample = "8=HNX.TDS.1\u00019=231\u000135=SI\u000149=HNX\u000152=20171010-07:55:51\u000115=75643\u000155=ACB\u0001167=ST\u0001225=00010101-12:01:00";
        String sample2 = "\u0001333=6800\u0001326=0\u0001260=8000\u0001425=UPC_BRD_01\u00013301=2695000\u0001334=10300\u0001140=8000\u0001388=20171010\u0001399=07:58:46\u0001400=100\u0001109=5500000.000000\u000117=175\u0001232=0\u0001327=0\u0001";
        String newValue = "\u0001332=";
        int count = 0;
        boolean isAppend = false;
        readFromFile(CONFIG);
        String sampleData = getSampleData(CONFIG_SETTING.get("SampleName"));
        String outputFileName = getFileName(CONFIG_SETTING.get("Name"));
        while (true) {
            Random random = new Random(Calendar.getInstance().getTimeInMillis());
            Integer newInt = random.nextInt(10000) + 1;
            String output = sample + newValue + newInt.toString() + sample2;
            count++;
            if (count > 50) {
                count = 0;
                isAppend = false;
            }
            writeToFile(outputFileName, output, isAppend, sampleData);
            isAppend = true;
            Thread.sleep(CONFIG_SETTING.get("Interval") == null?5000:Integer.parseInt(CONFIG_SETTING.get("Interval")) * 1000);
        }
    }

    private static void writeToFile(String path, String newline, boolean isAppend, String sampleData) {
        try {
            FileWriter fw = new FileWriter(path,isAppend);
            BufferedWriter bw = new BufferedWriter(fw);
            if (isAppend) {
                bw.write(String.format("\n%s", newline));
                System.out.println("Append: " + newline);
            } else {
                bw.write(String.format("%s\n%s", sampleData, newline));
                System.out.println("Write: " + newline);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFromFile(String path) {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach(Simulator::updateConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateConfig(String line) {
        String[] info = line.split("=");
        if (info.length == 2) {
            CONFIG_SETTING.put(info[0],info[1]);
        }
    }

    private static String getFileName(String fileConfigName) {
        if (fileConfigName == null) {
            fileConfigName = "Log";
        }
        Date curDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
        return String.format("%s_%s.log",fileConfigName,formatter.format(curDate));
    }

    private static String getSampleData(String fileSampleName) {
        String result = "";
        if (fileSampleName == null) {
            fileSampleName = "Log_Sample.log";
        }
        try(Stream<String> stream = Files.lines(Paths.get(fileSampleName))) {
            result = stream.collect(Collectors.joining("\n"));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}


