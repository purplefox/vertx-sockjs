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

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows you to do route requests based on the HTTP verb and the request URI, in a manner similar
 * to <a href="http://www.sinatrarb.com/">Sinatra</a> or <a href="http://expressjs.com/">Express</a>.<p>
 * RouteMatcher also lets you extract parameters from the request URI either a simple pattern or using
 * regular expressions for more complex matches. Any parameters extracted will be added to the requests parameters
 * which will be available to you in your request handler.<p>
 * It's particularly useful when writing REST-ful web applications.<p>
 * To use a simple pattern to extract parameters simply prefix the parameter name in the pattern with a ':' (colon).<p>
 * Different handlers can be specified for each of the HTTP verbs, GET, POST, PUT, DELETE etc.<p>
 * For more complex matches regular expressions can be used in the pattern. When regular expressions are used, the extracted
 * parameters do not have a name, so they are put into the HTTP request with names of param0, param1, param2 etc.<p>
 * Multiple matches can be specified for each HTTP verb. In the case there are more than one matching patterns for
 * a particular request, the first matching one will be used.<p>
 * Instances of this class are not thread-safe<p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class RouteMatcherImpl implements RouteMatcher {

  private final Map<HttpMethod, List<PatternBinding>> bindingsMap = new HashMap<>();
  
  private Handler<HttpServerRequest> noMatchHandler;

  /**
   * Do not instantiate this directly - use RouteMatcher.newRouteMatcher() instead
   */
  public RouteMatcherImpl() {
  }

  @Override
  public RouteMatcher accept(HttpServerRequest request) {
    List<PatternBinding> bindings = bindingsMap.get(request.method());
    if (bindings != null) {
      route(request, bindings);
    } else {
      notFound(request);
    }
    return this;
  }

  @Override
  public RouteMatcher matchMethod(HttpMethod method, String pattern, Handler<HttpServerRequest> handler) {
    addPattern(method, pattern, handler);
    return this;
  }

  @Override
  public RouteMatcher matchMethodWithRegEx(HttpMethod method, String regex, Handler<HttpServerRequest> handler) {
    addRegEx(method, regex, handler);
    return this;
  }

  /**
   * Specify a handler that will be called for all HTTP methods
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  @Override
  public RouteMatcher all(String pattern, Handler<HttpServerRequest> handler) {
    addPattern(HttpMethod.GET, pattern, handler);
    addPattern(HttpMethod.PUT, pattern, handler);
    addPattern(HttpMethod.POST, pattern, handler);
    addPattern(HttpMethod.DELETE, pattern, handler);
    addPattern(HttpMethod.OPTIONS, pattern, handler);
    addPattern(HttpMethod.HEAD, pattern, handler);
    addPattern(HttpMethod.TRACE, pattern, handler);
    addPattern(HttpMethod.CONNECT, pattern, handler);
    addPattern(HttpMethod.PATCH, pattern, handler);    
    return this;
  }

  /**
   * Specify a handler that will be called for all HTTP methods
   * @param regex A regular expression
   * @param handler The handler to call
   */
  @Override
  public RouteMatcher allWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    addRegEx(HttpMethod.GET, regex, handler);
    addRegEx(HttpMethod.PUT, regex, handler);
    addRegEx(HttpMethod.POST, regex, handler);
    addRegEx(HttpMethod.DELETE, regex, handler);
    addRegEx(HttpMethod.OPTIONS, regex, handler);
    addRegEx(HttpMethod.HEAD, regex, handler);
    addRegEx(HttpMethod.TRACE, regex, handler);
    addRegEx(HttpMethod.CONNECT, regex, handler);
    addRegEx(HttpMethod.PATCH, regex, handler);
    return this;
  }

  /**
   * Specify a handler that will be called when no other handlers match.
   * If this handler is not specified default behaviour is to return a 404
   */
  @Override
  public RouteMatcher noMatch(Handler<HttpServerRequest> handler) {
    noMatchHandler = handler;
    return this;
  }

  private void addPattern(HttpMethod method, String input, Handler<HttpServerRequest> handler) {
    List<PatternBinding> bindings = getBindings(method);
    // We need to search for any :<token name> tokens in the String and replace them with named capture groups
    Matcher m =  Pattern.compile(":([A-Za-z][A-Za-z0-9_]*)").matcher(input);
    StringBuffer sb = new StringBuffer();
    Set<String> groups = new HashSet<>();
    while (m.find()) {
      String group = m.group().substring(1);
      if (groups.contains(group)) {
        throw new IllegalArgumentException("Cannot use identifier " + group + " more than once in pattern string");
      }
      m.appendReplacement(sb, "(?<$1>[^\\/]+)");
      groups.add(group);
    }
    m.appendTail(sb);
    String regex = sb.toString();
    PatternBinding binding = new PatternBinding(Pattern.compile(regex), groups, handler);
    bindings.add(binding);
  }

  private void addRegEx(HttpMethod method, String input, Handler<HttpServerRequest> handler) {
    List<PatternBinding> bindings = getBindings(method);
    PatternBinding binding = new PatternBinding(Pattern.compile(input), null, handler);
    bindings.add(binding);
  }
  
  private List<PatternBinding> getBindings(HttpMethod method) {
    List<PatternBinding> bindings = bindingsMap.get(method);
    if (bindings == null) {
      bindings = new ArrayList<>();
      bindingsMap.put(method, bindings);
    }
    return bindings;
  }

  private void route(HttpServerRequest request, List<PatternBinding> bindings) {
    for (PatternBinding binding: bindings) {
      Matcher m = binding.pattern.matcher(request.path());
      if (m.matches()) {
        Map<String, String> params = new HashMap<>(m.groupCount());
        if (binding.paramNames != null) {
          // Named params
          for (String param: binding.paramNames) {
            params.put(param, m.group(param));
          }
        } else {
          // Un-named params
          for (int i = 0; i < m.groupCount(); i++) {
            params.put("param" + i, m.group(i + 1));
          }
        }
        request.params().addAll(params);
        binding.handler.handle(request);
        return;
      }
    }
    notFound(request);
  }

  private void notFound(HttpServerRequest request) {
    if (noMatchHandler != null) {
      noMatchHandler.handle(request);
    } else {
      // Default 404
      request.response().setStatusCode(404);
      request.response().end();
    }
  }

  private static class PatternBinding {
    final Pattern pattern;
    final Handler<HttpServerRequest> handler;
    final Set<String> paramNames;

    private PatternBinding(Pattern pattern, Set<String> paramNames, Handler<HttpServerRequest> handler) {
      this.pattern = pattern;
      this.paramNames = paramNames;
      this.handler = handler;
    }
  }

}
