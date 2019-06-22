package Dto;

import java.util.List;

/**
 * Interfaccia per implementazione di un filtro
 * @param <Interface> Tipologia di dato da filtrare
 */
public interface IFilter<Interface> {
    /**
     * Applica il filtro ad un detrminato dataset
     * @param dataset dataset da filtrare
     * @return dataset filtrato
     * @throws IllegalAccessException
     */
    List<Interface> Apply(List<Interface> dataset)  throws IllegalAccessException;
}
