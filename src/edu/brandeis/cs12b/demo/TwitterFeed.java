package edu.brandeis.cs12b.demo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterFeed {
	private Hosts hosebirdHosts;
	private StatusesFilterEndpoint hosebirdEndpoint;
	private List<String> terms;
	private Authentication hosebirdAuth;
	Client hosebirdClient;
	BlockingQueue<String> msgQueue;
	BlockingQueue<Event> eventQueue;

	public void prepare(String term) {
		setupHoseBird();
		setTerms(term);
		authenticate();
	}

	public void run() throws InterruptedException {
		startHosebird();
		processMessages();
		hosebirdClient.stop();
	}

	private void setupHoseBird() {
		hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		hosebirdEndpoint = new StatusesFilterEndpoint();
	}

	private void setTerms(String term) {
		terms = Arrays.asList(term);
		hosebirdEndpoint.trackTerms(terms);
	}

	private void authenticate() {
	    hosebirdAuth = new OAuth1("ipH5HPlDfK4iiABttWvFU7Jus", "vFhAcJFhEht7bxRmwAWnEyoB5mVoJGeJsackcbvweaVa6MOfiv", "49873-7lmcyOP9Zd1PSKvlypkKUztV06GtZpEy3CJnEH8SDfJq", "fcW8sjGmHsYuRijGwBFWiYtJqldMfcXo9p9ZbsdUC9F6K");

	}

	private void startHosebird() {
		msgQueue = new LinkedBlockingQueue<String>(100000);
		eventQueue = new LinkedBlockingQueue<Event>(1000);

		// Connect to service and start watching for the terms of interest
		ClientBuilder builder = new ClientBuilder()
		    .name("Brandeis-Client-1")
		    .hosts(hosebirdHosts)
		    .authentication(hosebirdAuth)
			.endpoint(hosebirdEndpoint)
			.processor(new StringDelimitedProcessor(msgQueue));
		hosebirdClient = builder.build();
		hosebirdClient.connect();
	}

	private void processMessages() throws InterruptedException {
		while (!hosebirdClient.isDone()) {
		  System.out.println("Waiting...");
		  String msg = msgQueue.poll(5, TimeUnit.SECONDS);
		  if (msg == null) {
		    System.out.println("Did not receive a message in 5 seconds");
		  } else {
		    processMessage(msg);
		  }
		}
	}

	private void processMessage(String msg) {
		System.out.println(msg);
	}
}
