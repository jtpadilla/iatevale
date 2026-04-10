package io.github.jtpadilla.a2a.server.base.lib.util;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER })
@Documented
public @interface NotNull {
}