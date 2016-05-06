/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.intelinvest.efficientfronter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author nedelko
 */
@Named
@ViewScoped
public class EfficientFronter implements Serializable {

    private List<Double> mmvbYields = Arrays.asList(0.614, 0.0729, 0.8308, 0.675, 0.1154, -0.672, 1.2114, 0.2321, -0.1693, 0.0341);
    private List<Double> depositYields = Arrays.asList(0.1090, 0.0930, 0.0870, 0.0790, 0.0720, 0.0760, 0.1040, 0.0680, 0.0540, 0.0650);
    private List<Double> goldYields = Arrays.asList(0.1125, -0.0136, 0.2228, 0.1336, 0.2146, 0.2567, 0.3108, 0.2909, 0.1784, -0.0069);
    private List<EntryData> list = new ArrayList<>();
    private String json;

    @PostConstruct
    private void init() {
        doChartData();
    }

    private void doChartData() {
        JSONArray array = new JSONArray();
        for (int i = 0; i <= 100; i++) {
            double x = 100.0 - i;
            double y = i + 0.0;
            DescriptiveStatistics stat = calsulateStatistic(calculateArray(mmvbYields, depositYields, x, y));
            DescriptiveStatistics statMG = calsulateStatistic(calculateArray(mmvbYields, goldYields, x, y));
            DescriptiveStatistics statDG = calsulateStatistic(calculateArray(depositYields, goldYields, x, y));
            EntryData ed = new EntryData(x, y, stat.getGeometricMean() - 100, stat.getStandardDeviation());
            list.add(ed);
            JSONObject ob = new JSONObject();
            ob.put("y", stat.getGeometricMean() - 100);
            ob.put("x", stat.getStandardDeviation());
            ob.put("value", "stocks: " + x + ", dep: " + y);
            ob.put("y2", statMG.getGeometricMean() - 100);
            ob.put("x2", statMG.getStandardDeviation());
            ob.put("value2", "stocks: " + x + ", gold: " + y);
            ob.put("y3", statDG.getGeometricMean() - 100);
            ob.put("x3", statDG.getStandardDeviation());
            ob.put("value3", "dep: " + x + ", gold: " + y);
            array.add(ob);
        }
        json = array.toJSONString();
        System.out.println(json);
    }

    private double[] calculateArray(List<Double> list1, List<Double> list2, double share1, double share2) {
        double[] array = new double[list1.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (((1 + list1.get(i)) * share1 + (1 + list2.get(i)) * share2));
        }
        return array;
    }

    private DescriptiveStatistics calsulateStatistic(double[] array) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        // Add the data from the array
        for (int i = 0; i < array.length; i++) {
            stats.addValue(array[i]);
        }
        return stats;
    }

    public String getJson() {
        return json;
    }
}
