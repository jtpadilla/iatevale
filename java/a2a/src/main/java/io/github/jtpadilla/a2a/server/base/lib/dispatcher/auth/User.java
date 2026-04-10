package io.github.jtpadilla.a2a.server.base.lib.dispatcher.auth;

public interface User {
    boolean isAuthenticated();
    String getUsername();
}
