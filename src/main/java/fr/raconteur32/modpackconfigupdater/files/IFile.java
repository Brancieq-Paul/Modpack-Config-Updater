package fr.raconteur32.modpackconfigupdater.files;

import java.nio.file.Path;

public interface IFile {
    public void init(Path FilePath);
    public void write();
    public Path getFilePath();
}
