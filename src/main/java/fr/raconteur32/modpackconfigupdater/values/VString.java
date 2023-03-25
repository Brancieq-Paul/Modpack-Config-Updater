package fr.raconteur32.modpackconfigupdater.values;

public class VString extends AValue<String> {

    public VString(String _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        return get_raw_value();
    }
}
