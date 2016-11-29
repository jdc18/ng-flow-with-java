/*global angular */
'use strict';

/**
 * The main app module
 * 
 * @name upload
 * @type {angular.Module}
 */
(function() {
	/*
	 * You can use CORS changing the targe be sure to specify the domain in the UploadServlet file (java)
	 */
	var upload = angular.module('UploadModule', [,'flow' ]).config(
			[ 'flowFactoryProvider', function(flowFactoryProvider) {
				flowFactoryProvider.defaults = {
					target : '/ng-flow-java/upload',
					permanentErrors : [ 500, 501 ],
					maxChunkRetries : 1,
					chunkRetryInterval : 5000,
					forceChunkSize : true,
					simultaneousUploads : 4,
					progressCallbacksInterval : 1,
					withCredentials : true,
					method : "octet"
				};
				flowFactoryProvider.on('catchAll', function(event) {
					console.log('catchAll', arguments);
				});
				// Can be used with different implementations of Flow.js
				// flowFactoryProvider.factory = fustyFlowFactory;
			} ]);

	upload.controller('ButtonController', function() {
		this.pausa = false;
		this.cancel = false;
				
		this.estadoPausa = function (file){
			if(file.paused || file.isComplete()){
				this.pause = false;
				return this.pause;
			}
			
			if(file.isUploading()){
				this.pause = true;
				return this.pause;
			}
			
		}
		this.estadoCancel = function (file){
			file.isUploading();
			return this.subido;
		}
		
	});
	
})();