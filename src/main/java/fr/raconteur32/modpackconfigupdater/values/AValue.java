package fr.raconteur32.modpackconfigupdater.values;

public abstract class AValue<T> {
    // Typed raw value
    private T _raw_value;

    public AValue(T _raw_value) {
        this._raw_value = _raw_value;
    }

    public T get_raw_value() {
        return _raw_value;
    }

    public void set_raw_value(T _raw_value) {
        this._raw_value = _raw_value;
    }

    @Override
    public String toString() {
        return _raw_value.toString();
    }
}
