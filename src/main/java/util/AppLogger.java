package util;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {

    private static final Logger logger = Logger.getLogger("AppLogger");

    static {
        setupLogger();
    }

    private static void setupLogger() {
        try {
            LogManager.getLogManager().reset();
            logger.setUseParentHandlers(false);

            //logger til konosol - når projektet er færdigt og skal i produktion fjernes det så der kun logges til fil
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new CustomFormatter());
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("logs/app.log", true);
            fileHandler.setLevel(Level.WARNING);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);

        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "Fejl ved opsætning af logsystem", e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return formatMessage(record);
        }
    }
}