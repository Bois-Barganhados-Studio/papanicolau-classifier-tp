package boisbarganhados.python_layer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class Requirements extends ConnectionLayer{

    private static final String REQUIREMENTS_PATH = "ai/scripts/requirements.py";

    public static boolean loadRequirements() {
        var status = false;
        try {
            var requirementsPath = REQUIREMENTS_PATH;
            String[] command = new String[] { getPythonCommand(), requirementsPath, requirementsPath.replace("requirements.py", "requirements.txt") };
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            process.waitFor();
            status = process.exitValue() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}