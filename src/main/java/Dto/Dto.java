package Dto;
import Utils.UObject;

public class Dto {

    public <ReturnType> ReturnType Get(String property){ return UObject.Get(this, property); }

    public Dto Set(String property, Object value) throws IllegalStateException{ return (Dto)UObject.Set(this, property, value); }
}
