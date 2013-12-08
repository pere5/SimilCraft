/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft;

import org.similcraft.engine.Engine;
import org.similcraft.log.LogFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 *
 * @author Per
 */
@Component
public class Main {

    @Autowired
    Engine engine;

    public static final Logger log = Logger.getLogger(Main.class.getName());
    static { (new LogFormatter()).setFormater(log); }

    public static void main (String[] args) {
        //initiate and load Spring context
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Main main = context.getBean(Main.class);
        main.start(args);
    }

    private void start(String[] args) {
        log.info("Starting application");
        engine.run();
        log.info("Closing application");
    }

    public String test() {
        return "lol";
    }
}
