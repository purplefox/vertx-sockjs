/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.sockjs.impl;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.sockjs.SockJSServerOptions;
import io.vertx.ext.sockjs.SockJSSocket;

import static io.vertx.core.buffer.Buffer.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
class EventSourceTransport extends BaseTransport {

  private static final Logger log = LoggerFactory.getLogger(EventSourceTransport.class);

  EventSourceTransport(Vertx vertx,RouteMatcher rm, String basePath, LocalMap<String, Session> sessions, SockJSServerOptions options,
                       Handler<SockJSSocket> sockHandler) {
    super(vertx, sessions, options);

    String eventSourceRE = basePath + COMMON_PATH_ELEMENT_RE + "eventsource";

    rm.matchMethodWithRegEx(HttpMethod.GET, eventSourceRE, req -> {
      if (log.isTraceEnabled()) log.trace("EventSource transport, get: " + req.uri());
      String sessionID = req.params().get("param0");
      Session session = getSession(options.getSessionTimeout(), options.getHeartbeatPeriod(), sessionID, sockHandler);
      session.setInfo(req.localAddress(), req.remoteAddress(), req.uri(), req.headers());
      session.register(new EventSourceListener(options.getMaxBytesStreaming(), req, session));
    });
  }

  private class EventSourceListener extends BaseListener {

    final int maxBytesStreaming;
    boolean headersWritten;
    int bytesSent;
    boolean closed;

    EventSourceListener(int maxBytesStreaming, HttpServerRequest req, Session session) {
      super(req, session);
      this.maxBytesStreaming = maxBytesStreaming;
      addCloseHandler(req.response(), session);
    }

    public void sendFrame(String body) {
      if (log.isTraceEnabled()) log.trace("EventSource, sending frame");
      if (!headersWritten) {
        req.response().headers().set("Content-Type", "text/event-stream; charset=UTF-8");
        setNoCacheHeaders(req);
        setJSESSIONID(options, req);
        req.response().setChunked(true);
        req.response().write("\r\n");
        headersWritten = true;
      }
      StringBuilder sb = new StringBuilder();
      sb.append("data: ");
      sb.append(body);
      sb.append("\r\n\r\n");
      Buffer buff = buffer(sb.toString());
      req.response().write(buff);
      bytesSent += buff.length();
      if (bytesSent >= maxBytesStreaming) {
        if (log.isTraceEnabled()) log.trace("More than maxBytes sent so closing connection");
        // Reset and close the connection
        close();
      }
    }

    public void close() {
      if (!closed) {
        try {
          session.resetListener();
          req.response().end();
          req.response().close();
        } catch (IllegalStateException e) {
          // Underlying connection might already be closed - that's fine
        }
        closed = true;
      }
    }

  }
}
