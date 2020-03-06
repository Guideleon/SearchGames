package sample;

public class ConfigFile {

    public static String getFile(String path){
        return ConfigFile.class.getResource(path).getPath();
    }

}
