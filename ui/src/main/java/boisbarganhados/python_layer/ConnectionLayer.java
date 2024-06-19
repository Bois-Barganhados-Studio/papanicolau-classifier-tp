package boisbarganhados.python_layer;

public class ConnectionLayer {

    private static String windows_python_cmd = "python";
    private static String linux_python_cmd = "python3";
    private static String mac_python_cmd = "python3";

    /**
     * Get the python command based on the OS
     * 
     * @return the python command based on the OS
     */
    protected static String getPythonCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return windows_python_cmd;
        } else if (os.contains("nix") || os.contains("nux")) {
            return linux_python_cmd;
        } else if (os.contains("mac")) {
            return mac_python_cmd;
        }
        return null;
    }

}
