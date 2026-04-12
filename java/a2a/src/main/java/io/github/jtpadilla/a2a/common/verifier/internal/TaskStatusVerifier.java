package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.TaskState;
import com.google.lf.a2a.v1.TaskStatus;

public class TaskStatusVerifier {

    static public TaskStatus verify(TaskStatus msg) {
        return new TaskStatusVerifier(msg).verify();
    }

    final private TaskStatus msg;

    private TaskStatusVerifier(TaskStatus msg) {
        this.msg = msg;
    }

    private TaskStatus getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("TaskStatus is null");
        }
        return msg;
    }

    private void verifyState(TaskStatus.Builder builder) {
        if (getThis().getState() == TaskState.TASK_STATE_UNSPECIFIED) {
            throw new IllegalArgumentException("TaskStatus.state is required");
        }
        builder.setState(getThis().getState());
    }

    private TaskStatus verify() {
        TaskStatus.Builder builder = TaskStatus.newBuilder();
        verifyState(builder);
        if (getThis().hasMessage()) {
            builder.setMessage(getThis().getMessage());
        }
        if (getThis().hasTimestamp()) {
            builder.setTimestamp(getThis().getTimestamp());
        }
        return builder.build();
    }

}
