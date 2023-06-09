package com.keelient.vertx_stock_broker.quotes;

import com.keelient.vertx_stock_broker.watchList.WatchList;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {
  public static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);
  public static void attach(final Router parent) {
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<UUID, WatchList>();
    final String path = "/account/watchlist/:accountId";
    getWatchList(parent, watchListPerAccount, path);
    putWatchList(parent, watchListPerAccount, path);
    deleteWatchList(parent, watchListPerAccount, path);
  }
  private static void deleteWatchList(Router parent, HashMap<UUID, WatchList> watchListPerAccount, String path) {
    parent.delete(path).handler(context -> {
      String accountId = getAccountId(context);
      final WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
      LOG.info("Deleted {} Remaining: {}", deleted, watchListPerAccount.values());
      context.response()
        .end(deleted.toJsonObject().toBuffer());
      }
    );
  }

  private static void putWatchList(Router parent, HashMap<UUID, WatchList> watchListPerAccount, String path) {
    parent.put(path).handler(context -> {
      final String accountId = getAccountId(context);
      var json = context.getBodyAsJson();
      var watchList = json.mapTo(WatchList.class);
      watchListPerAccount.put(UUID.fromString(accountId), watchList);
      context.response().end(json.toBuffer());
      }
    );
  }
  private static void getWatchList(Router parent, HashMap<UUID, WatchList> watchListPerAccount, String path) {
    parent.get(path).handler(context -> {
      final String accountId = getAccountId(context);
      var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
      if (watchList.isEmpty()) {
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "whatchlist for account " + accountId + " not avalilable")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
      }
      context.response().end(watchList.get().toJsonObject().toBuffer());
    });
  }


  private static String getAccountId(RoutingContext context) {
    final String accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
