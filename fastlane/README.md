fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
### build_open_source_demo
```
fastlane build_open_source_demo
```
Build open source demo app without signing
### publish_open_source_sdk
```
fastlane publish_open_source_sdk
```
Lane to create Release on GitHub. Task create release, add tags, apply release notes and publish library.

On android publish to Github packages. On iOS publish to Cocoapods

Default without options run on iOS.

To run on android add option: --env android

To publish version with incremented minor or major version, before run this lane run: gradlew bumpMinorVersion or bumpMajorVersion
### build_ipa
```
fastlane build_ipa
```
Builds .ipa for project.

Parameters:

* **`build_number`**: Build number

* **`skip_cocoapods`**: Should Cocoa Pods install be skipped?. (`DL_SKIP_COCOAPODS`)
### sonarqube
```
fastlane sonarqube
```
Runs SonarQube analysys.
### ui_tests
```
fastlane ui_tests
```
Run UI tests
### integration_tests
```
fastlane integration_tests
```
Run integration tests
### unit_tests
```
fastlane unit_tests
```
Run Unit tests
### strict_lint
```
fastlane strict_lint
```
Run code validation, fails on warnings
### publish_to_staging
```
fastlane publish_to_staging
```
Publishes produced .ipa and symbols to staging

Parameters:

* **`release_notes`**: Release notes.

* **`recipients`**: Extension of DL_CRASHLYTICS_EMAILS or DL_CRASHLYTICS_GROUPS environments defining concrete set of recipients, e.g. when = DEV,QA it will read data from DL_CRASHLYTICS_GROUPS_DEV and DL_CRASHLYTICS_GROUPS_QA
### release_app
```
fastlane release_app
```
Release app

* **`build_number`**: Build number
### upload_to_itunes_connect
```
fastlane upload_to_itunes_connect
```

### upload_symbols_to_firebase
```
fastlane upload_symbols_to_firebase
```
Upload symbols to firebase
### refresh_symbols_on_crashlytics
```
fastlane refresh_symbols_on_crashlytics
```
Refresh symbols on crashlytics

* **`build_number`**: Build number
### upload_symbols_to_sentry_io
```
fastlane upload_symbols_to_sentry_io
```
Upload symbols to sentry

* **`dsym_path`**: path do dsym files
### refresh_appstore_symbols_on_sentry
```
fastlane refresh_appstore_symbols_on_sentry
```
Refresh symbols downladed from Appstore on sentry

* **`build_number`**: Build number
### upload_symbols_to_instabug
```
fastlane upload_symbols_to_instabug
```
Upload symbols to instabug
### create_certs
```
fastlane create_certs
```
Creates certificates for given app_identifier. Admin account is required.

Parameters:

* **`type`**: Provisioning profile type. Should be one of following: development, enterprise, app-store, adhoc

* **`username`**: Apple ID with admin rights.

* **`app_id`**: App's/extension identifier. (`DL_APP_IDENTIFIER`)

* **`file_name``**: File name of file containing all of identifiers for batch creation.

* **`team_id`**: Dev account team id. (`DL_APPLEDEV_ACCOUNT_TEAMID`)
### build_pod_static_lib
```
fastlane build_pod_static_lib
```
Builds static library, documentation, podspec and changelog and put it in one directory.

Parameters:

* **`project_name`**: Name of the project. (`DL_PROJECT_NAME`)

* **`scheme`**: XCode scheme of universal library. (`DL_BUILD_SCHEME`)

* **`build_output_path`**: Path to output directory. (`DL_BUILD_OUTPUT_PATH`)
### publish_pod_static_lib
```
fastlane publish_pod_static_lib
```
Publish Cocoapod that is distributed as static library.

Parameters:

* **`project_name`**: Name of the project. (`PROJECT_NAME`)

* **`output_git_repo_path`**: Local path where remote repository will be cloned. Default value is build/remote. (`DL_OUTPUT_GIT_REPO_PATH`)

* **`output_git_repo_url`**: Repository where new version of the library should be pushed. (`DL_OUTPUT_GIT_REPO_URL`)

* **`specs_repository`**: DreamLab podspecs repository identifier. Values: public|private. (`DL_OUTPUT_SPEC_REPOSITORY`)

* **`output_sample_git_repo_path`**: Local path where repository with sample project will be cloned. Default value is build/sample. (`DL_OUTPUT_SAMPLE_GIT_REPO_PATH`)

* **`input_example_git_repo_url`**: Repository with sample project. (`DL_INPUT_EXAMPLE_GIT_REPO_URL`)

* **`build_output_path`**: Path to directory whith output of `build_pod` lane. (`DL_BUILD_OUTPUT_PATH`)
### publish_pod_source
```
fastlane publish_pod_source
```
Publish Cocoapod that is distributed as source code.

Parameters:

* **`project_name`**: Name of the project. (`DL_PROJECT_NAME`)

* **`specs_repository`**: DreamLab podspecs repository identifier. Values: public|private. (`DL_OUTPUT_SPEC_REPOSITORY`)
### publish_changelog
```
fastlane publish_changelog
```
Build changelog and push it ot git repository.
### build_documentation
```
fastlane build_documentation
```
Build Documentation

* **`output_dir`**: Path to output directory.
### init_env
```
fastlane init_env
```
Initialize environment for building.

It fetch the .env.default file.
### build_for_ui_tests
```
fastlane build_for_ui_tests
```

### store_custom_signing_identity
```
fastlane store_custom_signing_identity
```

### store_custom_provisioning_profile
```
fastlane store_custom_provisioning_profile
```

### lint_podspec
```
fastlane lint_podspec
```
Lints Cocoapod using its podspec; Private and allows_warnings options set.

Parameters:

* **`project_name`**: Name of the project. (`DL_PROJECT_NAME`)

* **`lint_options`**: Additional pod_lib_lint options (like --verbose, --fail-fast)

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
