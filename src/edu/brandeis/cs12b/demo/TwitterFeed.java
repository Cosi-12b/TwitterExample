package edu.brandeis.cs12b.demo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
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
		hosebirdAuth = new OAuth1("hDIsWy2si3nTR8iKhEc9knVRi", "rKSoW2kEsJQ0sX0a1etORJXTLBE779kCtOg52hJC9RyMuhAoK4",
				"49873-5QFAa6KbNNxlNKgSFS52SC1hslF4pFEAmB5dHsp3aNZO", "TjspsD2rbF82dzI4B85H7lzbnEjnKPQgSqqqGLvofta0j");
	}

	private void startHosebird() {
		msgQueue = new LinkedBlockingQueue<String>(100000);

		// Connect to service and start watching for the terms of interest
		ClientBuilder builder = new ClientBuilder().hosts(hosebirdHosts).authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint).processor(new StringDelimitedProcessor(msgQueue));
		hosebirdClient = builder.build();
		hosebirdClient.connect();
	}

	private void processMessages() throws InterruptedException {
		List<String> tokens = new LinkedList<String>();
		while (tokens.size() < 1000) {
			String msg = msgQueue.take();
			processMessage(msg);
		}

	}

	private void processMessage(String msg) {
		System.out.println(msg);
	}
}
