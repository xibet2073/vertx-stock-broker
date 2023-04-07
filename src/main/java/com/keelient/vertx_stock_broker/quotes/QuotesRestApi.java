package com.keelient.vertx_stock_broker.quotes;

import com.keelient.vertx_stock_broker.assets.Asset;
import com.keelient.vertx_stock_broker.assets.AssetsRestApi;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);
  final static Map<String, Quote> cachedQuotes = new HashMap<>();

  public static void attach(Router parent) {
    getQuotes(parent);
  }

  //http://localhost:8888/quotes/AMZN
  private static void getQuotes(Router parent) {
    AssetsRestApi.ASSETS.forEach(symbol ->
      cachedQuotes.put(symbol, initRandomQuote(symbol))
    );

    parent.get("/quotes/:asset").handler(context -> {
      final String assetParam = context.pathParam("asset");
      LOG.debug("Asset parameter: {}", assetParam);

      final var quote = cachedQuotes.get(assetParam);
      final JsonObject response = quote.toJsonObject();
      LOG.info("Path {} responds with {}", context.normalizedPath(), response);
      context.response().end(response.toBuffer());
      }
    );
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 200));
  }
}
