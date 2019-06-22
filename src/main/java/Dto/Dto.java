package Dto;
import Utils.UObject;

/**
 * Data Transfert Object - Modello astratto di trasferimento dati all'interno dell'applicazione
 */
public abstract class Dto {

    /**
     * Recupera la classe di una proprietà dell'oggetto
     * @param property nome della proprietà
     * @return classe della proprietà
     */
    public Class GetType(String property){ return UObject.GetType(this, property); }

    /**
     * Recupera il valore di una proprietà dell'oggetto
     * @param property nome della proprietà
     * @param <ReturnType> classe di casting per il valore
     * @return valore della proprietà castato
     * @throws IllegalAccessException
     */
    public <ReturnType> ReturnType Get(String property) throws IllegalAccessException { return UObject.Get(this, property); }

    /**
     * Imposta il valore di una proprietà dell'oggetto
     * @param property nome della proprietà
     * @param value valore da impostare
     * @return oggetto this per la concatenzaione (method chaining)
     * @throws IllegalAccessException
     */
    public Dto Set(String property, Object value) throws IllegalAccessException{ return (Dto)UObject.Set(this, property, value); }
}
