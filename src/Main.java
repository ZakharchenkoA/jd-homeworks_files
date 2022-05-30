import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String saveDirectory = "/users/Andrey/Games/saveGame/";
    private static final String home = "/users/Andrey/Games/";

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        List<File> directories = Arrays.asList(
                new File(home + "src"),
                new File(home + "res"),
                new File(home + "saveGame"),
                new File(home + "temp"),
                new File(home + "src/main"),
                new File(home + "src/test"),
                new File(home + "res/drawables"),
                new File(home + "res/vectors"),
                new File(home + "res/icons"));

        directories.forEach(d -> {
            if (d.mkdir()) sb.append("директория ")
                    .append(d)
                    .append(" создана успешно\n");
            else sb.append("директория")
                    .append(d)
                    .append(" не создана\n");
        });

        List<File> files = Arrays.asList(
                new File(home + "src/main/Main.java"),
                new File(home + "src/main/Utils.java"),
                new File(home + "temp/temp.txt")
        );

        files.forEach(f -> {
            try {
                if (f.createNewFile()) sb.append("файл ")
                        .append(f)
                        .append(" создан успешно\n");
                else sb.append("файл ")
                        .append(f)
                        .append(" не создан\n");
            } catch (IOException e) {
                sb.append(e.getMessage()).append('\n');
            }
        });

        try (FileWriter writer = new FileWriter(home + "temp/temp.txt")) {
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            sb.append(e.getMessage()).append('\n');
        }

        GameProgress progress1 = new GameProgress(90, 1, 3, 200);
        GameProgress progress2 = new GameProgress(80, 3, 8, 400);
        GameProgress progress3 = new GameProgress(30, 4, 12, 600);

        ArrayList<String> saveFiles = new ArrayList<>();
        saveFiles.add(saveGame(saveDirectory + "save1.dat", progress1));
        saveFiles.add(saveGame(saveDirectory + "save2.dat", progress2));
        saveFiles.add(saveGame(saveDirectory + "save3.dat", progress3));

        zipFiles(saveDirectory + "save.zip", saveFiles);
    }

    public static String saveGame(String directory, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(directory + ".dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return directory;
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
