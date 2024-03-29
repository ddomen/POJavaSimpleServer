package Utils;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe di utilità nella gestione dei csv
 */
public final class UCsv {

    /**
     * Converte un csv in una lista di oggetti
     * @param csv stringa contenente le righe csv
     * @param _class classe con cui parsare le righe del csv
     * @param <ReturnType> classe per parsare
     * @return lista di oggetti con classe richiesta
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <ReturnType> List<ReturnType> Parse(String csv, Class<ReturnType> _class) throws InstantiationException, IllegalAccessException { return Parse(csv, _class,";"); }

    /**
     * @param csv stringa contenente le righe csv
     * @param _class classe con cui parsare le righe del csv
     * @param separator stringa di separazione fra le colonne del csv
     * @param <ReturnType> classe per parsare
     * @return lista di oggetti con classe richiesta
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <ReturnType> List<ReturnType> Parse(String csv, Class<ReturnType> _class, String separator) throws InstantiationException, IllegalAccessException{
        List<ReturnType> result = new ArrayList<ReturnType>();

        //Splitto per riga
        List<String> rows = new ArrayList<String>(Arrays.asList(csv.split("[\n\r]")));
        //La prima riga la uso come intestazione per definire i campi e poi la rimuovo
        List<String> headers = new ArrayList<String>(Arrays.asList(rows.get(0).split(separator)));
        rows.remove(0);

        for(String row : rows){
            //Per ogni riga creo un'instanza della classe e ne setto i campi
            ReturnType current = _class.newInstance();
            List<String> cells = Arrays.asList(row.split(separator));
            int cellSize = cells.size();
            for(int i = 0; i < headers.size(); i++){
                UObject.Set(current, headers.get(i), i < cellSize ? cells.get(i) : null);
            }
            result.add(current);
        }

        return result;
    }
}
