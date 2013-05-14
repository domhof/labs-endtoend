package org.openengsb.labs.endtoend.karaf.shell;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;

public class KarafShell implements Shell {
    private final PrintWriter pw;
    private final OutputHandler outputHandler;

    public KarafShell(OutputStream output, InputStream input, String applicationName) {
        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
        this.outputHandler = new OutputHandler(new InputStreamReader(input), new KarafPromptRecognizer("karaf",
                applicationName));
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.waitForPrompt(timeout, timeUnit);
    }

    public void execute(String command) {
        this.pw.println(command);
        this.pw.flush();
    }

    @Override
    public String execute(String command, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        this.pw.println(command);
        this.pw.flush();
        try {
            return this.outputHandler.getOutput(timeout, timeUnit);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException(command, e);
        }
    }

    public void close() {
        this.pw.close();
        this.outputHandler.shutdown();
    }
}
