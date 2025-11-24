package co.elastic.apm.opbeans.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that simulates DT call by delegating some of the API call to other services, obtained 
 * from the environment variable OPBEANS_SERVICES
 * The probability to delegate the call is obtained from the environment variable OPBEANS_DT_PROBABILITY
 * If the probability is 0 or the list of services is empty, the redirection will be disabled
 *
 */
public class DTInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(DTInterceptor.class);
    
    private float dtProb;
    private String[] hostList;
    private RestTemplate restTemplate;
    private Random random = new Random();

    public DTInterceptor(Environment env) {

        hostList = env.getProperty("OPBEANS_SERVICES", "").split(",");
        hostList = Arrays.stream(hostList).filter(s -> !s.equals("")).toArray(String[]::new);
        try {
            dtProb = Float.parseFloat(env.getProperty("OPBEANS_DT_PROBABILITY", "0.5"));
        } catch (NumberFormatException ex) {
            dtProb = 0.5f;
        }

        // Disable DT if we don't have any hosts
        if (hostList.length == 0) {
            dtProb = 0f;
        } else {
            // pre-process urls for simplicity
            for (int i = 0; i < hostList.length; i++) {
                if (!hostList[i].startsWith("http")) { //make sure we have a protocol
                    hostList[i] = "http://" + hostList[i]+":3000";
                }
                if (hostList[i].endsWith("/")) { // remove trailing /
                    hostList[i] = hostList[i].substring(0, hostList[i].length()-1);
                }
            }
        }
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        log.debug("DT Probability: {}",dtProb);
        log.debug("DT Services: {}",Arrays.toString(hostList));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("Received request to {}",request.getRequestURI());
        if (random.nextFloat() <= dtProb) {
            String destination = hostList[random.nextInt(hostList.length)];
            log.debug("Executing remote call to  {}{}",destination,request.getRequestURI());
            try {
                String json = restTemplate.getForObject(destination + request.getRequestURI(), String.class);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json);
            } catch (RestClientException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RestCallException) {
                    RestCallException rce = (RestCallException) cause;
                    response.sendError(rce.statusCode, rce.statusText);
                    log.debug("Returned error {},{}",rce.statusCode, rce.statusText);
                } else {
                    log.error("Returned unknown error 500 ",e);
                    response.sendError(500, "Internal error while executing remote DT call");
                }
            }
            return false;
        }
        log.debug("Proceeding to local service");
        return true;
    }
    
    private static class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
            return (httpResponse.getStatusCode().is4xxClientError()
                    || httpResponse.getStatusCode().is5xxServerError());
        }

        @Override
        public void handleError(HttpMethod method, URI url, ClientHttpResponse httpResponse) throws IOException {
            throw new RestCallException(httpResponse.getStatusCode().value(), httpResponse.getStatusText());
        }
    }

    private static class RestCallException extends IOException {
        private static final long serialVersionUID = 1L;
        private int statusCode;
        private String statusText;

        public RestCallException(int statusCode, String statusText) {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }
    }
}
