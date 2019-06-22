package Dto;

import Utils.UObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta per la modellazione degli operatori del filtro
 * @param <DataType> Classe del dato per cui il filtro è applicabile
 */
public abstract class DtoFilterOperator<DataType> extends Dto {
    /**
     * dato diverso da $not
     */
    public DataType $not;
    /**
     * dato uguale a $eq
     */
    public DataType $eq;
    /**
     * dato contenuto in $in
     */
    public List<DataType> $in;
    /**
     * dato non contenuto in $nin
     */
    public List<DataType> $nin;

    /**
     * dato maggiore di $gt
     */
    public DataType $gt;
    /**
     * dato maggiore o uguale di $gte
     */
    public DataType $gte;
    /**
     * dato minore di $lt
     */
    public DataType $lt;
    /**
     * dato minore o uguale di $lte
     */
    public DataType $lte;
    /**
     * dato compreso fra $bt[0] e $bt[1] ($bt[0] <= dato <= $bt[1])
     */
    public List<DataType> $bt;


    /**
     * Applica l'operatore ad una proprietà dei dati del dataset, filtrando gli elementi che non corrispondono al filtro
     * @param dataset dataset da filtrare
     * @param property proprietà del dato da filtrare
     * @return dataset filtrato
     * @throws IllegalAccessException
     */
    public List<DtoData> Apply(List<DtoData> dataset, java.lang.String property) throws IllegalAccessException{
        List<DtoData> result = new ArrayList<DtoData>();
        for(DtoData data : dataset){
            //Recupero il valore del campo dell'oggetto
            DataType current = UObject.Get(data, property.toUpperCase());
            //valore uguale $eq (se esiste $eq)?
            if($eq != null && this.NotEqual(current, $eq)) continue;
            //valore diverso $not (se esiste $not)?
            if($not != null && this.Equal(current, $not)) continue;
            //valore contenuto in $in (se esiste $in)?
            if($in != null && this.NotContained(current, $in)) continue;
            //valore non contenuto in $nin (se esiste $nin)?
            if($nin != null && this.Contained(current, $nin)) continue;
            //valore maggiore $gt (se esiste $gt)?
            if($gt != null && this.LesserEqual(current, $gt)) continue;
            //valore maggiore uguale $gte (se esiste $gte)?
            if($gte != null && this.Lesser(current, $gte)) continue;
            //valore minore $lt (se esiste $lt)?
            if($lt != null && this.GreaterEqual(current, $lt)) continue;
            //valore minore uguale $lte (se esiste $lte)?
            if($lte != null && this.Greater(current, $lte)) continue;
            //valore compreso fra $bt[0] e $bt[1] (se esiste $bt e $bt ha almeno due elementi)?
            if($bt != null && $bt.size() >= 2 && !this.Between(current, $bt.get(0), $bt.get(1))) continue;
            result.add(data);
        }
        return result;
    }

    /**
     * Definizione astratta di ugualianza per l'operatore
     * @param left parte sinistra dell'ugualianza
     * @param right parte destra dell'ugualianza
     * @return true se uguali
     */
    protected abstract boolean Equal(DataType left, DataType right);
    /**
     * Definizione astratta di ordine per l'operatore
     * @param left parte sinistra dell'ordine
     * @param right parte destra dell'ordine
     * @return true se la parte sinistra è maggiore della destra
     */
    protected abstract boolean Greater(DataType left, DataType right);

    /**
     * Funzione opposta all'ugualianza per l'operatore
     * @param left parte sinistra dell'ordine
     * @param right parte destra dell'ordine
     * @return true se non uguali
     */
    protected boolean NotEqual(DataType left, DataType right){ return !this.Equal(left, right); }
    /**
     * Funzione di ordine e ugualianza per l'operatore
     * @param left parte sinistra dell'ordine
     * @param right parte destra dell'ordine
     * @return true se la parte sinistra è maggiore della destra o se le parti sono uguali
     */
    protected boolean GreaterEqual(DataType left, DataType right){ return this.Greater(left, right) || this.Equal(left, right); }
    /**
     * Funzione di ordine inverso ed ugualianza per l'operatore
     * @param left parte sinistra dell'ordine
     * @param right parte destra dell'ordine
     * @return true se la parte sinistra non è maggiore della destra
     */
    protected boolean LesserEqual(DataType left, DataType right){ return !this.Greater(left, right); }
    /**
     * Funzione di ordine inverso per l'operatore
     * @param left parte sinistra dell'ordine
     * @param right parte destra dell'ordine
     * @return true se la parte sinistra non è maggiore o uguale della destra
     */
    protected boolean Lesser(DataType left, DataType right){ return !this.GreaterEqual(left, right); }
    /**
     * Funzione per valutare se una parte è compresa fra altre due per l'operatore
     * @param left parte sinistra
     * @param min parte minima (min <= left)
     * @param max parte massima (left <= max)
     * @return true se la parte sinistra è compresa fra le parti min e max (min <= left <= max)
     */
    protected boolean Between(DataType left, DataType min, DataType max){ return this.GreaterEqual(left, min) && this.LesserEqual(left, max); }

