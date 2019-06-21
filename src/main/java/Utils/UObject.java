package Utils;

import com.google.gson.Gson;

import java.lang.reflect.Field;

public final class UObject {

    public static Field GetField(Object object, String property){
        Class<?> _class = object.getClass();
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

    public static Class GetType(Object object, String property){
        Field field = UObject.GetField(object, property);
        if(field != null){ return field.getType(); }
        return null;
    }

    public static <ReturnType> ReturnType Get(Object object, String property) throws IllegalAccessException{
        Field field = UObject.GetField(object, property);
        if(field != null){ return (ReturnType)field.get(object); }
        return null;
    }

    public static Object Set(Object object, String property, Object value) throws IllegalAccessException{
        Field field = UObject.GetField(object, property);
        if(field != null){
            Class<?> type = field.getType();
            if(value != null && value.getClass() == String.class) { field.set(object, Parse((String)value, type)); }
            else{ field.set(object, value); }
        }
        return object;
    }

    public static Object Parse(String value, Class<?> _class){
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

    public static String toJSON(Object object){ return new Gson().toJson(object); }

    public static <Result> Result fromJSON(String json, Class<Result> _class) {
        try{ return (Result)new Gson().fromJson(json, _class); }
        catch (Exception ex){
            return null;
        }
    }

    public static <Result> Result fromJSON(String json, Class<Result> _class, boolean throwException) throws Exception{
        try{ return (Result)new Gson().fromJson(json, _class); }
        catch (Exception ex){
            if(throwException){ throw ex; }
            else{ return null; }
        }
    }
}
