/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.groovy.ext.sockjs;
import groovy.transform.CompileStatic
import io.vertx.lang.groovy.InternalHelper
import io.vertx.ext.sockjs.BridgeOptions
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.core.Vertx
import io.vertx.ext.sockjs.SockJSServerOptions
import io.vertx.core.Handler
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@CompileStatic
public class SockJSServer {
  final def io.vertx.ext.sockjs.SockJSServer delegate;
  public SockJSServer(io.vertx.ext.sockjs.SockJSServer delegate) {
    this.delegate = delegate;
  }
  public io.vertx.ext.sockjs.SockJSServer getDelegate() {
    return delegate;
  }
  public static SockJSServer sockJSServer(Vertx vertx, HttpServer httpServer) {
    def ret= new SockJSServer(io.vertx.ext.sockjs.SockJSServer.sockJSServer(vertx.getDelegate(), httpServer.getDelegate()));
    return ret;
  }
  public SockJSServer installApp(Map<String, Object> options = [:], Handler<SockJSSocket> sockHandler) {
    this.delegate.installApp(options != null ? new io.vertx.ext.sockjs.SockJSServerOptions(new io.vertx.core.json.JsonObject(options)) : null, new Handler<io.vertx.ext.sockjs.SockJSSocket>() {
      public void handle(io.vertx.ext.sockjs.SockJSSocket event) {
        sockHandler.handle(new SockJSSocket(event));
      }
    });
    return this;
  }
  public SockJSServer bridge(Map<String, Object> options = [:], Map<String, Object> bridgeOptions = [:]) {
    this.delegate.bridge(options != null ? new io.vertx.ext.sockjs.SockJSServerOptions(new io.vertx.core.json.JsonObject(options)) : null, bridgeOptions != null ? new io.vertx.ext.sockjs.BridgeOptions(new io.vertx.core.json.JsonObject(bridgeOptions)) : null);
    return this;
  }
  public void close() {
    this.delegate.close();
  }
  public void installTestApplications() {
    this.delegate.installTestApplications();
  }
}
