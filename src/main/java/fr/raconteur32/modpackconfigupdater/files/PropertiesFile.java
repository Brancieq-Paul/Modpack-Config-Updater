package fr.raconteur32.modpackconfigupdater.files;


import fr.raconteur32.modpackconfigupdater.ModpackConfigUpdater;
import fr.raconteur32.modpackconfigupdater.values.VProperties;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesFile extends VProperties implements IMergeableFile<PropertiesFile> {

    private Path FilePath;

    public PropertiesFile() {
        super();
    }

    public PropertiesFile(Path NewFilePath) {
        super();
        this.init(NewFilePath);
    }


    @Override
    public void init(Path FilePath) {
        setFilePath(FilePath);
        this.set_raw_value(fileToProperties());
    }

    private Properties fileToProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(FilePath.toString()));
        } catch (IOException e) {
            ModpackConfigUpdater.LOGGER.error("Could not open properties file.");
        }
        return properties;
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
        String base_regex = "\\s*=\\s*)(.*)";
        Matcher matcher;
        Set<String> names = get_raw_value().stringPropertyNames();
        try {
            FileReader fileReader = new FileReader(FilePath.toFile());
            BufferedReader br = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String content = sb.toString();

            for (String name : names) {
                String value = get_raw_value().getProperty(name);
                String adapted_regex = "(" + name + base_regex;
                Pattern pattern = Pattern.compile(adapted_regex);
                matcher = pattern.matcher(content);
                if (matcher.find()) {
                    content = content.replace(matcher.group(), matcher.group(1) + value);
                } else {
                    content += "\n" + name + " = " + value;
                }
            }
            FileWriter fw = new FileWriter(FilePath.toFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            ModpackConfigUpdater.LOGGER.error("Can't write properties file", e);
        }
    }

    @Override
    public PropertiesFile merge(PropertiesFile other) {
        try {
            PropertiesFile merged = new PropertiesFile(getFilePath());
            merged.set_raw_value(mergePropertiesByIteratingKeySet(this.get_raw_value(), other.get_raw_value()));
            return merged;
        } catch (Exception e) {
            e.printStackTrace();
            ModpackConfigUpdater.LOGGER.error("Couldn't merge files");
        }
        return null;
    }

    private static Properties mergePropertiesByIteratingKeySet(Properties... properties) {
        Properties mergedProperties = new Properties();
        for (Properties property : properties) {
            Set<String> propertyNames = property.stringPropertyNames();
            for (String name : propertyNames) {
                String propertyValue = property.getProperty(name);
                mergedProperties.setProperty(name, propertyValue);
            }
        }
        return mergedProperties;
    }
}
