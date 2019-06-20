package Dto;

import Utils.UObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DtoDataFilter extends Dto{
    public DtoDataFilterOperator id;
    public DtoDataFilterOperator codice_asl;
    public DtoDataFilterOperator asl;
    public DtoDataFilterOperator distretto;
    public DtoDataFilterOperator rapporto_ssr;
    public DtoDataFilterOperator denominazione_gestore;
    public DtoDataFilterOperator denominazione_struttura_operativa;
    public DtoDataFilterOperator p_iva;
    public DtoDataFilterOperator indirizzo_sede_operativa;
    public DtoDataFilterOperator codice_istat_comune;
    public DtoDataFilterOperator comune_sede_operativa;
    public DtoDataFilterOperator provincia;
    public DtoDataFilterOperator attivita;
    public DtoDataFilterOperator pl_res;
    public DtoDataFilterOperator pl_semires;
    public DtoDataFilterOperator pl_res_eccesso_da_art_26;
    public DtoDataFilterOperator pl_semires_eccesso_da_art_26;
    public DtoDataFilterOperator res_art_26;
    public DtoDataFilterOperator p_l_res_eccesso;
    public DtoDataFilterOperator semires_art_26;
    public DtoDataFilterOperator p_l_semires_eccesso;
    public DtoDataFilterOperator rsa_disabili;
    public DtoDataFilterOperator cd_disabili;
    public DtoDataFilterOperator rsa_anziani;
    public DtoDataFilterOperator rsa_demenze;
    public DtoDataFilterOperator cd_anziani;
    public DtoDataFilterOperator cd_demenze;

    public List<DtoDataSet> Apply(List<DtoDataSet> dataset){
        List<DtoDataSet> result = new ArrayList<DtoDataSet>();
        result.addAll(dataset);
        Field[] fields = this.getClass().getFields();
        for(Field field : fields){
            String property = field.getName();
            DtoDataFilterOperator operator = (DtoDataFilterOperator)UObject.Get(this, property);
            if(operator != null){ result =  operator.Apply(result, property); }
        }
        return result;
    }

}
