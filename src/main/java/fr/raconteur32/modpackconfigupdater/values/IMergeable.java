package fr.raconteur32.modpackconfigupdater.values;

import fr.raconteur32.modpackconfigupdater.utils.GsonTools;

public interface IMergeable<T> {
    public T merge(T other) throws GsonTools.JsonObjectExtensionConflictException;
}
