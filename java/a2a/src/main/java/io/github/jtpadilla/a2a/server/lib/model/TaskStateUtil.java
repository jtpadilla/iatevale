package io.github.jtpadilla.a2a.server.lib.model;

import com.google.lf.a2a.v1.TaskState;

public class TaskStateUtil {

    static public boolean isTerminal(TaskState taskState) {
        return switch (taskState) {
            case TaskState.UNRECOGNIZED -> false; // TODO: Tal vez generar una excepcion...
            case TaskState.TASK_STATE_UNSPECIFIED -> false;
            case TaskState.TASK_STATE_SUBMITTED -> false;
            case TaskState.TASK_STATE_WORKING  -> false;
            case TaskState.TASK_STATE_COMPLETED  -> false;
            case TaskState.TASK_STATE_FAILED  -> true;
            case TaskState.TASK_STATE_CANCELED  -> true;
            case TaskState.TASK_STATE_INPUT_REQUIRED  -> false;
            case TaskState.TASK_STATE_REJECTED  -> true;
            case TaskState.TASK_STATE_AUTH_REQUIRED  -> false;
        };

    }
}
