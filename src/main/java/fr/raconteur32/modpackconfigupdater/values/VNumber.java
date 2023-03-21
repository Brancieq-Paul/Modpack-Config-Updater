package fr.raconteur32.modpackconfigupdater.values;

public class VNumber extends AValue<Number> {

    public VNumber(Number _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        return get_raw_value().toString();
    }
}
