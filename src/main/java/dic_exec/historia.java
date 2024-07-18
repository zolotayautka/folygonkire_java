package dic_exec;

import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class historia {
    private Vector<String> history;
    final String F = "history.txt";
    public historia() {
        new_file();
        history = new Vector<String>();
        read();
    }
    public historia(String kotoba, String imi) {
        new_file();
        history = new Vector<String>();
        read();
        String line = kotoba + "  " + imi;
        int size = history.size();
        if (size == 0){
            write(line);
        } else if (line.compareTo(history.get(size-1)) != 0) {
            write(line);
        }
    }
    private void read(){
        Scanner scanner = null;
        try {
            File file = new File(F);
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                history.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    private void write(String line){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(F, true));
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void new_file(){
        File file = new File(F);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Vector<String> return_history(){
        return history;
    }
}