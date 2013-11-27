/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.similcraft.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author Per
 */
public class LogFormatter extends Formatter {

    public void setFormater(Logger logger) {
        logger.setUseParentHandlers(false);
        Handler conHdlr = new ConsoleHandler();
        conHdlr.setFormatter(this);
        logger.addHandler(conHdlr);
    }

    public LogFormatter() {
        super();
    }

    @Override
    public String format(LogRecord record) {
        return record.getLevel() + ": " + record.getMessage() + "\r\n";
    }
}