    /**
     * Funzione per valutare se una parte è membra di un inseme
     * @param member parte membra
     * @param array insieme di controllo
     * @return true se esiste un elemento dell'insieme di controllo è uguale al membro di ricerca
     */
    protected boolean Contained(DataType member, List<DataType> array){ for(DataType element : array){ if(this.Equal(element, member)){ return true; } } return false; }
    /**
     * Funzione per valutare se una parte non è membra di un inseme
     * @param member parte membra
     * @param array insieme di controllo
     * @return false se esiste un elemento dell'insieme di controllo è uguale al membro di ricerca
     */
    protected boolean NotContained(DataType member, List<DataType> array){ return !this.Contained(member, array); }

    /**
     * Preset per operatori di filtro su dati di tipo Long
     */
    public static class Long extends DtoFilterOperator<java.lang.Long> {
        /**
         * Creazione semplice dell'operatore
         */
        public Long(){}

        /**
         * Creazione dell'operatore con relazione di equivalenza
         * @param $eq valore di equivalenza (Long)
         */
        public Long(java.lang.Long $eq){ this.$eq = $eq; }

        /**
         * Definizione di ugualianza per tipo Long
         * @param left  parte sinistra dell'ugualianza
         * @param right parte destra dell'ugualianza
         * @return true se la parte sinistra è uguale alla parte destra
         */
        protected boolean Equal(java.lang.Long left, java.lang.Long right) { return left.equals(right); }

        /**
         * Definizione di ordine per tipo Long
         * @param left  parte sinistra dell'ordine
         * @param right parte destra dell'ordine
         * @return true se la parte sinistra è maggiore della destra
         */
        protected boolean Greater(java.lang.Long left, java.lang.Long right) { return left > right; }
    }

    /**
     * Preset per operatori di filtro su dati di tipo Integer
     */
    public static class Integer extends DtoFilterOperator<java.lang.Integer> {
        /**
         * Creazione semplice dell'operatore
         */
        public Integer(){}
        /**
         * Creazione dell'operatore con relazione di equivalenza
         * @param $eq valore di equivalenza (Integer)
         */
        public Integer(java.lang.Integer $eq){ this.$eq = $eq; }
        /**
         * Definizione di ugualianza per tipo Integer
         * @param left  parte sinistra dell'ugualianza
         * @param right parte destra dell'ugualianza
         * @return true se la parte sinistra è uguale alla parte destra
         */
        protected boolean Equal(java.lang.Integer left, java.lang.Integer right) { return left.equals(right); }
        /**
         * Definizione di ordine per tipo Integer
         * @param left  parte sinistra dell'ordine
         * @param right parte destra dell'ordine
         * @return true se la parte sinistra è maggiore della destra
         */
        protected boolean Greater(java.lang.Integer left, java.lang.Integer right) { return left > right; }
    }

    /**
     * Preset per operatori di filtro su dati di tipo Double
     */
    public static class Double extends  DtoFilterOperator<java.lang.Double> {
        /**
         * Creazione semplice dell'operatore
         */
        public Double(){}
        /**
         * Creazione dell'operatore con relazione di equivalenza
         * @param $eq valore di equivalenza (Double)
         */
        public Double(java.lang.Double $eq){ this.$eq = $eq; }
        /**
         * Definizione di ugualianza per tipo Double
         * @param left  parte sinistra dell'ugualianza
         * @param right parte destra dell'ugualianza
         * @return true se la parte sinistra è uguale alla parte destra
         */
        protected boolean Equal(java.lang.Double left, java.lang.Double right) { return left.equals(right); }
        /**
         * Definizione di ordine per tipo Double
         * @param left  parte sinistra dell'ordine
         * @param right parte destra dell'ordine
         * @return true se la parte sinistra è maggiore della destra
         */
        protected boolean Greater(java.lang.Double left, java.lang.Double right) { return left > right; }
    }

    /**
     * Preset per operatori di filtro su dati di tipo String
     */
    public static class String extends DtoFilterOperator<java.lang.String> {
        /**
         * Creazione semplice dell'operatore
         */
        public String(){}
        /**
         * Creazione dell'operatore con relazione di equivalenza
         * @param $eq valore di equivalenza (String)
         */
        public String(java.lang.String $eq){ this.$eq = $eq; }
        /**
         * Definizione di ugualianza per tipo String
         * @param left  parte sinistra dell'ugualianza
         * @param right parte destra dell'ugualianza
         * @return true se la parte sinistra è uguale alla parte destra
         */
        protected boolean Equal(java.lang.String left, java.lang.String right) { return left.equalsIgnoreCase(right); }
        /**
         * Definizione di ordine per tipo String
         * @param left  parte sinistra dell'ordine
         * @param right parte destra dell'ordine
         * @return true se la parte sinistra è maggiore della destra
         */
        protected boolean Greater(java.lang.String left, java.lang.String right) { return left.compareToIgnoreCase(right) > 0; }
    }

}
