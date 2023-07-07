package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/sample.txt";
        String sourceCode = null;
        try {
            sourceCode = readFile(filePath);

            Scanner scanner = new Scanner(sourceCode);
            Queue<Token> tokens = scanner.tokenize();

            Parser parser = new Parser(tokens);
            parser.parse();

            System.out.println("Parsing completed successfully!");

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (ParsingException e) {
            System.out.println(e.getMessage() +
                    " \nLine Number: " + e.getToken().lineNumber +
                    " - Position: " + e.getToken().position + "\n" +
                    "Line: " + getLineFromSourceCode(sourceCode, e.getToken().lineNumber) + "\n");
        }
    }

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }


    public static String getLineFromSourceCode(String sourceCode, int lineNumber) {
        String[] lines = sourceCode.split("\n");
        if (lineNumber > 0 && lineNumber <= lines.length) {
            return lines[lineNumber - 1];
        } else {
            return "Invalid line number";
        }
    }
}
