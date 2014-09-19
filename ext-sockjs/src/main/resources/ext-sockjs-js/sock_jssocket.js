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
    var __args = arguments;
    if (__args.length === 0) {
      return j_sockJSSocket.writeHandlerID();
    } else utils.invalidArgs();
  };

  /*
   Close it
  
  */
  this.close = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSSocket.close();
    } else utils.invalidArgs();
  };

  /*
   Return the remote address for this socket
  
  */
  this.remoteAddress = function() {
    var __args = arguments;
    if (__args.length === 0) {
      return new SocketAddress(j_sockJSSocket.remoteAddress());
    } else utils.invalidArgs();
  };

  /*
   Return the local address for this socket
  
  */
  this.localAddress = function() {
    var __args = arguments;
    if (__args.length === 0) {
      return new SocketAddress(j_sockJSSocket.localAddress());
    } else utils.invalidArgs();
  };

  /*
   Return the headers corresponding to the last request for this socket or the websocket handshake
   Any cookie headers will be removed for security reasons
  
  */
  this.headers = function() {
    var __args = arguments;
    if (__args.length === 0) {
      return new Headers(j_sockJSSocket.headers());
    } else utils.invalidArgs();
  };

  /*
   Return the URI corresponding to the last request for this socket or the websocket handshake
  
  */
  this.uri = function() {
    var __args = arguments;
    if (__args.length === 0) {
      return j_sockJSSocket.uri();
    } else utils.invalidArgs();
  };

  this.handler = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_sockJSSocket.handler(function(jVal) {
      arg0(new Buffer(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  this.pause = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSSocket.pause();
      return that;
    } else utils.invalidArgs();
  };

  this.resume = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSSocket.resume();
      return that;
    } else utils.invalidArgs();
  };

  this.endHandler = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_sockJSSocket.endHandler(arg0);
      return that;
    } else utils.invalidArgs();
  };

  this.exceptionHandler = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_sockJSSocket.exceptionHandler(function(jVal) {
      arg0(new Throwable(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  this.write = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'object' && __args[0]._vertxgen) {
      j_sockJSSocket.write(arg0._jdel());
      return that;
    } else utils.invalidArgs();
  };

  this.setWriteQueueMaxSize = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] ==='number') {
      j_sockJSSocket.setWriteQueueMaxSize(arg0);
      return that;
    } else utils.invalidArgs();
  };

  this.writeQueueFull = function() {
    var __args = arguments;
    if (__args.length === 0) {
      return j_sockJSSocket.writeQueueFull();
    } else utils.invalidArgs();
  };

  this.drainHandler = function(arg0) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_sockJSSocket.drainHandler(arg0);
      return that;
    } else utils.invalidArgs();
  };

  this._vertxgen = true;

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_sockJSSocket;
  }

};

// We export the Constructor function
module.exports = SockJSSocket;