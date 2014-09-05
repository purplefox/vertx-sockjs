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

var utils = require('vertx-js/util/utils');
var Headers = require('vertx-js/headers');
var Buffer = require('vertx-js/buffer');
var WriteStream = require('vertx-js/write_stream');
var ReadStream = require('vertx-js/read_stream');
var SocketAddress = require('vertx-js/socket_address');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JSockJSSocket = io.vertx.ext.sockjs.SockJSSocket;

/**

 You interact with SockJS clients through instances of SockJS socket.<p>
  @class
*/
var SockJSSocket = function(j_val) {

  var j_sockJSSocket = j_val;
  var that = this;
  ReadStream.call(this, j_val);
  WriteStream.call(this, j_val);

  /*
  
  */
  this.writeHandlerID = function() {
    return j_sockJSSocket.writeHandlerID();
  };

  /*
   Close it
  
  */
  this.close = function() {
    j_sockJSSocket.close();
  };

  /*
   Return the remote address for this socket
  
  */
  this.remoteAddress = function() {
    return new SocketAddress(j_sockJSSocket.remoteAddress());
  };

  /*
   Return the local address for this socket
  
  */
  this.localAddress = function() {
    return new SocketAddress(j_sockJSSocket.localAddress());
  };

  /*
   Return the headers corresponding to the last request for this socket or the websocket handshake
   Any cookie headers will be removed for security reasons
  
  */
  this.headers = function() {
    return new Headers(j_sockJSSocket.headers());
  };

  /*
   Return the URI corresponding to the last request for this socket or the websocket handshake
  
  */
  this.uri = function() {
    return j_sockJSSocket.uri();
  };

  this.dataHandler = function(arg0) {
    j_sockJSSocket.dataHandler(function(jVal) {
      arg0(new Buffer(jVal));
    });
    return that;
  };

  this.pause = function() {
    j_sockJSSocket.pause();
    return that;
  };

  this.resume = function() {
    j_sockJSSocket.resume();
    return that;
  };

  this.endHandler = function(arg0) {
    j_sockJSSocket.endHandler(arg0);
    return that;
  };

  this.exceptionHandler = function(arg0) {
    j_sockJSSocket.exceptionHandler(function(jVal) {
      arg0(new Throwable(jVal));
    });
    return that;
  };

  this.writeBuffer = function(arg0) {
    j_sockJSSocket.writeBuffer(arg0._jdel());
    return that;
  };

  this.setWriteQueueMaxSize = function(arg0) {
    j_sockJSSocket.setWriteQueueMaxSize(arg0);
    return that;
  };

  this.writeQueueFull = function() {
    return j_sockJSSocket.writeQueueFull();
  };

  this.drainHandler = function(arg0) {
    j_sockJSSocket.drainHandler(arg0);
    return that;
  };

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_sockJSSocket;
  }

};

// We export the Constructor function
module.exports = SockJSSocket;