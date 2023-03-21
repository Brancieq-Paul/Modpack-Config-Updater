package fr.raconteur32.modpackconfigupdater.values;

public class VBoolean extends AValue<Boolean> {

    public VBoolean(boolean _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        return get_raw_value().toString();
    }
}
