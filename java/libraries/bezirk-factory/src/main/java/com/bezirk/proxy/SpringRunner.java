package com.bezirk.proxy;

import com.bezirk.starter.UhuConfig;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for starting uhu using Spring's xml configuration method
 * UhuSpringRunner changed to SpringRunner
 */
public class SpringRunner {

    /**
     * This default spring configuration file is used to start uhu,
     * unless one is specified explicitly.
     */
    public static final String CONFIG_FILE_DEFAULT = "uhu.xml";

    /**
     * Directory where services can write data.  This is relative
     * to APP_HOME.
     */
    public static final String DATA_DIR_RELATIVE = "data";

    private static Logger log = LoggerFactory.getLogger(SpringRunner.class);

    private String configFile = CONFIG_FILE_DEFAULT;

    private ApplicationContext springContext = null;

    private String appHome = null;

    public SpringRunner() {
        /*
		 * Set the application home directory. If we are running from the
		 * binary distribution, appHome is set to: /path/to/uhu-version.
		 * If we are running from the developement envronment, 
		 * appHome is set to the project root directory.
		 */
        appHome = System.getenv().get("APP_HOME");

        if (appHome == null || appHome.isEmpty()) {
            appHome = "."; // current working dir means service root
        }
    }

    /**
     * Start uhu using the configuration file specified in the configFile data member
     *
     * @throws Exception
     */
    public void run() {
        // Read in spring configuration file
        log.info("Initializing Spring context.");
        springContext = new ClassPathXmlApplicationContext(configFile);

        UhuConfig uhuConfig = (UhuConfig) springContext.getBean("uhuSettings");

        if (uhuConfig == null) {
            log.error("uhuSettings was not found");
            return;
        }

        log.info("Starting Bezirk...");
        String uhuDataPath = uhuConfig.getDataPath();
        uhuConfig.setDataPath(appHome + File.separator + uhuDataPath);
        Proxy api = (Proxy) Factory.getInstance(uhuConfig);

        // Get all ServiceRunners specified in the app context
        Map<String, IServiceRunner> services = springContext.getBeansOfType(IServiceRunner.class);

        int numServices = services.size();
        if (numServices < 1) {
            log.error("Did not find any ServiceRunners in config file");
            return;
        }
        log.info("Found " + numServices + " ServiceRunners: " + services.keySet());

        // Used to record started and failed services
        List<String> startedServices = new ArrayList<String>();
        List<String> failedServices = new ArrayList<String>();

        // For each service: 1. set the data path, 2. initialize, and 3. start
        for (IServiceRunner service : services.values()) {
            String name = service.getClass().getSimpleName();
            log.info("Starting uhu service with runner: " + name);

            // 1. Set data path
            String dataPath = buildDataPath(service);
            service.setDataPath(dataPath);
            log.info("Data path was set to: " + service.getDataPath());

            try {
                log.debug("Initializing service: " + name);
                service.init(); // 2. Initialize data members, if necessary
                log.debug("Running service: " + name);
                service.run(); //  3. Start the service

                // Service successfully started
                startedServices.add(name);
            }
            // Don't exit uhu if a service fails to start
            catch (Exception e) {
                failedServices.add(name);
                System.out.println();
                log.error("There was a problem starting service with runner: "
                        + name, e);
            }
        }

        log.info("Uhu has started with these services:" + startedServices);
        if (!failedServices.isEmpty()) {
            log.warn("These services could not be started:" + failedServices);
        }
    }

    /**
     * Create the data path for this particular service. If the service
     * has explicitly set a relative dataPath, we use that value.  If
     * dataPath is not set, dataPath is set to appHome/data.  If the
     * service specifies an *absolute* path, we set dataPath to the
     * default.  This is because we are requiring the dataPath to be
     * relative to appHome.
     *
     * @param service
     * @return
     */
    private String buildDataPath(IServiceRunner service) {
		
		/* dataPath is where the service can write its data. We add a 
		 * subDirectory to this later on depending on whether the 
		 * service has set an explicit path in its app context */
        String dataPath = appHome + File.separator;

        // If service has set an explicit *relative* dataPath, use it
        if (UhuValidatorUtility.checkForString(service.getDataPath())
                && !service.getDataPath().equals(".")) {
			/* Any dataPath supplied by the config file needs to be 
			   relative to appHome, so we disallow absolute paths here */
            File userSpecifiedPath = new File(service.getDataPath());
            if (userSpecifiedPath.isAbsolute()) {
                dataPath += DATA_DIR_RELATIVE;
                log.warn("Uhu services should not specify an absolute dataPath. Setting dataPath to: "
                        + dataPath);
            }
            // Set the user-specified path relative to appHome
            else {
                dataPath += service.getDataPath();
            }
        }
        // Otherwise, use the default
        else {
            dataPath += DATA_DIR_RELATIVE;
        }

        return dataPath;
    }

    /**
     * XML configuration file used to start uhu.  If this is not set, then
     * CONFIG_FILE_DEFAULT is used.
     * <p/>
     * IMPORTANT:  it is assumed that this file is on the classpath
     *
     * @param configFile
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
