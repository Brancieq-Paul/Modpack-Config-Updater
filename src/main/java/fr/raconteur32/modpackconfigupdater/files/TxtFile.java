package fr.raconteur32.modpackconfigupdater.files;

import fr.raconteur32.modpackconfigupdater.logs.Logs;
import fr.raconteur32.modpackconfigupdater.values.IMergeable;

import java.io.*;
import java.nio.file.Path;

public class TxtFile implements IMergeableFile<TxtFile> {
    private Path FilePath;
    public TxtFile() {
        super();
    }

    public TxtFile(Path NewFilePath) {
        super();
        this.init(NewFilePath);
    }


    public void init(Path FilePath) {
        setFilePath(FilePath);
    }

    public void setFilePath(Path filePath) {
        FilePath = filePath;
    }

    @Override
    public Path getFilePath() {
        return this.FilePath;
    }

    @Override
    public void write() {
    }

    @Override
    public TxtFile merge(IMergeable<TxtFile> other) {
        if (!(other instanceof TxtFile otherTxtFile)) {
            throw new IllegalArgumentException("Cannot merge with a non-TxtFile object");
        }

        try {
            FileReader fr = new FileReader(otherTxtFile.getFilePath().toFile());
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            FileWriter fw = new FileWriter(this.getFilePath().toFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
            return this;
        } catch (IOException e) {
            Logs.LOGGER.error("Can't merge .txt", e);
        }
        return this;
    }
}
