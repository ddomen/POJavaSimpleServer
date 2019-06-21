package Dto;
import Utils.UObject;

public class Dto {

    public Class GetType(String property){ return UObject.GetType(this, property); }
    public <ReturnType> ReturnType Get(String property) throws IllegalAccessException { return UObject.Get(this, property); }
    public Dto Set(String property, Object value) throws IllegalAccessException{ return (Dto)UObject.Set(this, property, value); }
}
