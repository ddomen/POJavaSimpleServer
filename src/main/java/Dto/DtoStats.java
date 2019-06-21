package Dto;

import java.util.List;

public class DtoStats extends Dto {
    public String field;
    public Double average;
    public Double min;
    public Double max;
    public Double std;
    public Double sum;
    public Long count;

    public static DtoStats Calculate(List<DtoData> dataset){
        DtoStats result = new DtoStats();
        for(DtoData data : dataset){

        }

        return result;
    }
}
