package Utils;

import com.google.gson.*;
import java.util.*;
import java.lang.reflect.Field;

/**
 * Classe di utilità nella gestione degli oggetti
 */
public final class UObject {

    /**
     * Recupera un campo di un oggetto a partire dal suo nome
     * @param object oggetto destinatario
     * @param property nome del campo
     * @return campo
     */
    public static Field GetField(Object object, String property){
        Class<?> _class = object.getClass();
        //Provo a recuperare il campo per la classe corrente
        //se non è stato trovato passo a controllare nella classe genitore (parent)
        //e ripeto il ciclo finché esiste una classe genitoriale.
        //Se ancora non è stata trovata torno null
        while (_class != null) {
            try {
                Field field = _class.getDeclaredField(property);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException ex) { _class = _class.getSuperclass(); }
            catch (Exception ex) { throw new IllegalStateException(ex); }
        }
        return null;
    }

    /**
     * Recupera la classe del campo di un oggetto
     * @param object oggetto destinatario
     * @param property nome del campo
     * @return classe del campo
     */
    public static Class GetType(Object object, String property){
        Field field = UObject.GetField(object, property);
        if(field != null){ return field.getType(); }
        return null;
    }


    /**
     * Recupera il valore di un campo di un oggetto
     * @param object oggetto destinatario
     * @param property nome del campo
     * @param <ReturnType> classe per castare
     * @return valore del campo (o null se inesistente)
     * @throws IllegalAccessException
     */
    public static <ReturnType> ReturnType Get(Object object, String property) throws IllegalAccessException{
        Field field = UObject.GetField(object, property);
        if(field != null){ return (ReturnType)field.get(object); }
        return null;
    }

    /**
     * Setta il valore di un campo di un oggetto
     * @param object oggetto destinatario
     * @param property nome del campo
     * @param value valore da settare
     * @return oggetto destinatario
     * @throws IllegalAccessException
     */
    public static Object Set(Object object, String property, Object value) throws IllegalAccessException{
        Field field = UObject.GetField(object, property);
        if(field != null){
            Class<?> type = field.getType();
            //Se il valore è una stringa provo a parsarla secondo la classe del campo descritta dall'oggetto
            if(value != null && value.getClass() == String.class) { field.set(object, Parse((String)value, type)); }
            else{ field.set(object, value); }
        }
        return object;
    }

    /**
     * Converte un valore da stringa alla classe richiesta
     * @param value valore da castare
     * @param _class classe per castare
     * @return valore castato (o null se non è possibile castare)
     */
    public static Object Parse(String value, Class<?> _class){
        //Controllo se è possibile parsare la stringa secondo la classe data
        if(String.class == _class) return value;
        else if(value.length() == 0) return null;
        else if(Boolean.class == _class) return Boolean.parseBoolean(value);
        else if(Byte.class == _class) return Byte.parseByte(value);
        else if(Short.class == _class) return Short.parseShort(value);
        else if(Integer.class == _class) return Integer.parseInt(value);
        else if(Long.class == _class) return Long.parseLong(value);
        else if(Float.class == _class) return Float.parseFloat(value);
        else if(Double.class == _class) return Double.parseDouble(value);
        return null;
    }

    /**
     * Serializza un oggetto in formato json
     * @param object oggetto destinatario
     * @return stringa json
     */
    public static String toJSON(Object object){ return CustomGsonBuilder.create().toJson(object); }

    /**
     * Deserializza un oggetto dal formato json
     * @param json stringa json
     * @param _class classe per deserializzare
     * @param <Result> classe per deserializzare
     * @return oggetto deserializzato
     */
    public static <Result> Result fromJSON(String json, Class<Result> _class){return fromJSON(json, _class, false); }

    /**
     * Deserializza un oggetto dal formato json
     * @param json stringa json
     * @param _class classe per deserializzare
     * @param throwException on/off generazione di eccezione nel caso di fallimento nella deserializzazione
     * @param <Result> classe per deserializzare
     * @return oggetto deserializzato
     */
    public static <Result> Result fromJSON(String json, Class<Result> _class, boolean throwException) {
        try{ return (Result)CustomGsonBuilder.create().fromJson(json, _class); }
        catch (Exception ex){ if(throwException){ throw ex; } }
        return null;
    }


    /**
     * Registra un deserializzatore customizzato per una classe specifica, se tale classe non è già stata associata ad un altro deserializzatore
     * @param _class classe destinataria
     * @param deserializer deserializzatore
     */
    public static void RegisterJsonDeserializer(Class _class, JsonDeserializer deserializer){
        if(!Deserializers.containsKey(_class)){
            CustomGsonBuilder.registerTypeAdapter(_class, deserializer);
            Deserializers.put(_class, deserializer);
        }
    }

    /**
     * GsonBuilder per aggiungere la possibilità di ussare deserializzatori customizzati
     */
    private static GsonBuilder CustomGsonBuilder = new GsonBuilder();
    /**
     * Mappa dei deserializzatori abbinati alle classi
     */
    private static Map<Class, JsonDeserializer> Deserializers = new HashMap<Class, JsonDeserializer>();
}
