package fr.raconteur32.modpackconfigupdater.values;

import java.util.List;

public class VArray extends AValue<List<AValue<?>>>{

    public VArray(List<AValue<?>> _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        return get_raw_value().toString();
    }
}
