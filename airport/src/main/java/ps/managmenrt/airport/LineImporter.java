package ps.managmenrt.airport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineImporter {

    // Base directory for SQL file operations
    public String getBaseDirectory() {
        return "C:\\Users\\ahmed\\IdeaProjects\\airport\\src\\main\\java\\mysql";
    }

    // Retrieves the SQL file path based on the index (0-9)
    public Path getSqlFilePath(int fileIndex) {
        String[] fileNames = {
                "sessiondata.sql", "tickets.sql", ".sql", ".sql", ".sql",
                ".sql", ".sql", ".sql", ".sql", ".sql"
        };

        if (fileIndex < 0 || fileIndex >= fileNames.length) {
            throw new IllegalArgumentException("Invalid file index. Please select an index between 0 and 9.");
        }

        return Paths.get(getBaseDirectory(), fileNames[fileIndex]);
    }

    // Reads specific lines from an SQL file and returns them as a List of strings
    public List<String> readSqlFile(int fileIndex, int startLine, int endLine) throws IOException {
        Path filePath = getSqlFilePath(fileIndex);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int currentLine = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLine >= startLine && currentLine <= endLine) {
                    lines.add(line);
                }
                currentLine++;
            }
        }
        return lines;
    }

    // Exports specified SQL lines as a single concatenated string
    public String exportSqlLinesAsString(int fileIndex, int startLine, int endLine) {
        try {
            List<String> lines = readSqlFile(fileIndex, startLine, endLine);
            return lines.stream().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Failed to read SQL lines.";
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java LineImporter <fileIndex> <startLine> <endLine>");
            return;
        }

        int fileIndex = Integer.parseInt(args[0]);
        int startLine = Integer.parseInt(args[1]);
        int endLine = Integer.parseInt(args[2]);

        LineImporter importer = new LineImporter();

        // Call exportSqlLinesAsString and return the result
        String result = importer.exportSqlLinesAsString(fileIndex, startLine, endLine);

        // Print the result or use it as needed (in a real application, you could return it instead)
        System.out.println(result);
    }
}
