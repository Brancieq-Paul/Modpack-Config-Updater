package fr.raconteur32.modpackconfigupdater.files.filemergesystem;

import fr.raconteur32.modpackconfigupdater.exceptions.FileCreationException;
import fr.raconteur32.modpackconfigupdater.files.*;
import fr.raconteur32.modpackconfigupdater.logs.Logs;
import fr.raconteur32.modpackconfigupdater.values.IMergeable;
import org.apache.commons.compress.utils.FileNameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class MergeableFileSystem implements IMergeable<MergeableFileSystem> {
    private Path fileSystemBase = null;
    public static final List<String> ANALYZED_FILE_EXTENSIONS = List.of("json", "properties", "yml", "yaml", "txt", "conf", "cfg", "ini", "xml", "properties", "ini", "rc", "config", "settings", "pref", "env", "toml");
    protected Map<String, IMergeableFile<IMergeableFile<?>>> mergeable_files_list = new HashMap<>();

    public MergeableFileSystem(@NotNull Path file_system_base_path) {
        Logs.LOGGER.debug("Initializing MergeableFileSystem from " + file_system_base_path);
        setFileSystemBase(file_system_base_path);
        regenerateFileList();
    }

    public void regenerateFileList() {
        try (Stream<Path> paths = Files.walk(getFileSystemBase(), FileVisitOption.FOLLOW_LINKS)) {
            paths.forEach(this::addIfMergeable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addIfMergeable(@NotNull Path path) {
        try {
            if (!ANALYZED_FILE_EXTENSIONS.contains(FileNameUtils.getExtension(path.getFileName().toString()))) {
                return;
            }
            Logs.LOGGER.debug(path + " extension detected as mergeable.");
            addMergeable(path);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 FileCreationException e) {
            Logs.LOGGER.error("Error while building the MergeableFile", e);
        }
    }

    private void addMergeable(Path path) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileCreationException {
        IMergeableFile file = (IMergeableFile)IFile.create(path);
        mergeable_files_list.put(path.toAbsolutePath().toString().replace(fileSystemBase.toAbsolutePath() + File.separator, ""), file);
    }

    public Path getFileSystemBase() {
        return fileSystemBase;
    }

    private void setFileSystemBase(Path fileSystemBase) {
        this.fileSystemBase = fileSystemBase;
    }

    @Override
    public IMergeable<MergeableFileSystem> merge(IMergeable<MergeableFileSystem> other) {
        if (!(other instanceof MergeableFileSystem otherFileSystem)) {
            throw new IllegalArgumentException("Cannot merge with a non-MergeableFileSystem object");
        }

        try {
            MergeableFileSystem base_merge = new MergeableFileSystem(this.getFileSystemBase());

            for (Map.Entry<String, IMergeableFile<IMergeableFile<?>>> entry : otherFileSystem.mergeable_files_list.entrySet()) {
                if (!base_merge.mergeable_files_list.containsKey(entry.getKey())) {
                    // Read other file
                    File ref = entry.getValue().getFilePath().toFile();
                    FileReader fr = new FileReader(ref);
                    BufferedReader br = new BufferedReader(fr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String fileContents = sb.toString();
                    // Write new file
                    File new_file = Paths.get(base_merge.fileSystemBase.toString(), entry.getKey()).toFile();
                    FileWriter fw = new FileWriter(new_file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(fileContents);
                    bw.close();
                } else {
                    IMergeableFile new_file = (IMergeableFile) base_merge.mergeable_files_list.get(entry.getKey()).merge(entry.getValue());
                    new_file.write();
                    base_merge.mergeable_files_list.put(entry.getKey(), new_file);
                }
            }
            return base_merge;
        } catch (IOException e) {
            Logs.LOGGER.error("Could not merge MergeableFileSystem properly", e);
            return null;
        }
    }
}
