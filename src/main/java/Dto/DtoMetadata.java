package Dto;

/**
 * Dto per la mappatura dei metadati del dataset
 */
public class DtoMetadata extends Dto {
    /**
     * Nome del campo del dataset (formattato)
     */
    public String alias;
    /**
     * Nome del campo del dataset
     */
    public String sourceField;
    /**
     * Classe del campo del dataset
     */
    public String type;
}
