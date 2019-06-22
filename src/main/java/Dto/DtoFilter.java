package Dto;

import java.lang.reflect.*;
import java.util.*;
import com.google.gson.*;

import Utils.UObject;

/**
 * Classe astratta per la mappatura del filtro su dataset
 * @param <Type> Tipo di filtro concatenabile su operazioni or ed and
 * @param <Interface> Interfaccia dei dati filtrabili
 */
public abstract class DtoFilter<Type extends DtoFilter, Interface> extends Dto{

    /**
     * Lista per la concatenazione dei filtri in logica OR
     */
    public List<Type> $or;
    /**
     * Lista per la concatenazione dei filtri in logica AND
     */
    public List<Type> $and;

    /**
     * Applica il filtro ad un detrminato dataset
     * @param dataset dataset da filtrare
     * @return dataset filtrato
     * @throws IllegalAccessException
     */
    public List<Interface> Apply(List<Interface> dataset) throws IllegalAccessException{
        //Copio la lista per lasciare intatta quella di input
        List<Interface> result = new ArrayList<Interface>();
        result.addAll(dataset);
        //Recupero i campi del filtro
        Field[] fields = this.getClass().getFields();
        for(Field field : fields){
            String property = field.getName();
            //Se il campo del filtro non è applicabile continuo (ad esempio $or o i campi statici)
            if(!ApplicableField(field)){ continue; }
            //Recupero l'operatore e se è presente e valido lo applico
            DtoFilterOperator operator = (DtoFilterOperator)UObject.Get(this, property);
            if(operator != null){ result =  operator.Apply(result, property); }
        }

        if($or != null){
            //Se è presente il campo $or applico ogni suo filtro al dataset risultante
            List<Interface> orResult = new ArrayList<Interface>();
            for(Type or : $or){ orResult.addAll(or.Apply(result)); }

            //Ripulisco il dataset risultante e riaggiungo gli elementi unici recuperati dalla lista di filtri in or
            result.clear();
            for(Interface r : orResult){ if(!result.contains(r)){ result.add(r); } }
        }
        if($and != null){
            //Essendo la logica in AND applico a catena ogni filtro presente nel campo $and
            for(Type and : $and){ result = and.Apply(result); }
        }

        return result;
    }

    /**
     * Controlla se un campo del filtro corrente sia applicabile come filtro
     * Se il campo inizia con $, è statico o non pubblico allora non sarà applicabile
     * @param field campo del filtro
     * @return true se applicabile
     */
    protected boolean ApplicableField(Field field){
        String property = field.getName();
        int modifiers = field.getModifiers();
        return !(property.startsWith("$") || Modifier.isStatic(modifiers)) && Modifier.isPublic(modifiers);
    }

    /**
     * Mappatura del filtro sui dati del dataset
     */
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

    /**
     * Mappatura del filtro sulle statistiche del dataset (inusato)
     */
    public static class Stats extends DtoFilter<Stats, DtoStats> {
        public DtoFilterOperator.Long count;

        public DtoFilterOperator.Double average;
        public DtoFilterOperator.Double min;
        public DtoFilterOperator.Double max;
        public DtoFilterOperator.Double std;
        public DtoFilterOperator.Double sum;

        public DtoFilterOperator.String field;
    }

    /**
     * Deserializzatore customizzato per convertire un json in un DtoFilter
     */
    public static JsonDeserializer<Data> Deserializer = new JsonDeserializer<Data>() {
        @Override
        public Data deserialize(JsonElement jsonElement, java.lang.reflect.Type jType, JsonDeserializationContext context) throws JsonParseException {
            //Genero un nuovo filtro
            Data deserialized = new Data();
            //Recupero l'oggetto json e i campi del filtro
            JsonObject json = jsonElement.getAsJsonObject();
            Field[] fields = Data.class.getFields();

            for(Field field : fields){
                Class type = field.getType();
                String property = field.getName();
                //Per ogni campo controllo se è presente nell'oggetto json
                if(!json.has(property)){ continue; }
                try{
                    //Recupero il valore del campo nell'oggetto json
                    JsonElement value = json.get(property);
                    if(value.isJsonObject()){
                        //Se il valore è di tipo "oggetto" allora provo a convertirlo in un DtoFilterOperator
                        deserialized.Set(property, context.deserialize(value, type));
                    }
                    else if(value.isJsonPrimitive()){
                        //Altrimenti se il valore è di tipo "primitivo" provo a convertirlo in un DtoFilterOperator che
                        //che abbia solo la proprietà $eq=primitivo, a seconda del tipo di operatore che possiede
                        // la proprietà del filtro
                        if(type == DtoFilterOperator.String.class){ deserialized.Set(property, new DtoFilterOperator.String(json.get(property).getAsString())); }
                        else if(type == DtoFilterOperator.Integer.class){ deserialized.Set(property, new DtoFilterOperator.Integer(json.get(property).getAsInt())); }
                        else if(type == DtoFilterOperator.Long.class){ deserialized.Set(property, new DtoFilterOperator.Long(json.get(property).getAsLong())); }
                        else if(type == DtoFilterOperator.Double.class){ deserialized.Set(property, new DtoFilterOperator.Double(json.get(property).getAsDouble())); }
                    }
                }catch (IllegalAccessException ex){}
            }
            return deserialized;
        }
    };
}
