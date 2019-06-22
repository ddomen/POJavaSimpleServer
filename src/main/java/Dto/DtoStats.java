package Dto;

import java.util.*;

/**
 * Dto per la mappatura ed il calcolo delle statistiche sul dataset
 */
public class DtoStats extends Dto {
    /**
     * Campo analizzato nel dataset
     */
    public String field = null;
    /**
     * Classe del campo analizzato
     */
    public String type = null;
    /**
     * Media (presente solo in caso di campi numerici)
     */
    public Double average = null;
    /**
     * Minimo (presente solo in caso di campi numerici)
     */
    public Double min = null;
    /**
     * Massimo (presente solo in caso di campi numerici)
     */
    public Double max = null;
    /**
     * Deviazione Standard (presente solo in caso di campi numerici)
     */
    public Double std = null;
    /**
     * Somma (presente solo in caso di campi numerici)
     */
    public Double sum = null;
    /**
     * Numero di campioni
     */
    public Long count = null;
    /**
     * Numero di campioni nulli
     */
    public Long empty = null;

    /**
     * Calcola le statistiche su un campo del dataset
     * @param field campo da analizzare
     * @param dataset dataset da analizzare
     * @return oggetto di statistiche
     * @throws IllegalAccessException
     */
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
            else if(_value != null){
                if(result.count == null){ result.count = 0L; }
                result.count++;
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
