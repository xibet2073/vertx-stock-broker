package com.keelient.vertx_stock_broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsRestApi {
  public static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static void attach(Router parent) {
    getAssets(parent);
  }

  //http://localhost:8888/assets
  private static void getAssets(Router parent) {
    parent.get("/assets").handler(context -> {
      final JsonArray response = new JsonArray();
      response.add(new Asset("AAPL"));
      response.add(new Asset("AMZN"));
      response.add(new Asset("TSLA"));
      LOG.info("Path {} responds with {}", context.normalizedPath(), response);
      context.response().end(response.toBuffer());
    });
  }
}
