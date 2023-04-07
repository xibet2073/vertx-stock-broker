package com.keelient.vertx_stock_broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {
  public static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final List<String> ASSETS = Arrays
    .asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX");

  public static void attach(Router parent) {
    getAssets(parent);
  }

  //http://localhost:8888/assets
  private static void getAssets(Router parent) {
    parent.get("/assets").handler(context -> {
      final JsonArray response = new JsonArray();
      ASSETS.stream().map(Asset::new).forEach(response::add);
      LOG.info("Path {} responds with {}", context.normalizedPath(), response);
      context.response().end(response.toBuffer());
    });
  }
}
