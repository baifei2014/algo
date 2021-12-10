package com.likecho.algo.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * 基于道格拉斯-普克算法坐标点抽稀
 */
public class Douglas {
    /**
     * 抽稀阈值，阈值越大，抽稀后的点集越少
     */
    private static double threshold = 0;

    public Douglas(double threshold) {
        this.threshold = threshold;
    }

    /**
     * 坐标点抽稀
     * <p>
     * 调用示例:
     * <pre>{@code
     *      List<String> points = Arrays.asList("113.943546,35.290477", "113.942737,35.289932", ...);
     *      Douglas douglas = new Douglas(0.0001);
     *      List res = douglas.compress(points);
     * }</pre>
     *
     * @param points 点集列表, eg: "113.943546,35.290477"
     * @return 抽稀后的点集
     */
    public List compress(List<String> points) {
        points = points.stream()
                .filter(p -> StringUtils.split(p, ",").length == 2)
                .collect(Collectors.toList());
        if (points.size() < 3) {
            return points;
        }
        List<String> result = new ArrayList<>();
        Stack<List<String>> s = new Stack<>();
        s.push(points);
        while (!s.isEmpty()) {
            points = s.pop();
            String start = points.get(0);
            String end = points.get(points.size() - 1);
            double maxDist = 0;
            int pivot = 0;
            for (int i = 1; i < points.size(); i++) {
                double dist = distance(points.get(i), start, end);
                if (dist > maxDist) {
                    maxDist = dist;
                    pivot = i;
                }
            }
            if (maxDist > threshold) {
                s.push(points.subList(0, pivot + 1));
                s.push(points.subList(pivot, points.size()));
                continue;
            }
            if (!result.contains(end)) {
                result.add(end);
            }
            if (!result.contains(start)) {
                result.add(start);
            }
        }
        return result;
    }

    /**
     * 获取点到起始坐标点直线的距离
     *
     * @param p
     * @param startP
     * @param endP
     * @return 点到直线距离
     */
    private double distance(String p, String startP, String endP) {
        List<Double> point = Arrays.asList(StringUtils.split(p, ","))
                .stream()
                .map(n -> Double.parseDouble(n))
                .collect(Collectors.toList());
        List<Double> startPoint = Arrays.asList(StringUtils.split(startP, ","))
                .stream()
                .map(n -> Double.parseDouble(n))
                .collect(Collectors.toList());
        List<Double> endPoint = Arrays.asList(StringUtils.split(endP, ","))
                .stream()
                .map(n -> Double.parseDouble(n))
                .collect(Collectors.toList());
        if (Double.compare(startPoint.get(0), endPoint.get(0)) == 0) {
            return Math.abs(point.get(0) - startPoint.get(0));
        }
        Double slope = (endPoint.get(1) - startPoint.get(1)) / (endPoint.get(0) - startPoint.get(0));
        Double intercept = startPoint.get(1) - (slope * startPoint.get(0));
        return Math.abs(slope * point.get(0) - point.get(1) + intercept) / Math.sqrt(Math.pow(slope, 2) + 1);
    }
}