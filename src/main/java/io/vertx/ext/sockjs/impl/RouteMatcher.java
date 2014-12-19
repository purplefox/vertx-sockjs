/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.sockjs.impl;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface RouteMatcher {

  static RouteMatcher routeMatcher() {
    return new RouteMatcherImpl();
  }

  @Fluent
  RouteMatcher accept(HttpServerRequest request);

  /**
   * Specify a handler that will be called for a matching request
   * @param method - the HTTP method
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  @Fluent
  RouteMatcher matchMethod(HttpMethod method, String pattern, Handler<HttpServerRequest> handler);

  /**
   * Specify a handler that will be called for all HTTP methods
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  @Fluent
  RouteMatcher all(String pattern, Handler<HttpServerRequest> handler);

  /**
   * Specify a handler that will be called for a matching request
   * @param method - the HTTP method
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  @Fluent
  RouteMatcher matchMethodWithRegEx(HttpMethod method, String pattern, Handler<HttpServerRequest> handler);

  /**
   * Specify a handler that will be called for all HTTP methods
   * @param regex A regular expression
   * @param handler The handler to call
   */
  @Fluent
  RouteMatcher allWithRegEx(String regex, Handler<HttpServerRequest> handler);

  /**
   * Specify a handler that will be called when no other handlers match.
   * If this handler is not specified default behaviour is to return a 404
   */
  @Fluent
  RouteMatcher noMatch(Handler<HttpServerRequest> handler);

}
