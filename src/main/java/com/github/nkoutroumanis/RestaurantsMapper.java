package com.github.nkoutroumanis;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicholaskoutroumanis
 */
public class RestaurantsMapper extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, HotelsRestaurantsWritable> {

    private static Grid grid;

    @Override
    public void configure(JobConf job) {
        grid = Grid.newInstance(job.getFloat("radius", 0), job.getInt("xCellsDivision", 1), job.getInt("yCellsDivision", 1));
    }

    @Override
    public void map(LongWritable key, Text value, OutputCollector<IntWritable, HotelsRestaurantsWritable> oc, Reporter rprtr) throws IOException {
        String s = value.toString();
        String[] tokens = s.split("|");

        float[] coordinates = new float[2];
        coordinates[0] = Float.valueOf(tokens[3]);//lat
        coordinates[1] = Float.valueOf(tokens[4]);//lon

        if (grid.contains(coordinates[0], coordinates[1])) {
            HotelsRestaurantsWritable hrw = new HotelsRestaurantsWritable(false, tokens[0] + " - " + tokens[1], coordinates[0], coordinates[1], tokens[5]);

            List<Integer> restaurantCells = grid.putRestaurantsInCells(coordinates[0], coordinates[1]);
            for (int i : restaurantCells) {
                oc.collect(new IntWritable(i), hrw);
            }

        }

    }
//    @Override
//    public void map(LongWritable key, Text value, OutputCollector<ArrayPrimitiveWritable, ArrayWritable> oc, Reporter rprtr) throws IOException {
//        String s = value.toString();
//        String[] tokens = s.split("|");
//
//        float[] coordinates = new float[2];
//        coordinates[0] = Float.valueOf(tokens[3]);
//        coordinates[1] = Float.valueOf(tokens[4]);
//
//        if (Grid.INSTANCE.contains(coordinates[0], coordinates[1])) {
//            String[] keywords = tokens[5].split(", ");
//
//            String[] array = new String[keywords.length + 1];
//            array[0] = tokens[0] + " - " + tokens[1];//the first element of the array is the id and name of restaurant
//
//            for (int i = 1; i < array.length; i++) {
//                array[i] = keywords[i - 1];
//            }
//
//            oc.collect(new ArrayPrimitiveWritable(coordinates), new ArrayWritable(array));
//        }
//    }
}