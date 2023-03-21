package fr.raconteur32.modpackconfigupdater.values;

import fr.raconteur32.modpackconfigupdater.utils.GsonTools;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface IMergeable<T> {
    public IMergeable<T> merge(IMergeable<T> other);

    public static <T> Type getMergeableType(IMergeable<T> clazz) {
        for (Type genericInterface : clazz.getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                if (IMergeable.class.equals(parameterizedType.getRawType())) {
                    return parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        return null;
    }
}
