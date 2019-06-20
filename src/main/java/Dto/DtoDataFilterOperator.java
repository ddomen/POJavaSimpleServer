package Dto;

import Utils.UObject;

import java.util.ArrayList;
import java.util.List;

public class DtoDataFilterOperator extends Dto{
    public Object $not;
    public List<Object> $in;
    public List<Object> $nin;
    public List<DtoDataFilter> $or;
    public List<DtoDataFilter> $and;

    public Double $gt;
    public Double $gte;
    public Double $lt;
    public Double $lte;
    public List<Double> $bt;


    public List<DtoDataSet> Apply(List<DtoDataSet> dataset, String property){
        List<DtoDataSet> result = new ArrayList<DtoDataSet>();
        for(DtoDataSet data : dataset){
            Object current = UObject.Get(data, property.toUpperCase());
            if($not != null && (current == $not || current.equals($not))) continue;
            if($in != null && !$in.contains(current)) continue;
            if($nin != null && $nin.contains(current)) continue;
            if($gt != null && (Double)current <= $gt) continue;
            if($gte != null && (Double)current < $gt) continue;
            if($lt != null && (Double)current >= $lt) continue;
            if($lte != null && (Double)current > $lte) continue;
            if($bt != null && $bt.size() >= 2 && ((Double)current < $bt.get(0) || (Double)current > $bt.get(1))){ continue; }
            result.add(data);
        }
        if($or != null && $or.size() > 0){ for(DtoDataFilter or : $or){ result.addAll(or.Apply(result)); } }
        if($and != null && $and.size() > 0){ for(DtoDataFilter and : $and){ result = and.Apply(result); } }

        List<DtoDataSet> finalResult = new ArrayList<DtoDataSet>();
        for(DtoDataSet data : dataset){ if(!finalResult.contains(data)){ finalResult.add(data); } }
        return finalResult;
    }

}
