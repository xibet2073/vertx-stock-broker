package com.keelient.vertx_stock_broker;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {
  public static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returnsQuoteForAsset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(
            vertx,
            new WebClientOptions()
                    .setDefaultPort(MainVerticle.PORT)
    );

    webClient.get("/quotes/AMZN")
            .send()
            .onComplete(testContext.succeeding(response -> {
              var json = response.bodyAsJsonObject();
              LOG.info("Response: {}", json);
              assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
              assertEquals(200, response.statusCode());
              testContext.completeNow();
            }));
  }

  @Test
  void returnsQuoteNotFoundForUnknownAsset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(
      vertx,
      new WebClientOptions()
        .setDefaultPort(MainVerticle.PORT)
    );

    webClient.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"message\":\"quote for asset UNKNOWN not avalilable\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
        assertEquals(404, response.statusCode());
        testContext.completeNow();
      }));
  }
}
