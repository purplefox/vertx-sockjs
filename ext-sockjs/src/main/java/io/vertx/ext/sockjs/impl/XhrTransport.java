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
import io.vertx.ext.routematcher.RouteMatcher;
import io.vertx.ext.sockjs.SockJSServerOptions;
import io.vertx.ext.sockjs.SockJSSocket;

import static io.vertx.core.buffer.Buffer.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
class XhrTransport extends BaseTransport {

  private static final Logger log = LoggerFactory.getLogger(XhrTransport.class);

  private static final Buffer H_BLOCK;

  static {
    byte[] bytes = new byte[2048 + 1];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte)'h';
    }
    bytes[bytes.length - 1] = (byte)'\n';
    H_BLOCK = buffer(bytes);
  }

  XhrTransport(Vertx vertx, RouteMatcher rm, String basePath, LocalMap<String, Session> sessions, SockJSServerOptions options,
               Handler<SockJSSocket> sockHandler) {

    super(vertx, sessions, options);

    String xhrBase = basePath + COMMON_PATH_ELEMENT_RE;
    String xhrRE = xhrBase + "xhr";
    String xhrStreamRE = xhrBase + "xhr_streaming";

    Handler<HttpServerRequest> xhrOptionsHandler = createCORSOptionsHandler(options, "OPTIONS, POST");

    rm.matchMethodWithRegEx(HttpMethod.OPTIONS, xhrRE, xhrOptionsHandler);
    rm.matchMethodWithRegEx(HttpMethod.OPTIONS, xhrStreamRE, xhrOptionsHandler);

    registerHandler(rm, sockHandler, xhrRE, false, options);
    registerHandler(rm, sockHandler, xhrStreamRE, true, options);

    String xhrSendRE = basePath + COMMON_PATH_ELEMENT_RE + "xhr_send";

    rm.matchMethodWithRegEx(HttpMethod.OPTIONS, xhrSendRE, xhrOptionsHandler);

    rm.matchMethodWithRegEx(HttpMethod.POST, xhrSendRE, req -> {
      if (log.isTraceEnabled()) log.trace("XHR send, post, " + req.uri());
      String sessionID = req.params().get("param0");
      final Session session = sessions.get(sessionID);
      if (session != null && !session.isClosed()) {
        handleSend(req, session);
      } else {
        req.response().setStatusCode(404);
        setJSESSIONID(options, req);
        req.response().end();
      }
    });
  }

  private void registerHandler(RouteMatcher rm, Handler<SockJSSocket> sockHandler, String re,
                               boolean streaming, SockJSServerOptions options) {
    rm.matchMethodWithRegEx(HttpMethod.POST, re, req -> {
      if (log.isTraceEnabled()) log.trace("XHR, post, " + req.uri());
      setNoCacheHeaders(req);
      String sessionID = req.params().get("param0");
      Session session = getSession(options.getSessionTimeout(), options.getHeartbeatPeriod(), sessionID, sockHandler);
      session.setInfo(req.localAddress(), req.remoteAddress(), req.uri(), req.headers());
      session.register(streaming? new XhrStreamingListener(options.getMaxBytesStreaming(), req, session) : new XhrPollingListener(req, session));
    });
  }

  private void handleSend(HttpServerRequest req, Session session) {
    req.bodyHandler(buff -> {
      String msgs = buff.toString();
      if (msgs.equals("")) {
        req.response().setStatusCode(500);
        req.response().end("Payload expected.");
        return;
      }
      if (!session.handleMessages(msgs)) {
        sendInvalidJSON(req.response());
      } else {
        req.response().headers().set("Content-Type", "text/plain; charset=UTF-8");
        setNoCacheHeaders(req);
        setJSESSIONID(options, req);
        setCORS(req);
        req.response().setStatusCode(204);
        req.response().end();
      }
      if (log.isTraceEnabled()) log.trace("XHR send processed ok");
    });
  }

  private abstract class BaseXhrListener extends BaseListener {

    boolean headersWritten;

    BaseXhrListener(HttpServerRequest req, Session session) {
      super(req, session);
    }

    public void sendFrame(String body) {
      if (log.isTraceEnabled()) log.trace("XHR sending frame");
      if (!headersWritten) {
        req.response().headers().set("Content-Type", "application/javascript; charset=UTF-8");
        setJSESSIONID(options, req);
        setCORS(req);
        req.response().setChunked(true);
        headersWritten = true;
      }
    }

    public void close() {
    }
  }

  private class XhrPollingListener extends BaseXhrListener {

    XhrPollingListener(HttpServerRequest req, Session session) {
      super(req, session);
      addCloseHandler(req.response(), session);
    }

    public void sendFrame(String body) {
      super.sendFrame(body);
      req.response().write(body + "\n");
      close();
    }

    public void close() {
      if (log.isTraceEnabled()) log.trace("XHR poll closing listener");
      if (!closed) {
        try {
          session.resetListener();
          req.response().end();
          req.response().close();
          closed = true;
        } catch (IllegalStateException e) {
          // Underlying connection might already be closed - that's fine
        }
      }
    }
  }

  private class XhrStreamingListener extends BaseXhrListener {

    int bytesSent;
    int maxBytesStreaming;

    XhrStreamingListener(int maxBytesStreaming, HttpServerRequest req, Session session) {
      super(req, session);
      this.maxBytesStreaming = maxBytesStreaming;
      addCloseHandler(req.response(), session);
    }

    public void sendFrame(String body) {
      boolean hr = headersWritten;
      super.sendFrame(body);
      if (!hr) {
        req.response().write(H_BLOCK);
      }
      String sbody = body + "\n";
      Buffer buff = buffer(sbody);
      req.response().write(buff);
      bytesSent += buff.length();
      if (bytesSent >= maxBytesStreaming) {
        close();
      }
    }

    public void close() {
      if (log.isTraceEnabled()) log.trace("XHR stream closing listener");
      if (!closed) {
        session.resetListener();
        try {
          req.response().end();
          req.response().close();
          closed = true;
        } catch (IllegalStateException e) {
          // Underlying connection might already be closed - that's fine
        }
      }
    }
  }

}
