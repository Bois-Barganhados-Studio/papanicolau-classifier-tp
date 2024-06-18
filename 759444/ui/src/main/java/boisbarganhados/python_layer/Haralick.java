package boisbarganhados.python_layer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class Haralick {


    private static final String HARALICK_SCRIPT_PATH = "ai/scripts/haralick.py";

    public static List<String> runHaralick(String imagePath) {
        var result = new ArrayList<String>();
        try {
            String[] command = new String[] { "python",HARALICK_SCRIPT_PATH, imagePath};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            process.waitFor();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

}