import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String saveDirectory = "/users/Andrey/Games/saveGame/";

    private static final String home = "/users/Andrey/Games/";

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        Map<String, File> directories = new HashMap<>();
        directories.put("src", new File(home + "src"));
        directories.put("res", new File(home + "res"));
        directories.put("saveGame", new File(home + "saveGame"));
        directories.put("temp", new File(home + "temp"));
        directories.put("main", new File(home + "src/main"));
        directories.put("test", new File(home + "src/test"));
        directories.put("drawables", new File(home + "res/drawables"));
        directories.put("vectors", new File(home + "res/vectors"));
        directories.put("icons", new File(home + "res/icons"));

        directories.forEach((k, v) -> {
            if (v.mkdir()) {
                sb.append("Каталог ").append(k).append(" создан\n");
            }
        });

        Map<String, File> files = new HashMap<>();
        files.put("Main",
                new File(directories.get("main") + "/Main.java")
        );
        files.put("Utils",
                new File(directories.get("main").getAbsolutePath() + "/Utils.java")
        );
        files.put("temp",
                new File(directories.get("temp").getAbsolutePath() + "/temp.txt")
        );

        files.forEach((k, v) -> {
            try {
                if (v.createNewFile()) {
                    sb.append("Файл ").append(k).append(" создан\n");
                }
            }catch (IOException ex){
                ex.getStackTrace();
            }
        });

        String text = sb.toString();

        try (FileWriter writer = new FileWriter(files.get("temp"))) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


        List<String> saveFiles = new ArrayList<>();
        Map<String, GameProgress> gameProgresses = new HashMap<>();

        File saves = new File(saveDirectory);
        if (saves.exists()) {
            gameProgresses.put(saveDirectory + "save1", new GameProgress(60, 2, 7, 1050));
            gameProgresses.put(saveDirectory + "save2", new GameProgress(100, 4, 21, 3020));
            gameProgresses.put(saveDirectory + "save3", new GameProgress(40, 10, 80, 4800));

            gameProgresses.forEach(Main::saveGame);
            gameProgresses.forEach((x, progress) -> saveFiles.add(x));

            zipFiles(saveDirectory + "/zip.zip", saveFiles);
        } else {
            System.out.println("Директории 'saveGame' не существует");
        }
    }

    public static void saveGame(String directory, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(directory + ".dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void zipFiles(String path, List<String> files) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(path)) {

            files.forEach(f -> {
                try {
                    ZipEntry zipEntry = new ZipEntry(f + ".dat");
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                    File file = new File(f + ".dat");
                    if (file.delete()) {
                        System.out.println("Файл " + f + " удален");
                    } else {
                        System.out.println("Ошибка удаления файла");
                    }
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
