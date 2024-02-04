package it.polimi.codekatabattle.models;

import lombok.Data;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

@Data
public class ExecuteResult {

    private String output;

    private String error;

    private Long exitCode;

    public static ExecuteResult fromContainer(GenericContainer<?> container) {
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setOutput(container.getLogs(OutputFrame.OutputType.STDOUT));
        executeResult.setError(container.getLogs(OutputFrame.OutputType.STDERR));
        executeResult.setExitCode(container.getContainerInfo().getState().getExitCodeLong());
        return executeResult;
    }

}
