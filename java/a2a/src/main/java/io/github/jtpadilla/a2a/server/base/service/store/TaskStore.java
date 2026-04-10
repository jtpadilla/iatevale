package io.github.jtpadilla.a2a.server.base.service.store;

import com.google.lf.a2a.v1.Task;
import org.jspecify.annotations.Nullable;

public interface TaskStore {
    void save(Task task, boolean isReplicated);
    @Nullable Task get(String taskId);
    void delete(String taskId);
    ListTasksResult list(ListTasksParams params);
}
