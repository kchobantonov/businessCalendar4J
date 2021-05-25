package com.samuraism.bc4j;

/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @since 1.17
 */
final class Logger {
    private final java.util.logging.Logger jul;
    private final org.slf4j.Logger slf4j;
    final static boolean testEnvironment;

    static {
        boolean junitFound = false;
        try {
            Class.forName("org.junit.jupiter.api.Test");
            junitFound = true;
        } catch (ClassNotFoundException ignore) {
        }
        testEnvironment = junitFound;
    }

    static List<String> debugMessages = new ArrayList<>();
    static List<String> infoMessages = new ArrayList<>();
    static List<String> warnMessages = new ArrayList<>();
    static List<String> errorMessages = new ArrayList<>();

    Logger(java.util.logging.Logger julLogger, org.slf4j.Logger slf4jLogger) {
        this.jul = julLogger;
        this.slf4j = slf4jLogger;
    }

    public static void main(String[] args) {
        Logger.getLogger(Logger.class).debug(() -> "debug");
        Logger.getLogger(Logger.class).info(() -> "info");
        Logger.getLogger(Logger.class).warn(() -> "warn");
        Logger.getLogger(Logger.class).error(() -> "error");
    }

    static final boolean SLF4J_EXISTS_IN_CLASSPATH;

    static {
        boolean useSLF4J = false;
        try {
            // use SLF4J if it's found in the classpath
            Class.forName("org.slf4j.Logger");
            useSLF4J = true;
            getLogger(Logger.class).info(() -> "SLF4J Logger selected");
        } catch (ClassNotFoundException ignore) {
        }
        SLF4J_EXISTS_IN_CLASSPATH = useSLF4J;
        getLogger(Logger.class).info(() -> SLF4J_EXISTS_IN_CLASSPATH ? "SLF4J Logger selected" : "jul Logger selected");
    }

    /**
     * Returns a Logger instance associated with the specified class.
     *
     * @param clazz class
     * @return logger instance
     */
    static Logger getLogger(Class<?> clazz) {
        return SLF4J_EXISTS_IN_CLASSPATH ?
                new Logger(null, org.slf4j.LoggerFactory.getLogger(clazz)) :
                new Logger(java.util.logging.Logger.getLogger(clazz.getName()), null);
    }

    void debug(Supplier<String> supplier) {
        if (testEnvironment) {
            debugMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isDebugEnabled()) {
                slf4j.debug(supplier.get());
            }
        }
        if (jul.isLoggable(Level.FINEST)) {
            jul.finest(supplier.get());
        }
    }

    void info(Supplier<String> supplier) {
        if (testEnvironment) {
            infoMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isInfoEnabled()) {
                slf4j.info(supplier.get());
            }
        }
        if (jul.isLoggable(Level.INFO)) {
            jul.info(supplier.get());
        }
    }

    void warn(Supplier<String> supplier) {
        if (testEnvironment) {
            warnMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isWarnEnabled()) {
                slf4j.warn(supplier.get());
            }
        }
        if (jul.isLoggable(Level.WARNING)) {
            jul.warning(supplier.get());
        }
    }

    void warn(Supplier<String> supplier, Throwable th) {
        if (testEnvironment) {
            warnMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isWarnEnabled()) {
                slf4j.warn(supplier.get(), th);
            }
        }
        if (jul.isLoggable(Level.WARNING)) {
            jul.log(Level.WARNING, supplier.get(), th);
        }
    }

    void error(Supplier<String> supplier) {
        if (testEnvironment) {
            errorMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isErrorEnabled()) {
                slf4j.error(supplier.get());
            }
        }
        if (jul.isLoggable(Level.SEVERE)) {
            jul.severe(supplier.get());
        }
    }

    void error(Supplier<String> supplier, Throwable th) {
        if (testEnvironment) {
            errorMessages.add(supplier.get());
        }
        if (SLF4J_EXISTS_IN_CLASSPATH) {
            if (slf4j.isErrorEnabled()) {
                slf4j.error(supplier.get(), th);
            }
        }
        if (jul.isLoggable(Level.SEVERE)) {
            jul.log(Level.SEVERE, supplier.get(), th);
        }
    }
}