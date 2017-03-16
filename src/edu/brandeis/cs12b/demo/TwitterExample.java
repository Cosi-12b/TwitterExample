package edu.brandeis.cs12b.demo;

public class TwitterExample {

  public static void main(String[] args) throws InterruptedException {
    TwitterFeed myFeed = new TwitterFeed();
    myFeed.prepare("trump and clinton");
    myFeed.run();
  }
}
