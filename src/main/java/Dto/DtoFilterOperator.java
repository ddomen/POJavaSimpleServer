package Dto;

import Utils.UObject;

import java.util.ArrayList;
import java.util.List;

public abstract class DtoFilterOperator<Type> extends Dto {
    public Type $not;
    public Type $eq;
    public List<Type> $in;
    public List<Type> $nin;

    public Type $gt;
    public Type $gte;
    public Type $lt;
    public Type $lte;
    public List<Type> $bt;


    public List<DtoData> Apply(List<DtoData> dataset, java.lang.String property) throws IllegalAccessException{
        List<DtoData> result = new ArrayList<DtoData>();
        for(DtoData data : dataset){
            Type current = UObject.Get(data, property.toUpperCase());
            if($eq != null && this.NotEqual(current, $eq)) continue;
            if($not != null && this.Equal(current, $not)) continue;
            if($in != null && this.NotContained(current, $in)) continue;
            if($nin != null && this.Contained(current, $nin)) continue;
            if($gt != null && this.LesserEqual(current, $gt)) continue;
            if($gte != null && this.Lesser(current, $gte)) continue;
            if($lt != null && this.GreaterEqual(current, $lt)) continue;
            if($lte != null && this.Greater(current, $lte)) continue;
            if($bt != null && $bt.size() >= 2 && !this.Between(current, $bt.get(0), $bt.get(1))) continue;
            result.add(data);
        }

        List<DtoData> finalResult = new ArrayList<DtoData>();
        for(DtoData data : dataset){ if(!finalResult.contains(data)){ finalResult.add(data); } }
        return finalResult;
    }

    protected abstract boolean Equal(Type left, Type right);
    protected abstract boolean Greater(Type left, Type right);

    protected boolean NotEqual(Type left, Type right){ return !this.Equal(left, right); }
    protected boolean GreaterEqual(Type left, Type right){ return this.Greater(left, right) || this.Equal(left, right); }
    protected boolean LesserEqual(Type left, Type right){ return !this.Greater(left, right); }
    protected boolean Lesser(Type left, Type right){ return !this.GreaterEqual(left, right); }
    protected boolean Between(Type left, Type min, Type max){ return this.GreaterEqual(left, min) && this.LesserEqual(left, max); }

    protected boolean Contained(Type member, List<Type> array){ for(Type element : array){ if(this.Equal(element, member)){ return true; } } return false; }
    protected boolean NotContained(Type member, List<Type> array){ return !this.Contained(member, array); }

    public class Long extends DtoFilterOperator<java.lang.Long> {
        protected boolean Equal(java.lang.Long left, java.lang.Long right) { return left.equals(right); }
        protected boolean Greater(java.lang.Long left, java.lang.Long right) { return left > right; }
    }

    public class Integer extends DtoFilterOperator<java.lang.Integer> {
        protected boolean Equal(java.lang.Integer left, java.lang.Integer right) { return left.equals(right); }
        protected boolean Greater(java.lang.Integer left, java.lang.Integer right) { return left > right; }
    }

    public class Double extends  DtoFilterOperator<java.lang.Double> {
        protected boolean Equal(java.lang.Double left, java.lang.Double right) { return left.equals(right); }
        protected boolean Greater(java.lang.Double left, java.lang.Double right) { return left > right; }
    }

    public class String extends DtoFilterOperator<java.lang.String> {
        protected boolean Equal(java.lang.String left, java.lang.String right) { return left.equalsIgnoreCase(right); }
        protected boolean Greater(java.lang.String left, java.lang.String right) { return left.compareToIgnoreCase(right) > 0; }
    }

}
