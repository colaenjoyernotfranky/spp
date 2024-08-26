import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Spp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: Spp [input] [output]");
        }
        if (args[0].equals("-d")) {
            decompile(args[1], args[2]);
        } else {
            compile(args[0], args[1]);
        }
    }

    private static void compile(String path, String outputPath) {
        try {
            String contents = Files.readString(new File(path).toPath());

            PrintWriter output = new PrintWriter(outputPath + ".cpp", StandardCharsets.US_ASCII);

            int numSpaces = -1;
            for (char c : contents.toCharArray()) {
                switch (c) {
                    case ' ':
                        numSpaces++;
                        break;
                    case '\n':
                        output.print((char) numSpaces);
                        numSpaces = -1;
                        break;
                    default:
                }
            }

            if (numSpaces != -1) {
                output.print((char) numSpaces);
            }

            output.close();

            Runtime r = Runtime.getRuntime();
            Process compilerProcess = r.exec(new String[]{"g++ " + outputPath + ".cpp -o " + outputPath});
            compilerProcess.waitFor();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(compilerProcess.getErrorStream()));
            String line;

            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            errorReader.close();
            if (compilerProcess.exitValue() != 0) {
                System.exit(compilerProcess.exitValue());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void decompile(String path, String outputPath) {
        try {
            System.out.println("Decompile");
            String contents = Files.readString(new File(path).toPath());

            PrintWriter output = new PrintWriter(outputPath, StandardCharsets.US_ASCII);

            for (char c : contents.toCharArray()) {
                output.println(getSpaces(c) + (int) c);
            }

            output.close();

            System.out.println("Finished. File at " + outputPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getSpaces(char c) {
        return new String(new char[(int) c]).replace('\0', ' ');
    }

}