package boisbarganhados.python_layer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class Classifier {

    public static final String IMAGES_PY_CLASSIFIER_TMP = "ai/imgs_temp/";

    private static final String CLASSIFIER_MODELS = "ai/models/";

    private static final String CLASSIFIER_SCRIPT_PATH = "ai/scripts/papanicolau_classifier.py";

    public static List<String> classify(String imagePath) {
        var result = new ArrayList<String>();
        try {
            String[] command = new String[] { "python", CLASSIFIER_SCRIPT_PATH, imagePath, CLASSIFIER_MODELS};
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