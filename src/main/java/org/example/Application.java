package org.example;


import com.sun.tools.javac.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Hello World!");
    }
}
