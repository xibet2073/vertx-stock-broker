package com.keelient.vertx_stock_broker.test;

import com.keelient.vertx_stock_broker.MainVerticle;
import com.keelient.vertx_stock_broker.assets.Asset;
import com.keelient.vertx_stock_broker.watchList.WatchList;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi {
  public static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void addsAndReturnsWatchListForAccount(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(
            vertx,
            new WebClientOptions()
                    .setDefaultPort(MainVerticle.PORT)
    );
    final UUID uuid = UUID.randomUUID();
    webClient.put("/account/watchlist/" + uuid.toString())
      .sendJson(getBody())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"AAPL\"}]}", json.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      })).compose(next -> {
        webClient.get("/account/watchlist/" + uuid.toString())
          .send()
          .onComplete(testContext.succeeding(
            response -> {
              var json = response.bodyAsJsonObject();
              LOG.info("Response: GET {}", json);
              assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"AAPL\"}]}", json.encode());
              assertEquals(200, response.statusCode());
              testContext.completeNow();
            }
          ));
        return Future.succeededFuture();
      });
  }

  private JsonObject getBody() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA"),
      new Asset("AAPL")
    )).toJsonObject();
  }
}
