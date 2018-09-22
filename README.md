# Violation Comments to GitLab Maven Plugin [![Build Status](https://travis-ci.org/tomasbjerre/violation-comments-to-gitlab-maven-plugin.svg?branch=master)](https://travis-ci.org/tomasbjerre/violation-comments-to-gitlab-maven-plugin) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-to-gitlab-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-to-gitlab-maven-plugin) [ ![Bintray](https://api.bintray.com/packages/tomasbjerre/tomasbjerre/se.bjurr.violations%3Aviolation-comments-to-gitlab-maven-plugin/images/download.svg) ](https://bintray.com/tomasbjerre/tomasbjerre/se.bjurr.violations%3Aviolation-comments-to-gitlab-maven-plugin/_latestVersion)
This is a Gradle plugin for [Violation Comments to GitLab Lib](https://github.com/tomasbjerre/violation-comments-to-gitlab-lib).

It can parse results from static code analysis and comment merge requests in GitLab with them.

The **merge must be performed first** in order for the commented lines in the MR to match the lines reported by the analysis tools!

Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Parser             | Reporter                                                           | Notes
| ---                | ---                                                                | ---
| `ANDROIDLINT`      | [_AndroidLint_](http://developer.android.com/tools/help/lint.html) |
| `CHECKSTYLE`       | [_Checkstyle_](http://checkstyle.sourceforge.net/)                 |
|                    | [_Detekt_](https://github.com/arturbosch/detekt)                   | with `--output-format xml`.
|                    | [_ESLint_](https://github.com/sindresorhus/grunt-eslint)           | with `format: 'checkstyle'`.
|                    | [_KTLint_](https://github.com/shyiko/ktlint)                       |
|                    | [_SwiftLint_](https://github.com/realm/SwiftLint)                  | with `--reporter checkstyle`.
|                    | [_TSLint_](https://palantir.github.io/tslint/usage/cli/)           | with `-t checkstyle`
|                    | [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer)            | with `phpcs api.php --report=checkstyle`.
| `CLANG`            | [_CLang_](https://clang-analyzer.llvm.org/)                        |
|                    | [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/)   | with `rubycop -f clang file.rb`
|                    |  [_GCC_](https://gcc.gnu.org/)
|                    | [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)
|                    | [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)
| `CODENARC`         | [_CodeNarc_](http://codenarc.sourceforge.net/)
| `CPD`              | [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)
| `CPPLINT`          | [_CPPLint_](https://github.com/theandrewdavis/cpplint)
| `CPPCHECK`         | [_CPPCheck_](http://cppcheck.sourceforge.net/)
| `CSSLINT`          | [_CSSLint_](https://github.com/CSSLint/csslint)
| `DOCFX`            | [_DocFX_](http://dotnet.github.io/docfx/)
| `FINDBUGS`         | [_Findbugs_](http://findbugs.sourceforge.net/)
|                    | [_Spotbugs_](https://spotbugs.github.io/)
| `FLAKE8`           | [_Flake8_](http://flake8.readthedocs.org/en/latest/)
|                    | [_AnsibleLint_](https://github.com/willthames/ansible-lint)        | with `-p`
|                    | [_Mccabe_](https://pypi.python.org/pypi/mccabe)
|                    | [_Pep8_](https://github.com/PyCQA/pycodestyle)
|                    |  [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)
| `FXCOP`            | [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
| `GENDARME`         | [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)
| `GOLINT`           | [_GoLint_](https://github.com/golang/lint)
|                    |  [_GoVet_](https://golang.org/cmd/vet/)                            | Same format as GoLint.
| `GOOGLEERRORPRONE` | [_GoogleErrorProne_](https://github.com/google/error-prone)
|                    |  [_NullAway_](https://github.com/uber/NullAway)                    | Same format as Google Error Prone.
| `JSHINT`           | [_JSHint_](http://jshint.com/)
| `LINT`             | _Lint_                                                             | A common XML format, used by different linters.
| `JCREPORT`         | [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)
| `KLOCWORK`         | [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)
| `KOTLINMAVEN`      | [_KotlinMaven_](https://github.com/JetBrains/kotlin)               | Output from Kotlin Maven Plugin.
| `KOTLINGRADLE`     | [_KotlinGradle_](https://github.com/JetBrains/kotlin)              | Output from Kotlin Gradle Plugin.
| `MYPY`             | [_MyPy_](https://pypi.python.org/pypi/mypy-lang)
| `PCLINT`           | [_PCLint_](http://www.gimpel.com/html/pcl.htm)                     | PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
| `PERLCRITIC`       | [_PerlCritic_](https://github.com/Perl-Critic)
| `PITEST`           | [_PiTest_](http://pitest.org/)
| `PYDOCSTYLE`       | [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)
| `PYLINT`           | [_PyLint_](https://www.pylint.org/)                                | with `pylint --output-format=parseable`.
| `PMD`              | [_PMD_](https://pmd.github.io/)
|                    |  [_Infer_](http://fbinfer.com/)                                    | Facebook Infer. With `--pmd-xml`.
|                    |  [_PHPPMD_](https://phpmd.org/)                                    | with `phpmd api.php xml ruleset.xml`.
| `RESHARPER`        | [_ReSharper_](https://www.jetbrains.com/resharper/)
| `SBTSCALAC`        | [_SbtScalac_](http://www.scala-sbt.org/)
| `SIMIAN`           | [_Simian_](http://www.harukizaemon.com/simian/)
| `STYLECOP`         | [_StyleCop_](https://stylecop.codeplex.com/)
| `XMLLINT`          | [_XMLLint_](http://xmlsoft.org/xmllint.html)
| `YAMLLINT`         | [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html) | with `-f parsable`
| `ZPTLINT`          | [_ZPTLint_](https://pypi.python.org/pypi/zptlint)

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!
 
## Usage ##
There is a running example [here](https://github.com/tomasbjerre/violation-comments-to-gitlab-maven-plugin/tree/master/violation-comments-to-gitlab-maven-plugin-example).

Here is and example: 

```
	<plugin>
		<groupId>se.bjurr.violations</groupId>
		<artifactId>violation-comments-to-gitlab-maven-plugin</artifactId>
		<version>X</version>
		<executions>
			<execution>
				<id>ViolationCommentsToGitLab</id>
				<goals>
					<goal>violation-comments</goal>
				</goals>
				<configuration>
					<gitLabUrl>${GITLAB_URL}</gitLabUrl>
					<mergeRequestIid>${GITLAB_mergeRequestIid}</mergeRequestIid>
					<projectId>${GITLAB_PROJECTID}</projectId>
					<commentOnlyChangedContent>true</commentOnlyChangedContent>
					<createCommentWithAllSingleFileComments>true</createCommentWithAllSingleFileComments>
					<keepOldComments>false</keepOldComments>
					<minSeverity>INFO</minSeverity>
					<apiTokenPrivate>true</apiTokenPrivate>
					<apiToken>${GITLAB_APITOKEN}</apiToken>
					<authMethodHeader>true</authMethodHeader>
					<ignoreCertificateErrors>true</ignoreCertificateErrors>
					<shouldSetWip>false</shouldSetWip>
					<violations>
						<violation>
							<parser>FINDBUGS</parser>
							<reporter>Findbugs</reporter>
							<folder>.</folder>
							<pattern>.*/findbugs/.*\.xml$</pattern>
						</violation>
						<violation>
							<parser>PMD</parser>
							<reporter>PMD</reporter>
							<folder>.</folder>
							<pattern>.*/pmd/.*\.xml$</pattern>
						</violation>
						<violation>
							<parser>CHECKSTYLE</parser>
							<reporter>Checkstyle</reporter>
							<folder>.</folder>
							<pattern>.*/checkstyle/.*\.xml$</pattern>
						</violation>
						<violation>
							<parser>JSHINT</parser>
							<reporter>JSHint</reporter>
							<folder>.</folder>
							<pattern>.*/jshint/.*\.xml$</pattern>
						</violation>
						<violation>
							<parser>CSSLINT</parser>
							<reporter>CSSLint</reporter>
							<folder>.</folder>
							<pattern>.*/csslint/.*\.xml$</pattern>
						</violation>
					</violations>
				</configuration>
			</execution>
		</executions>
	</plugin>
```

You may also have a look at [Violations Lib](https://github.com/tomasbjerre/violations-lib).

## Developer instructions

To make a release, first run:
```
mvn release:prepare -DperformRelease=true
mvn release:perform
```
Then release the artifact from [staging](https://oss.sonatype.org/#stagingRepositories). More information [here](http://central.sonatype.org/pages/releasing-the-deployment.html).
