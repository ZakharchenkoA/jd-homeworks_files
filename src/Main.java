import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String saveDirectory = "/users/Andrey/Games/savegames/";
    final static List<String> saveFiles = new ArrayList<>();
    final static Map<String, GameProgress> gameProgresses = new LinkedHashMap<>();

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        File src = new File("/users/Andrey/Games/src");
        if (src.mkdir()) sb.append("Каталог 'src' создан");

        File res = new File("/users/Andrey/Games/res");
        if (res.mkdir()) sb.append("\nКаталог 'res' создан");

        File saveGame = new File("/users/Andrey/Games/savegames");
        if (saveGame.mkdir()) sb.append("\nКаталог 'savegames' создан");

        File temp = new File("/users/Andrey/Games/temp");
        if (temp.mkdir()) sb.append("\nКаталог 'temp' создан");

        File main = new File("/users/Andrey/Games/src/main");
        if (main.mkdir()) sb.append("\nКаталог 'main' создан");

        File test = new File("/users/Andrey/Games/src/test");
        if (test.mkdir()) sb.append("\nКаталог 'test' создан");

        File mainJava = new File(main, "Main.java");
        try {
            if (mainJava.createNewFile()) sb.append("\nФайл 'Main.java' был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        File utils = new File(main, "Utils.java");
        try {
            if (utils.createNewFile()) sb.append("\nФайл 'Utils.java' был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        File drawables = new File("/users/Andrey/Games/res/drawables");
        if (drawables.mkdir()) sb.append("\nКаталог 'drawables' создан");

        File vectors = new File("/users/Andrey/Games/res/vectors");
        if (vectors.mkdir()) sb.append("\nКаталог 'vectors' создан");

        File icons = new File("/users/Andrey/Games/res/icons");
        if (icons.mkdir()) sb.append("\nКаталог 'icons' создан");

        File tempTxt = new File(temp, "temp.txt");
        try {
            if (tempTxt.createNewFile()) sb.append("\nФайл 'temp.txt' был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        String text = sb.toString();

        try (FileWriter writer = new FileWriter(tempTxt)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


        File saves = new File(saveDirectory);
        if (saves.exists()) {
            gameProgresses.put(saveDirectory + "save1", new GameProgress(60, 2, 7, 1050));
            gameProgresses.put(saveDirectory + "save2", new GameProgress(100, 4, 21, 3020));
            gameProgresses.put(saveDirectory + "save3", new GameProgress(40, 10, 80, 4800));

            gameProgresses.forEach(Main::saveGame);
            gameProgresses.forEach((x, progress) -> saveFiles.add(x));

            zipFiles(saveDirectory + "/zip.zip", saveFiles);
        } else {
            System.out.println("Директории 'savegames' не существует");
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
                        System.out.println("Файл удален");
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
