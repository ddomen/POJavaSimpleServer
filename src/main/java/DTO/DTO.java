package DTO;
import Utils.UObject;

public class DTO {

    public <ReturnType> ReturnType Get(String property){ return UObject.Get(this, property); }

    public DTO Set(String property, Object value) throws IllegalStateException{ return (DTO)UObject.Set(this, property, value); }
}
