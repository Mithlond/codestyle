'use strict';

angular.module('github_light', ['ngResource'])
        .filter('releaseNumberFilter', [function () {
            return function (value) {
                if(!value) {
                    return '';
                }

                // Cut out the version constant from this tag, which is expected to
                // be on the form [text-version].
                var lastDashIndex = value.lastIndexOf('-');
                if(lastDashIndex != -1) {

                    // Cut out the version number, and return it.
                    return value.substring(lastDashIndex + 1);
                }

                // Unknown
                return value;
            };
        }])
        .factory('tagService', ['$resource', function ($resource) {
            return $resource(
                    'https://api.github.com/repos/:owner/:name/:operation',
                    {
                        owner: '@owner',
                        name: '@name',
                        operation: '@operation'
                    },
                    {
                        list: {method: 'GET', params: {operation: 'tags'}, isArray: true}
                    }
            );
        }])
        .factory('docsService', ['tagService', function (tagService) {

            // Internal state
            var toReturn;
            var repoDefinition = function () {

                // Shared state
                this.owner = 'unknown';
                this.name = 'unknown';
                this.tagList = [];
                this.completed = false;

                // Public API
                this.init = function (repoOwner, repoName) {
                    this.owner = repoOwner;
                    this.name = repoName;

                    // Find the tag list
                    this.tagList = tagService.list({prop: 'value'}, {owner: this.owner, name: this.name}, function () {
                        toReturn.completed = true;
                    });

                    // All done.
                    return this;
                };

                this.hasTags = function () {
                    return this.tagList.length > 0;
                };
            };

            // All done.
            toReturn = new repoDefinition('unknown', 'unknown');
            return toReturn;
        }]).controller('mithlondRepoController', ['$scope', '$routeParams', '$resource', 'docsService',
            function ($scope, $routeParams, $resource, docsService) {

                // Bind the repoDefinition in the scope
                $scope.repo = docsService.init('Mithlond', 'mithlond-codestyle');
            }]);