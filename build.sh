#!/bin/bash
mvn versions:update-properties
mvn clean generate-resources eclipse:eclipse install || exit 1
cd violation-comments-to-gitlab-maven-plugin-example
mvn versions:update-properties -DallowSnapshots=true

#
# Get project id: curl -XGET "https://gitlab.com/api/v4/projects/tomas.bjerre85%2Fviolations-test"
# MR: https://gitlab.com/api/v4/projects/2732496/merge_requests
#
mvn violation-comments-to-gitlab:violation-comments -DGITLAB_URL=https://gitlab.com/ -DGITLAB_MERGEREQUESTIID=1 -DGITLAB_PROJECTID=tomas.bjerre85/violations-test -DGITLAB_APITOKEN=$GITLAB_APITOKEN -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -e
