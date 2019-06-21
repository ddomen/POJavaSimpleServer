package Dto;

import com.google.gson.*;
import java.util.*;

import Utils.UObject;

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
        return result;
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

    public static class Long extends DtoFilterOperator<java.lang.Long> {
        public Long(){}
        public Long(java.lang.Long $eq){ this.$eq = $eq; }
        protected boolean Equal(java.lang.Long left, java.lang.Long right) { return left.equals(right); }
        protected boolean Greater(java.lang.Long left, java.lang.Long right) { return left > right; }
    }

    public static class Integer extends DtoFilterOperator<java.lang.Integer> {
        public Integer(){}
        public Integer(java.lang.Integer $eq){ this.$eq = $eq; }
        protected boolean Equal(java.lang.Integer left, java.lang.Integer right) { return left.equals(right); }
        protected boolean Greater(java.lang.Integer left, java.lang.Integer right) { return left > right; }
    }

    public static class Double extends  DtoFilterOperator<java.lang.Double> {
        public Double(){}
        public Double(java.lang.Double $eq){ this.$eq = $eq; }
        protected boolean Equal(java.lang.Double left, java.lang.Double right) { return left.equals(right); }
        protected boolean Greater(java.lang.Double left, java.lang.Double right) { return left > right; }
    }

    public static class String extends DtoFilterOperator<java.lang.String> {
        public String(){}
        public String(java.lang.String $eq){ this.$eq = $eq; }
        protected boolean Equal(java.lang.String left, java.lang.String right) { return left.equalsIgnoreCase(right); }
        protected boolean Greater(java.lang.String left, java.lang.String right) { return left.compareToIgnoreCase(right) > 0; }
    }

}
