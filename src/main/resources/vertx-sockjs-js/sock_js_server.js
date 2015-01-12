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

/** @module vertx-sockjs-js/sock_js_server */
var utils = require('vertx-js/util/utils');
var SockJSSocket = require('vertx-sockjs-js/sock_js_socket');
var HttpServer = require('vertx-js/http_server');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JSockJSServer = io.vertx.ext.sockjs.SockJSServer;
var SockJSServerOptions = io.vertx.ext.sockjs.SockJSServerOptions;
var SockJSServerOptions = io.vertx.ext.sockjs.SockJSServerOptions;
var BridgeOptions = io.vertx.ext.sockjs.BridgeOptions;

/**

 @class
*/
var SockJSServer = function(j_val) {

  var j_sockJSServer = j_val;
  var that = this;

  /**

   @public
   @param options {Object} 
   @param sockHandler {function} 
   @return {SockJSServer}
   */
  this.installApp = function(options, sockHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_sockJSServer.installApp(options != null ? new SockJSServerOptions(new JsonObject(JSON.stringify(options))) : null, function(jVal) {
      sockHandler(new SockJSSocket(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  /**

   @public
   @param options {Object} 
   @param bridgeOptions {Object} 
   @return {SockJSServer}
   */
  this.bridge = function(options, bridgeOptions) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'object') {
      j_sockJSServer.bridge(options != null ? new SockJSServerOptions(new JsonObject(JSON.stringify(options))) : null, bridgeOptions != null ? new BridgeOptions(new JsonObject(JSON.stringify(bridgeOptions))) : null);
      return that;
    } else utils.invalidArgs();
  };

  /**

   @public

   */
  this.close = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSServer.close();
    } else utils.invalidArgs();
  };

  /**

   @public

   */
  this.installTestApplications = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSServer.installTestApplications();
    } else utils.invalidArgs();
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_sockJSServer;
};

/**

 @memberof module:vertx-sockjs-js/sock_js_server
 @param vertx {Vertx} 
 @param httpServer {HttpServer} 
 @return {SockJSServer}
 */
SockJSServer.sockJSServer = function(vertx, httpServer) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'object' && __args[1]._jdel) {
    return new SockJSServer(JSockJSServer.sockJSServer(vertx._jdel, httpServer._jdel));
  } else utils.invalidArgs();
};

// We export the Constructor function
module.exports = SockJSServer;