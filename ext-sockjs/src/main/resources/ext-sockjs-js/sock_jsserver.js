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
var SockJSSocket = require('ext-sockjs-js/sock_jssocket');
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
    j_sockJSServer.installApp(options != null ? SockJSServerOptions.optionsFromJson(new JsonObject(JSON.stringify(options))) : null, function(jVal) {
      sockHandler(new SockJSSocket(jVal));
    });
    return that;
  };

  this.bridge = function(options, bridgeOptions) {
    j_sockJSServer.bridge(options != null ? SockJSServerOptions.optionsFromJson(new JsonObject(JSON.stringify(options))) : null, bridgeOptions != null ? BridgeOptions.optionsFromJson(new JsonObject(JSON.stringify(bridgeOptions))) : null);
    return that;
  };

  this.close = function() {
    j_sockJSServer.close();
  };

  this.installTestApplications = function() {
    j_sockJSServer.installTestApplications();
  };

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_sockJSServer;
  }

};

SockJSServer.sockJSServer = function(vertx, httpServer) {
  return new SockJSServer(JSockJSServer.sockJSServer(vertx._jdel(), httpServer._jdel()));
};

// We export the Constructor function
module.exports = SockJSServer;