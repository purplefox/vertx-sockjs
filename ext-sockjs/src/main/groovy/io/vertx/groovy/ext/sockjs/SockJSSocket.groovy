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
import io.vertx.groovy.core.Headers
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.streams.WriteStream
import io.vertx.groovy.core.streams.ReadStream
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
public class SockJSSocket implements ReadStream<SockJSSocket>,  WriteStream<SockJSSocket> {
  final def io.vertx.ext.sockjs.SockJSSocket delegate;
  public SockJSSocket(io.vertx.ext.sockjs.SockJSSocket delegate) {
    this.delegate = delegate;
  }
  public io.vertx.ext.sockjs.SockJSSocket getDelegate() {
    return delegate;
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
    def ret= new SocketAddress(this.delegate.remoteAddress());
    return ret;
  }
  /**
   * Return the local address for this socket
   */
  public SocketAddress localAddress() {
    def ret= new SocketAddress(this.delegate.localAddress());
    return ret;
  }
  /**
   * Return the headers corresponding to the last request for this socket or the websocket handshake
   * Any cookie headers will be removed for security reasons
   */
  public Headers headers() {
    def ret= new Headers(this.delegate.headers());
    return ret;
  }
  /**
   * Return the URI corresponding to the last request for this socket or the websocket handshake
   */
  public String uri() {
    def ret = this.delegate.uri();
    return ret;
  }
  public SockJSSocket dataHandler(Handler<Buffer> arg0) {
    ((io.vertx.core.streams.ReadStream) this.delegate).dataHandler(new Handler<io.vertx.core.buffer.Buffer>() {
      public void handle(io.vertx.core.buffer.Buffer event) {
        arg0.handle(new Buffer(event));
      }
    });
    return this;
  }
  public SockJSSocket pause() {
    ((io.vertx.core.streams.ReadStream) this.delegate).pause();
    return this;
  }
  public SockJSSocket resume() {
    ((io.vertx.core.streams.ReadStream) this.delegate).resume();
    return this;
  }
  public SockJSSocket endHandler(Handler<Void> arg0) {
    ((io.vertx.core.streams.ReadStream) this.delegate).endHandler(arg0);
    return this;
  }
  public SockJSSocket exceptionHandler(Handler<Throwable> arg0) {
    ((io.vertx.core.streams.StreamBase) this.delegate).exceptionHandler(arg0);
    return this;
  }
  public SockJSSocket writeBuffer(Buffer arg0) {
    ((io.vertx.core.streams.WriteStream) this.delegate).writeBuffer(arg0.getDelegate());
    return this;
  }
  public SockJSSocket setWriteQueueMaxSize(int arg0) {
    ((io.vertx.core.streams.WriteStream) this.delegate).setWriteQueueMaxSize(arg0);
    return this;
  }
  public boolean writeQueueFull() {
    def ret = ((io.vertx.core.streams.WriteStream) this.delegate).writeQueueFull();
    return ret;
  }
  public SockJSSocket drainHandler(Handler<Void> arg0) {
    ((io.vertx.core.streams.WriteStream) this.delegate).drainHandler(arg0);
    return this;
  }
}
