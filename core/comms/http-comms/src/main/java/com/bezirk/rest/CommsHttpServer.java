package com.bezirk.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * The comms HTTP server based of NANOHttpD implementation
 *
 * @author PIK6KOR
 */
public class CommsHttpServer extends RouterNanoHTTPD {
    private static final Logger logger = LoggerFactory.getLogger(CommsHttpServer.class);

    private static final int PORT = 8080;
    private static CommsHttpServer httpServer;
    private BezirkRestCommsManager restCommsManager = BezirkRestCommsManager.getInstance();

    private CommsHttpServer() {
        super(PORT);
        addMappings();
        System.out.println("\nRunning! Point your browers to http://localhost:" + PORT + "/ \n");
    }


    public static CommsHttpServer getInstance() {
        if (httpServer == null) {
            httpServer = new CommsHttpServer();
        }

        return httpServer;
    }

    /**
     * starts the NanoHTTPD server!!
     *
     * @return
     */
    public boolean startServer() {
        //ServerRunner.run(CommsHttpServer.class);
        logger.debug("STARTING Rest Server!!!");
        try {
            start();
            restCommsManager.setStarted(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Exception while starting server.. " + e);
        }
        return true;
    }

    /**
     * Stops the nanoHTTPD server!!
     *
     * @return
     */
    public boolean stopServer() {
        logger.debug("Rest server has been stopped!!!");
        stop();
        restCommsManager.setStarted(false);

        //interupt the sender thread...
        restCommsManager.getSenderThread().interrupt();
        return true;
    }


    @Override
    public Response serve(IHTTPSession session) {

        // TODO Auto-generated method stub
        return super.serve(session);
    }


    /**
     * Handlers for each unique routes
     */
    @Override
    public void addMappings() {
        super.addMappings();
        //addRoute("/bezirk", BezirkRestHandler.class);
        //this is the route which handles the request..
        addRoute("/bezirk/zirk", BezirkRestRequestHandler.class);

        //call the constructor, Should this be done ? cross check again ??
        new BezirkRestRequestHandler();

        //reponse route
        addRoute("/bezirk/zirk/response/:eventMsgId", BezirkRestResponseHandler.class);

    }


}
