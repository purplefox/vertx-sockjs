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
var SockJSSocket = require('ext-sockjs-js/sock_js_socket');
var HttpServer = require('vertx-js/http_server');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JSockJSServer = io.vertx.ext.sockjs.SockJSServer;
var BridgeOptions = io.vertx.ext.sockjs.BridgeOptions;
var SockJSServerOptions = io.vertx.ext.sockjs.SockJSServerOptions;

/**

  @class
*/
var SockJSServer = function(j_val) {

  var j_sockJSServer = j_val;
  var that = this;

  this.installApp = function(options, sockHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'function') {
      j_sockJSServer.installApp(options != null ? new SockJSServerOptions(new JsonObject(JSON.stringify(options))) : null, function(jVal) {
      sockHandler(new SockJSSocket(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  this.bridge = function(options, bridgeOptions) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && typeof __args[1] === 'object') {
      j_sockJSServer.bridge(options != null ? new SockJSServerOptions(new JsonObject(JSON.stringify(options))) : null, bridgeOptions != null ? new BridgeOptions(new JsonObject(JSON.stringify(bridgeOptions))) : null);
      return that;
    } else utils.invalidArgs();
  };

  this.close = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSServer.close();
    } else utils.invalidArgs();
  };

  this.installTestApplications = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_sockJSServer.installTestApplications();
    } else utils.invalidArgs();
  };

  this._vertxgen = true;

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_sockJSServer;
  }

};

SockJSServer.sockJSServer = function(vertx, httpServer) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._vertxgen && typeof __args[1] === 'object' && __args[1]._vertxgen) {
    return new SockJSServer(JSockJSServer.sockJSServer(vertx._jdel(), httpServer._jdel()));
  } else utils.invalidArgs();
};

// We export the Constructor function
module.exports = SockJSServer;