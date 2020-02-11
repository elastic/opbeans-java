#!/usr/bin/env groovy
@Library('apm@current') _

pipeline {
  agent { label 'linux && immutable' }
  environment {
    REPO = 'opbeans-java'
    BASE_DIR = "src/github.com/elastic/${env.REPO}"
    NOTIFY_TO = credentials('notify-to')
    JOB_GCS_BUCKET = credentials('gcs-bucket')
    JOB_GIT_CREDENTIALS = '2a9602aa-ab9f-4e52-baf3-b71ca88469c7-UserAndToken'
    PIPELINE_LOG_LEVEL = 'INFO'
    BUILD_OPTS = "${params.BUILD_OPTS}"
    DETAILS_ARTIFACT_URL = "${env.BUILD_URL}artifact/${env.DETAILS_ARTIFACT}"
  }
  options {
    timeout(time: 1, unit: 'HOURS')
    timestamps()
    ansiColor('xterm')
    disableResume()
    durabilityHint('PERFORMANCE_OPTIMIZED')
    rateLimitBuilds(throttle: [count: 60, durationName: 'hour', userBoost: true])
  }
  parameters {
    string(name: 'BUILD_OPTS', defaultValue: '', description: 'Build options to build and test the opbeans project')
    string(name: 'GITHUB_CHECK_NAME', defaultValue: '', description: 'Name of the GitHub check to be updated. Only if this build is triggered from another parent stream.')
    string(name: 'GITHUB_CHECK_REPO', defaultValue: '', description: 'Name of the GitHub repo to be updated. Only if this build is triggered from another parent stream.')
    string(name: 'GITHUB_CHECK_SHA1', defaultValue: '', description: 'Name of the GitHub repo to be updated. Only if this build is triggered from another parent stream.')
  }
  stages{
    stage('Checkout'){
      options {
        skipDefaultCheckout()
      }
      steps {
        githubCheckNotify('PENDING')
        deleteDir()
        gitCheckout(basedir: "${BASE_DIR}")
        stash allowEmpty: true, name: 'source', useDefaultExcludes: false
      }
    }
    stage('Build') {
      options {
        skipDefaultCheckout()
      }
      steps {
        deleteDir()
        unstash 'source'
        dir("${env.BASE_DIR}"){
          sh "${env.BUILD_OPTS} make build"
        }
      }
    }
    stage('Test') {
      options {
        skipDefaultCheckout()
      }
      steps {
        deleteDir()
        unstash 'source'
        dir("${env.BASE_DIR}"){
          sh "${env.BUILD_OPTS} make test"
        }
      }
      post {
        always {
          junit(allowEmptyResults: true, keepLongStdio: true, testResults: "${env.BASE_DIR}/**/junit-*.xml")
        }
      }
    }
  }
  post {
    cleanup {
      githubCheckNotify(currentBuild.currentResult == 'SUCCESS' ? 'SUCCESS' : 'FAILURE')
      notifyBuildResult()
    }
  }
}

/**
 Notify the GitHub check of the parent stream
**/
def githubCheckNotify(String status) {
  if (params.GITHUB_CHECK_NAME?.trim() && params.GITHUB_CHECK_REPO?.trim() && params.GITHUB_CHECK_SHA1?.trim()) {
    githubNotify context: "${params.GITHUB_CHECK_NAME}",
                 description: "${params.GITHUB_CHECK_NAME} ${status.toLowerCase()}",
                 status: "${status}",
                 targetUrl: "${env.RUN_DISPLAY_URL}",
                 sha: params.GITHUB_CHECK_SHA1, account: 'elastic', repo: params.GITHUB_CHECK_REPO, credentialsId: env.JOB_GIT_CREDENTIALS
  }
}
