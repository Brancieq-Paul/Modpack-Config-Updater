package fr.raconteur32.modpackconfigupdater.files;

import fr.raconteur32.modpackconfigupdater.ModpackConfigUpdater;
import fr.raconteur32.modpackconfigupdater.exceptions.FileCreationException;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public interface IFile {
    Map<Class<? extends IFile>, Integer> fileTypes = new HashMap<>();

    static void register(int priority, Class<? extends IFile> fileType) {
        fileTypes.put(fileType, priority);
    }

    public static IFile create(Path filePath) throws FileCreationException {
        IFile result = null;
        int maxPriority = -1;
        for (Map.Entry<Class<? extends IFile>, Integer> entry : fileTypes.entrySet()) {
            int priority = entry.getValue();
            Class<? extends IFile> fileType = entry.getKey();
            try {
                Constructor<? extends IFile> constructor = fileType.getConstructor(Path.class);
                IFile file = constructor.newInstance(filePath);
                if (priority > maxPriority) {
                    result = file;
                    maxPriority = priority;
                }
            } catch (Exception e) {
                // Log error and try the next file type
                ModpackConfigUpdater.LOGGER.debug("Can't create file with " + fileType.getName(), e);
            }
        }
        if (result == null) {
            throw new FileCreationException("Unable to create file from path: " + filePath);
        }
        return result;
    }

    // Save the raw value in file
    public void write();

    // Get the file path member
    public Path getFilePath();
}
