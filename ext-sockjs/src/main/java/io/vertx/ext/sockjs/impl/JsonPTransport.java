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
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.routematcher.RouteMatcher;
import io.vertx.ext.sockjs.SockJSServerOptions;
import io.vertx.ext.sockjs.SockJSSocket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
class JsonPTransport extends BaseTransport {

  private static final Logger log = LoggerFactory.getLogger(JsonPTransport.class);

  JsonPTransport(Vertx vertx, RouteMatcher rm, String basePath, final LocalMap<String, Session> sessions, final SockJSServerOptions options,
            final Handler<SockJSSocket> sockHandler) {
    super(vertx, sessions, options);

    String jsonpRE = basePath + COMMON_PATH_ELEMENT_RE + "jsonp";

    rm.getWithRegEx(jsonpRE, new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        if (log.isTraceEnabled()) log.trace("JsonP, get: " + req.uri());
        String callback = req.params().get("callback");
        if (callback == null) {
          callback = req.params().get("c");
          if (callback == null) {
            req.response().setStatusCode(500);
            req.response().writeStringAndEnd("\"callback\" parameter required\n");
            return;
          }
        }

        String sessionID = req.params().get("param0");
        Session session = getSession(options.getSessionTimeout(), options.getHeartbeatPeriod(), sessionID, sockHandler);
        session.setInfo(req.localAddress(), req.remoteAddress(), req.uri(), req.headers());
        session.register(new JsonPListener(req, session, callback));
      }
    });

    String jsonpSendRE = basePath + COMMON_PATH_ELEMENT_RE + "jsonp_send";

    rm.postWithRegEx(jsonpSendRE, new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        if (log.isTraceEnabled()) log.trace("JsonP, post: " + req.uri());
        String sessionID = req.params().get("param0");
        final Session session = sessions.get(sessionID);
        if (session != null && !session.isClosed()) {
          handleSend(req, session);
        } else {
          req.response().setStatusCode(404);
          setJSESSIONID(options, req);
          req.response().end();
        }
      }
    });
  }

  private void handleSend(final HttpServerRequest req, final Session session) {
    req.bodyHandler(new Handler<Buffer>() {

      public void handle(Buffer buff) {
        String body = buff.toString();

        boolean urlEncoded;
        String ct = req.headers().get("content-type");
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(ct)) {
          urlEncoded = true;
        } else if ("text/plain".equalsIgnoreCase(ct)) {
          urlEncoded = false;
        } else {
          req.response().setStatusCode(500);
          req.response().writeStringAndEnd("Invalid Content-Type");
          return;
        }

        if (body.equals("") || urlEncoded && (!body.startsWith("d=") || body.length() <= 2)) {
          req.response().setStatusCode(500);
          req.response().writeStringAndEnd("Payload expected.");
          return;
        }

        if (urlEncoded) {
          try {
            body = URLDecoder.decode(body, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No UTF-8!");
          }
          body = body.substring(2);
        }

        if (!session.handleMessages(body)) {
          sendInvalidJSON(req.response());
        } else {
          setJSESSIONID(options, req);
          req.response().headers().set("Content-Type", "text/plain; charset=UTF-8");
          setNoCacheHeaders(req);
          req.response().writeStringAndEnd("ok");
          if (log.isTraceEnabled()) log.trace("send handled ok");
        }
      }
    });
  }

  private class JsonPListener extends BaseListener {

    final String callback;
    boolean headersWritten;
    boolean closed;

    JsonPListener(HttpServerRequest req, Session session, String callback) {
      super(req, session);
      this.callback = callback;
      addCloseHandler(req.response(), session);
    }


    public void sendFrame(String body) {

      if (log.isTraceEnabled()) log.trace("JsonP, sending frame");

      if (!headersWritten) {
        req.response().setChunked(true);
        req.response().headers().set("Content-Type", "application/javascript; charset=UTF-8");
        setNoCacheHeaders(req);
        setJSESSIONID(options, req);
        headersWritten = true;
      }

      body = escapeForJavaScript(body);

      StringBuilder sb = new StringBuilder();
      sb.append(callback).append("(\"");
      sb.append(body);
      sb.append("\");\r\n");

      //End the response and close the HTTP connection

      req.response().writeString(sb.toString());
      close();
    }

    public void close() {
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
}
