package DTO;
import java.lang.reflect.Field;

public class DTO {

    public <ReturnType> ReturnType Get(String property){
        Class<?> _class = this.getClass();
        while (_class != null) {
            try {
                Field field = _class.getDeclaredField(property);
                field.setAccessible(true);
                return (ReturnType)field.get(this);
            }
            catch (NoSuchFieldException ex) { _class = _class.getSuperclass(); }
            catch (Exception ex) { throw new IllegalStateException(ex); }
        }
        return null;
    }

    public DTO Set(String property, Object value) throws IllegalStateException{
        Class<?> _class = this.getClass();
        while (_class != null) {
            try {
                Field field = _class.getDeclaredField(property);
                field.setAccessible(true);
                field.set(this, value);
                return this;
            }
            catch (NoSuchFieldException ex) { _class = _class.getSuperclass(); }
            catch (Exception ex) { throw new IllegalStateException(ex); }
        }
        return this;
    }
}
