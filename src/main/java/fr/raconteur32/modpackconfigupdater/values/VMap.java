package fr.raconteur32.modpackconfigupdater.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VMap extends AValue<Map<String, AValue<?>>> implements IMergeable<VMap> {

    public VMap(Map<String, AValue<?>> _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(get_raw_value());
    }

    @SuppressWarnings("unchecked")
    private <T> IMergeable<T> safeMerge(IMergeable<T> a, IMergeable<?> b) {
        return a.merge((IMergeable<T>) b);
    }

    @Override
    public VMap merge(IMergeable<VMap> other) {
        if (!(other instanceof VMap otherVMap)) {
            throw new IllegalArgumentException("Cannot merge with a non-VMap object");
        }

        Map<String, AValue<?>> mergedMap = new HashMap<>(this.get_raw_value());

        for (Map.Entry<String, AValue<?>> entry : otherVMap.get_raw_value().entrySet()) {
            String key = entry.getKey();
            AValue<?> value = entry.getValue();

            if (!mergedMap.containsKey(key)) {
                mergedMap.put(key, value);
            } else {
                Object currentValue = mergedMap.get(key);

                if (currentValue instanceof IMergeable && value instanceof IMergeable) {
                    Type currentValueMergeableType = IMergeable.getMergeableType((IMergeable<?>) currentValue);
                    Type valueMergeableType = IMergeable.getMergeableType((IMergeable<?>) value);

                    if (Objects.equals(currentValueMergeableType, valueMergeableType)) {
                        IMergeable<?> currentMergeable = (IMergeable<?>) currentValue;
                        IMergeable<?> valueMergeable = (IMergeable<?>) value;

                        AValue<?> mergedValue = (AValue<?>)safeMerge(currentMergeable, valueMergeable);
                        mergedMap.put(key, mergedValue);
                    } else {
                        mergedMap.put(key, value);
                    }
                } else {
                    mergedMap.put(key, value);
                }
            }
        }

        return new VMap(mergedMap);
    }
}
