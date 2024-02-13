package br.com.nogueiranogueira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Solution {
    public static void main(String[] args) {
        String url;
        if (args == null || args.length <= 0){
            System.out.println("You dont inform a url, so we gonna run the app using a generic of Yahoo");
            url = "https://news.yahoo.com";
        }else{
            url = args[0];
        }
        HtmlParser parser = new HtmlParser();
        List<String> listaUrls = crawl(url, parser);
        System.out.println(listaUrls);
    }

    public static List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Set<String> result = ConcurrentHashMap.newKeySet();
        String hostname = getHostNames(startUrl);

        ExecutorService executor = Executors.newFixedThreadPool(64);
        result.add(startUrl);
        try {
            crawl(result, startUrl, hostname, executor, htmlParser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();

        try{
            executor.awaitTermination(10, TimeUnit.SECONDS);

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return new ArrayList<>(result);
    }

    private static String getHostNames(String url){
        int index = url.indexOf('/', 7);
        return (index != -1) ? url.substring(0, index) : url;
    }

    private static void crawl(Set<String> result, String start, String hostname, ExecutorService executor, HtmlParser parser) throws IOException {
        List<Future> futures = new ArrayList<>();
        for(String url : parser.getUrls(start)){
            System.out.println(url);
            if(url.startsWith(hostname) && result.add(url)){
                futures.add(executor.submit(() -> {
                    try {
                        crawl(result, url, hostname, executor, parser);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        }

        for(Future future : futures){
            try {
                future.get();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}