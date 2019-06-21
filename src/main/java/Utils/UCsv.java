package Utils;

import java.util.*;

public final class UCsv {

    public static <ReturnType> List<ReturnType> Parse(String csv, Class<ReturnType> _class) throws InstantiationException, IllegalAccessException { return Parse(csv, _class,";"); }

    public static <ReturnType> List<ReturnType> Parse(String csv, Class<ReturnType> _class, String separator) throws InstantiationException, IllegalAccessException{
        List<ReturnType> result = new ArrayList<ReturnType>();

        List<String> rows = new ArrayList<String>(Arrays.asList(csv.split("[\n\r]")));
        List<String> headers = new ArrayList<String>(Arrays.asList(rows.get(0).split(separator)));
        rows.remove(0);

        for(String row : rows){
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
