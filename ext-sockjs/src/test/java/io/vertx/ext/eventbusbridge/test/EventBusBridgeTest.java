/*
 * Copyright 2014 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.eventbusbridge.test;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sockjs.BridgeOptions;
import io.vertx.ext.sockjs.SockJSServer;
import io.vertx.ext.sockjs.SockJSServerOptions;
import io.vertx.test.core.HttpTestBase;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class EventBusBridgeTest extends VertxTestBase {

  private HttpServer server;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    server = vertx.createHttpServer(new HttpServerOptions().setPort(HttpTestBase.DEFAULT_HTTP_PORT));
    SockJSServer sockJSServer = SockJSServer.sockJSServer(vertx, server);
    sockJSServer.bridge(new SockJSServerOptions().setPrefix("/eventbus"), new BridgeOptions().addInboundPermitted(new JsonObject()).addOutboundPermitted(new JsonObject()));
  }

  @Override
  public void tearDown() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    server.close(ar -> {
      assertTrue(ar.succeeded());
      latch.countDown();
    });
    awaitLatch(latch);
    super.tearDown();
  }

  @Test
  public void testSimple() {
    HttpClient client = vertx.createHttpClient(new HttpClientOptions());

    server.listen(ar -> {
      assertTrue(ar.succeeded());
      // We use raw websocket transport
      client.connectWebsocket(HttpTestBase.DEFAULT_HTTP_PORT, HttpTestBase.DEFAULT_HTTP_HOST, "/eventbus/websocket", ws -> {

        // Register
        JsonObject msg = new JsonObject().put("type", "register").put("address", "someaddress");
        ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(msg.encode(), true));

        // Send
        msg = new JsonObject().put("type", "send").put("address", "someaddress").put("body", "hello world");
        ws.writeFrame(io.vertx.core.http.WebSocketFrame.textFrame(msg.encode(), true));

        ws.handler(buff -> {
          String str = buff.toString();
          JsonObject received = new JsonObject(str);
          assertEquals("hello world", received.getString("body"));
          testComplete();
        });
      });
    });

    await();
  }
}
