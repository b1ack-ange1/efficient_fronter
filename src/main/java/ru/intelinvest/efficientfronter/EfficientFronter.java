/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.intelinvest.efficientfronter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
    @Getter
    @Setter
    private List<EntryData> list = new ArrayList<>();
    @Getter
    @Setter
    private String json;
    @Getter
    @Setter
    private List<EntityType> listTypes = new ArrayList<>();
    private Set<EntityType> defaultTypes = new HashSet<>();
    private Set<EntityType> selectedTypes = new HashSet<>();
    private Map<EntityType, List<Double>> typesMap = new HashMap<>();
    @Getter
    @Setter
    private EntityType type = EntityType.MMVB;
    @Getter
    @Setter
    private EntityType type1 = EntityType.GOLD;
    @Getter
    @Setter
    private EntityType type2;
    @Getter
    @Setter
    private EntityType type3;
    @Getter
    @Setter
    private EntityType type4;
    @Getter
    @Setter
    private EntityType type5;
    @Getter
    @Setter
    private EntityType type6;
    @Getter
    @Setter
    private EntityType type7;
    @Getter
    @Setter
    private EntityType type8;
    @Getter
    @Setter
    private EntityType type9;
    @Getter
    @Setter
    private Map<Integer, EntityType> entityMap = new HashMap<>();

    @PostConstruct
    private void init() {
        listTypes.add(EntityType.MMVB);
        listTypes.add(EntityType.GOLD);
        listTypes.add(EntityType.DEPOSIT);
        // добавляем типы по умолчанию
        defaultTypes.add(EntityType.MMVB);
        defaultTypes.add(EntityType.GOLD);
        typesMap.put(EntityType.MMVB, mmvbYields);
        typesMap.put(EntityType.GOLD, goldYields);
        typesMap.put(EntityType.DEPOSIT, depositYields);
        entityMap.put(new Integer("2"), type2);
        entityMap.put(new Integer("3"), type3);
        entityMap.put(new Integer("4"), type4);
    }

    public void calculate() {
        defaultTypes.clear();
        defaultTypes.add(type);
        defaultTypes.add(type1);
        System.out.println("SELECTED SIZE: " + defaultTypes.size());
        List<Pair> pairs = createPairs();
        json = createJson(pairs);
        System.out.println("JSON: " + json);
    }

    private List<Pair> createPairs() {
        List<Pair> pairs = new ArrayList<>();
        pairs.add(new Pair(type, type1));
        for (EntityType e : defaultTypes) {
            for (Entry<Integer, EntityType> entry : entityMap.entrySet()) {
                if (entry.getValue() != null) {
                    System.out.println("ENTRY: " + entry.getValue());
                    System.out.println("ENTRY1: " + e);
                    String s = entry.getValue() + "";
                    EntityType t =EntityType.valueOf(s);
                    pairs.add(new Pair(e, t));
                }
            }
        }
        System.out.println("PAIRS SIZE: " + pairs.size());
        return pairs;
    }

    private String createJson(List<Pair> pairs) {
        JSONArray array = new JSONArray();
        int count = 0;
        for (Pair pair : pairs) {
            System.out.println("PAIR_" + count + ": " + pair);
            for (int i = 0; i <= 100; i++) {
                double x = 100.0 - i;
                double y = i + 0.0;
                DescriptiveStatistics stat = calsulateStatistic(calculateArray(typesMap.get(pair.first), typesMap.get(pair.second), x, y));
                EntryData ed = new EntryData(x, y, getRoundedDouble(stat.getGeometricMean() - 100, 2), getRoundedDouble(stat.getStandardDeviation(), 2));
                list.add(ed);
                JSONObject ob = new JSONObject();
                ob.put("y" + count, ed.getV1());
                ob.put("x" + count, ed.getV2());
                ob.put("value", type.getDesc() + ": " + x + ", " + type1.getDesc() + ": " + y);
                array.add(ob);
            }
            count++;
        }
        return array.toJSONString();
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

    public enum EntityType {
        MMVB("Акции"),
        GOLD("Золото"),
        DEPOSIT("Депозиты");

        private String desc;

        private EntityType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public class Pair {

        EntityType first;
        EntityType second;

        public Pair(EntityType first, EntityType second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.first);
            hash = 97 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair other = (Pair) obj;
            if (this.first != other.first) {
                return false;
            }
            if (this.second != other.second) {
                return false;
            }
            if (this.first == other.second && this.second == other.first) {
                return true;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Pair{" + "first=" + first + ", second=" + second + '}';
        }
    }

    private double getRoundedDouble(double d, int round) {
        return new BigDecimal(d).setScale(round, RoundingMode.HALF_UP).doubleValue();
    }
}
