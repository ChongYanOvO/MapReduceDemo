package Sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class RunJob {

//    final static Path INPUT_PATH = new Path("/Volumes/software/IdeaProjects/DataClean/src/test/mapreduce/Sort/input");
//    final static Path OUTPUT_PATH = new Path("/Volumes/software/IdeaProjects/DataClean/src/test/mapreduce/Sort/output");
    final static Path OUTPUT_PATH = new Path("hdfs://localhost:9000/mapreduce/Sort/output");
    final static Path INPUT_PATH = new Path("hdfs://localhost:9000/mapreduce/Sort/input");

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Sort");
        job.setJarByClass(RunJob.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.setInputPaths(job, INPUT_PATH);

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(SortBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileSystem fileSystem = OUTPUT_PATH.getFileSystem(conf);
        if (fileSystem.exists(OUTPUT_PATH)) {
            fileSystem.delete(OUTPUT_PATH, true);
        }

        job.setOutputFormatClass(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, OUTPUT_PATH);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
