package Dto;

import java.util.List;

/**
 * Interfaccia per implementazione di un filtro. Generalizza la possibilità di applicazione di un filtro su un dataset
 * @param <DataType> Tipologia di dato da filtrare
 */
public interface IFilter<DataType> {
    /**
     * Applica il filtro ad un detrminato dataset
     * @param dataset dataset da filtrare
     * @return dataset filtrato
     * @throws IllegalAccessException
     */
    List<DataType> Apply(List<DataType> dataset)  throws IllegalAccessException;
}
