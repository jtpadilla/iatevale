package io.github.jtpadilla.a2a.server.base.lib.dispatcher;

import io.github.jtpadilla.a2a.server.base.lib.dispatcher.auth.User;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCallContext {

    // TODO Todavía no estoy del todo seguro sobre los tipos de estos campos
    private final Map<Object, Object> modelConfig = new ConcurrentHashMap<>();
    private final Map<String, Object> state;
    private final User user;
    private final Set<String> requestedExtensions;
    private final Set<String> activatedExtensions;
    private final @Nullable String requestedProtocolVersion;
    private volatile @Nullable Runnable eventConsumerCancelCallback;

    public ServerCallContext(User user, Map<String, Object> state, Set<String> requestedExtensions) {
        this(user, state, requestedExtensions, null);
    }

    public ServerCallContext(User user, Map<String, Object> state, Set<String> requestedExtensions, @Nullable String requestedProtocolVersion) {
        this.user = user;
        this.state = state;
        this.requestedExtensions = new HashSet<>(requestedExtensions);
        this.activatedExtensions = new HashSet<>(); // Siempre comienza vacío, se llena más tarde mediante el código de la aplicación
        this.requestedProtocolVersion = requestedProtocolVersion;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public User getUser() {
        return user;
    }

    public Set<String> getRequestedExtensions() {
        return new HashSet<>(requestedExtensions);
    }

    public Set<String> getActivatedExtensions() {
        return new HashSet<>(activatedExtensions);
    }

    public void activateExtension(String extensionUri) {
        activatedExtensions.add(extensionUri);
    }

    public void deactivateExtension(String extensionUri) {
        activatedExtensions.remove(extensionUri);
    }

    public boolean isExtensionActivated(String extensionUri) {
        return activatedExtensions.contains(extensionUri);
    }

    public boolean isExtensionRequested(String extensionUri) {
        return requestedExtensions.contains(extensionUri);
    }

    public @Nullable String getRequestedProtocolVersion() {
        return requestedProtocolVersion;
    }

    /**
     * Establece el callback que se invocará cuando el cliente se desconecte o la llamada se cancele.
     * <p>
     * Este callback se utiliza normalmente para detener el ciclo de sondeo de EventConsumer cuando un cliente
     * se desconecta de un endpoint de streaming. Los transportes (JSON-RPC sobre HTTP/SSE, REST sobre HTTP/SSE,
     * gRPC streaming) invocan este callback cuando detectan que el cliente ha cerrado la conexión.
     * </p>
     * <p>
     * <strong>Seguridad de Hilos (Thread Safety):</strong> El callback puede ser invocado desde cualquier hilo,
     * dependiendo de la implementación del transporte. Las implementaciones deben ser seguras para hilos.
     * </p>
     * <strong>Ejemplo de uso:</strong>
     * <pre>{@code
     * EventConsumer consumer = new EventConsumer(queue);
     * context.setEventConsumerCancelCallback(consumer::cancel);
     * }</pre>
     *
     * @param callback el callback a invocar en la desconexión del cliente, o null para borrar cualquier callback existente
     * @see #invokeEventConsumerCancelCallback()
     */
    public void setEventConsumerCancelCallback(@Nullable Runnable callback) {
        this.eventConsumerCancelCallback = callback;
    }

    /**
     * Invoca el callback de cancelación de EventConsumer si se ha establecido uno.
     * <p>
     * Este método es llamado por las capas de transporte cuando un cliente se desconecta o cancela una
     * solicitud de streaming. Desencadena el callback registrado a través de
     * {@link #setEventConsumerCancelCallback(Runnable)}, que normalmente detiene el
     * ciclo de sondeo de EventConsumer.
     * </p>
     * <p>
     * <strong>Comportamiento específico del transporte:</strong>
     * </p>
     * <ul>
     *   <li><strong>JSON-RPC/REST sobre HTTP/SSE:</strong> Llamado desde Vert.x
     *       {@code HttpServerResponse.closeHandler()} cuando se cierra la conexión SSE</li>
     *   <li><strong>gRPC streaming:</strong> Llamado desde gRPC
     *       {@code Context.CancellationListener.cancelled()} cuando se cancela la llamada</li>
     * </ul>
     * <p>
     * <strong>Seguridad de Hilos (Thread Safety):</strong> Este método es seguro para hilos. El callback se
     * almacena en un campo volátil y se comprueba que no sea nulo antes de la invocación para evitar condiciones de carrera.
     * </p>
     * <p>
     * Si no se ha establecido ningún callback, este método no hace nada (no-op).
     * </p>
     *
     */
    public void invokeEventConsumerCancelCallback() {
        Runnable callback = this.eventConsumerCancelCallback;
        if (callback != null) {
            callback.run();
        }
    }
}