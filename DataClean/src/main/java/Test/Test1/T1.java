package Test.Test1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class T1 {
//  public static Path IN_PATH = new Path("/Volumes/software/IdeaProjects/DataClean/src/test/mapreduce/Test1/T1/input");
//  public static Path OUT_PATH = new Path("/Volumes/software/IdeaProjects/DataClean/src/test/mapreduce/Test1/T1/output");
    public static Path OUT_PATH = new Path("hdfs://localhost:9000/MapReduce/Test1/T1/output");
    public static Path IN_PATH = new Path("hdfs://localhost:9000/MapReduce/Test1/T1/input");

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "T1");
        job.setJarByClass(T1.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.setInputPaths(job, IN_PATH);


        job.setMapperClass(M1.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setReducerClass(R1.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileSystem fileSystem = OUT_PATH.getFileSystem(conf);
        if (fileSystem.exists(OUT_PATH)) {
            fileSystem.delete(OUT_PATH, true);
        }

        job.setOutputFormatClass(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, OUT_PATH);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

class M1 extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Counter counter = context.getCounter("Map", "In");
        counter.increment(1L);
        String[] line = value.toString().split("\t");
        context.write(new Text(line[0]), new DoubleWritable(Double.parseDouble(line[2])));
    }
}

class R1 extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        Double sum = 0.0;
        int count = 0;
        for (DoubleWritable i : values) {
            sum += i.get();
            count++;
        }
        Double result = sum / count;
        context.write(key, new DoubleWritable(result));
    }
}
