package oop.ex5.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class 'reads' the given Sjava file, and extract it's content
 * into a list of String, in order to be able to process it easily.
 */
public class SjavaFileReader {

    /**
     * The path to the sourceFile (the .sjava file).
     */
    private final String sourceFilePath;

    /**
     * A BufferedReader instance, used to read the file's content.
     */
    private BufferedReader bufferedReader = null;

    /**
     * A list of Strings, which holds the file's content (each line in
     * the .sjava file is reserved in a single list cell).
     */
    private List<String> fileContent;

    /**
     * The class's constructor.
     *
     * @param sourceFilePath The path to the sourceFile (the .sjava file).
     * @throws FileNotFoundException In case the path to the file is invalid.
     */
    SjavaFileReader(String sourceFilePath) throws FileNotFoundException {
        this.sourceFilePath = sourceFilePath;
        openReader();
    }

    /**
     * This method creates a new BufferedReader instance, and sets it as a class data member.
     *
     * @throws FileNotFoundException In case the path to the file is invalid.
     */
    public void openReader() throws FileNotFoundException {
        BufferedReader reader;
        // opening a reader with the given sourceFile path
        reader = new BufferedReader(new FileReader(this.sourceFilePath));
        this.bufferedReader = reader;

    }

    /**
     * This method extracts data from the commands file into an array, line by line
     * or in case of invalid section structure.
     *
     * @throws IOException If failed to read one of the lines.
     */
    public void readFile() throws IOException {
        List<String> fileContent = new ArrayList<>();
        String line = this.bufferedReader.readLine();
        while (line != null) {
            fileContent.add(line);
            line = this.bufferedReader.readLine();
        }
        this.fileContent = fileContent;
    }

    /**
     * A getter to the file content.
     *
     * @return The list which holds the file content.
     */
    public List<String> getFileContent() {
        return fileContent;
    }

}
