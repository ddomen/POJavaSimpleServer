package Dto;

import Utils.UObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class DtoFilter<Type extends DtoFilter, Interface> extends Dto{

    public List<Type> $or;
    public List<Type> $and;

    public List<Interface> Apply(List<Interface> dataset){
        List<Interface> result = new ArrayList<Interface>();
        result.addAll(dataset);
        Field[] fields = this.getClass().getFields();
        for(Field field : fields){
            String property = field.getName();
            DtoFilterOperator operator = (DtoFilterOperator)UObject.Get(this, property);
            if(operator != null){ result =  operator.Apply(result, property); }
        }

        final List<Interface> orResult = new ArrayList<Interface>();
        for(Type or : $or){ orResult.addAll(or.Apply(result)); }
        for(Interface r : result){ if(!orResult.contains(r)){ result.remove(r); } }
        for(Type and : $and){ result = and.Apply(result); }

        return result;
    }

    public static class Data extends DtoFilter<Data, DtoData>{
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
    }

    public static class Stats extends DtoFilter<Stats, DtoStats> {
        public DtoFilterOperator.Long count;

        public DtoFilterOperator.Double average;
        public DtoFilterOperator.Double min;
        public DtoFilterOperator.Double max;
        public DtoFilterOperator.Double std;
        public DtoFilterOperator.Double sum;

        public DtoFilterOperator.String field;
    }

}
