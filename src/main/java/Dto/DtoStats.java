package Dto;

import java.util.ArrayList;
import java.util.List;

public class DtoStats extends Dto {
    public String field = null;
    public String type = null;
    public Double average = null;
    public Double min = null;
    public Double max = null;
    public Double std = null;
    public Double sum = null;
    public Long count = null;
    public Long empty = null;

    public static DtoStats Calculate(String field, List<DtoData> dataset) throws IllegalAccessException{
        DtoStats result = new DtoStats();
        result.field = field;
        List<Double> values = new ArrayList<Double>();

        for(DtoData data : dataset){
            Class type = data.GetType(result.field);
            Object _value = data.Get(result.field);
            if(result.type == null){ result.type = type.getName().toLowerCase().replace("java.lang.", ""); }
            if(_value != null && (type == Long.class || type == Integer.class || type == Double.class)){
                Double value = null;
                if(type == Long.class){ value = ((Long)_value).doubleValue(); }
                else if(type == Integer.class){ value = ((Integer)_value).doubleValue(); }
                else { value = (Double)_value; }

                if(result.sum == null){ result.sum = 0D; }
                if(result.count == null){ result.count = 0L; }
                if(result.empty == null){ result.empty = 0L; }
                if(result.min == null){ result.min = value; }
                if(result.max == null){ result.max = value; }

                result.sum += value;
                result.min = Math.min(result.min, value);
                result.max = Math.max(result.max, value);
                result.count++;
                values.add(value);
            }
            else if(_value == null) {
                if(result.empty == null){ result.empty = 0L; }
                result.empty++;
            }
        }

        if(result.sum != null && result.count != null){ result.average = result.sum / result.count; }

        if(result.average != null && result.count != null){
            Double sum = 0D;
            for(Double value : values){ sum += Math.pow(value - result.average, 2); }
            result.std = Math.sqrt(sum / result.count);
        }

        return result;
    }
}
