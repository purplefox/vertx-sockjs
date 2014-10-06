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
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.core.streams.ReadStream
import io.vertx.groovy.core.MultiMap
import io.vertx.core.Handler
import io.vertx.groovy.core.net.SocketAddress
/**
 *
 * You interact with SockJS clients through instances of SockJS socket.<p>
 * The API is very similar to {@link io.vertx.core.http.WebSocket}.
 * It implements both {@link ReadStream} and {@link WriteStream} so it can be used with
 * {@link io.vertx.core.streams.Pump} to pump data with flow control.<p>
 * Instances of this class are not thread-safe.<p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@CompileStatic
public class SockJSSocket implements ReadStream<Buffer>,  WriteStream<Buffer> {
  final def io.vertx.ext.sockjs.SockJSSocket delegate;
  public SockJSSocket(io.vertx.ext.sockjs.SockJSSocket delegate) {
    this.delegate = delegate;
  }
  public Object getDelegate() {
    return delegate;
  }
  public boolean writeQueueFull() {
    def ret = ((io.vertx.core.streams.WriteStream) this.delegate).writeQueueFull();
    return ret;
  }
  public SockJSSocket exceptionHandler(Handler<Throwable> handler) {
    this.delegate.exceptionHandler(handler);
    return this;
  }
  public SockJSSocket handler(Handler<Buffer> handler) {
    this.delegate.handler(new Handler<io.vertx.core.buffer.Buffer>() {
      public void handle(io.vertx.core.buffer.Buffer event) {
        handler.handle(Buffer.FACTORY.apply(event));
      }
    });
    return this;
  }
  public SockJSSocket pause() {
    this.delegate.pause();
    return this;
  }
  public SockJSSocket resume() {
    this.delegate.resume();
    return this;
  }
  public SockJSSocket endHandler(Handler<Void> endHandler) {
    this.delegate.endHandler(endHandler);
    return this;
  }
  public SockJSSocket write(Buffer data) {
    this.delegate.write((io.vertx.core.buffer.Buffer)data.getDelegate());
    return this;
  }
  public SockJSSocket setWriteQueueMaxSize(int maxSize) {
    this.delegate.setWriteQueueMaxSize(maxSize);
    return this;
  }
  public SockJSSocket drainHandler(Handler<Void> handler) {
    this.delegate.drainHandler(handler);
    return this;
  }
  /**
   * When a {@code SockJSSocket} is created it automatically registers an event handler with the event bus, the ID of that
   * handler is given by {@code writeHandlerID}.<p>
   * Given this ID, a different event loop can send a buffer to that event handler using the event bus and
   * that buffer will be received by this instance in its own event loop and written to the underlying socket. This
   * allows you to write data to other sockets which are owned by different event loops.
   */
  public String writeHandlerID() {
    def ret = this.delegate.writeHandlerID();
    return ret;
  }
  /**
   * Close it
   */
  public void close() {
    this.delegate.close();
  }
  /**
   * Return the remote address for this socket
   */
  public SocketAddress remoteAddress() {
    def ret= SocketAddress.FACTORY.apply(this.delegate.remoteAddress());
    return ret;
  }
  /**
   * Return the local address for this socket
   */
  public SocketAddress localAddress() {
    def ret= SocketAddress.FACTORY.apply(this.delegate.localAddress());
    return ret;
  }
  /**
   * Return the headers corresponding to the last request for this socket or the websocket handshake
   * Any cookie headers will be removed for security reasons
   */
  public MultiMap headers() {
    def ret= MultiMap.FACTORY.apply(this.delegate.headers());
    return ret;
  }
  /**
   * Return the URI corresponding to the last request for this socket or the websocket handshake
   */
  public String uri() {
    def ret = this.delegate.uri();
    return ret;
  }

  static final java.util.function.Function<io.vertx.ext.sockjs.SockJSSocket, SockJSSocket> FACTORY = io.vertx.lang.groovy.Factories.createFactory() {
    io.vertx.ext.sockjs.SockJSSocket arg -> new SockJSSocket(arg);
  };
}
