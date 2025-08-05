package co2.co2Server;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class PlotPythonServer {
    private static final Logger logger = LoggerFactory.getLogger(PlotPythonServer.class);

    private static Process pythonServerProcess = null;
    public static final int port = 50051;

    @PostConstruct
    public void startOnBoot() {
        try {
            startPythonGrpcServerIfNotRunning();
        } catch (IOException e) {
            System.out.println("Failed to start Python gRPC server on boot:");
            e.printStackTrace();
        }
    }

    public void startPythonGrpcServerIfNotRunning() throws IOException {
        if (pythonServerProcess == null || !pythonServerProcess.isAlive()) {
            ProcessBuilder builder = new ProcessBuilder(
                    "python3", "plotServer.py", "-port", String.valueOf(port)
            );
            File pythonScriptDir = new File(System.getProperty("user.dir"), "src/main/python");
            builder.directory(pythonScriptDir);

            builder.redirectErrorStream(true);
            System.out.println("[Java PlotPythonServer]starting python server width command " + String.join(" ", builder.command()) + " in directory  " + builder.directory() );
            pythonServerProcess = builder.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonServerProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @PreDestroy
    public void stopPythonServer() {
        if (pythonServerProcess != null && pythonServerProcess.isAlive()) {
            pythonServerProcess.destroy();
            System.out.println("Python gRPC server stopped.");
        }
    }


}
