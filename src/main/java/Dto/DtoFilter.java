package Dto;

import Utils.UObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DtoFilterData extends Dto{
    public DtoFilterOperator.Long id;
    public DtoFilterOperator.Long codice_asl;
    public DtoFilterOperator.Long codice_istat_comune;

    public DtoFilterOperator.String asl;
    public DtoFilterOperator.String distretto;
    public DtoFilterOperator.String rapporto_ssr;
    public DtoFilterOperator.String denominazione_gestore;
    public DtoFilterOperator.String denominazione_struttura_operativa;
    public DtoFilterOperator.String p_iva;
    public DtoFilterOperator.String indirizzo_sede_operativa;
    public DtoFilterOperator.String comune_sede_operativa;
    public DtoFilterOperator.String provincia;
    public DtoFilterOperator.String attivita;

    public DtoFilterOperator.Integer pl_res;
    public DtoFilterOperator.Integer pl_semires;
    public DtoFilterOperator.Integer pl_res_eccesso_da_art_26;
    public DtoFilterOperator.Integer pl_semires_eccesso_da_art_26;
    public DtoFilterOperator.Integer res_art_26;
    public DtoFilterOperator.Integer p_l_res_eccesso;
    public DtoFilterOperator.Integer semires_art_26;
    public DtoFilterOperator.Integer p_l_semires_eccesso;
    public DtoFilterOperator.Integer rsa_disabili;
    public DtoFilterOperator.Integer cd_disabili;
    public DtoFilterOperator.Integer rsa_anziani;
    public DtoFilterOperator.Integer rsa_demenze;
    public DtoFilterOperator.Integer cd_anziani;
    public DtoFilterOperator.Integer cd_demenze;

    public List<DtoFilterData> $or;
    public List<DtoFilterData> $and;

    public List<DtoDataSet> Apply(List<DtoDataSet> dataset){
        List<DtoDataSet> result = new ArrayList<DtoDataSet>();
        result.addAll(dataset);
        Field[] fields = this.getClass().getFields();
        for(Field field : fields){
            String property = field.getName();
            DtoFilterOperator operator = (DtoFilterOperator)UObject.Get(this, property);
            if(operator != null){ result =  operator.Apply(result, property); }
        }
        return result;
    }

}
