# Violation Comments to GitLab Maven Plugin [![Build Status](https://travis-ci.org/tomasbjerre/violation-comments-to-gitlab-maven-plugin.svg?branch=master)](https://travis-ci.org/tomasbjerre/violation-comments-to-gitlab-maven-plugin) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-to-gitlab-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/violation-comments-to-gitlab-maven-plugin)

This is a Gradle plugin for [Violation Comments to GitLab Lib](https://github.com/tomasbjerre/violation-comments-to-gitlab-lib).

It can parse results from static code analysis and comment merge requests in GitLab with them.

You can have a look at [violations-test](https://gitlab.com/tomas.bjerre85/violations-test/merge_requests/1) to see what the result may look like.

The **merge must be performed first** in order for the commented lines in the MR to match the lines reported by the analysis tools!

It supports:
 * [_AndroidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/)
   * [_Detekt_](https://github.com/arturbosch/detekt) with `--output-format xml`.
   * [_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`.
   * [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer) with `phpcs api.php --report=checkstyle`.
 * [_CLang_](https://clang-analyzer.llvm.org/)
   * [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/) with `rubycop -f clang file.rb`
 * [_CodeNarc_](http://codenarc.sourceforge.net/)
 * [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/)
   * [_AnsibleLint_](https://github.com/willthames/ansible-lint) with `-p`
   * [_Mccabe_](https://pypi.python.org/pypi/mccabe)
   * [_Pep8_](https://github.com/PyCQA/pycodestyle)
   * [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)
 * [_GoLint_](https://github.com/golang/lint)
   * [_GoVet_](https://golang.org/cmd/vet/) Same format as GoLint.
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)
 * [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)
 * [_MyPy_](https://pypi.python.org/pypi/mypy-lang)
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)
 * [_PyLint_](https://www.pylint.org/)
 * [_PMD_](https://pmd.github.io/)
   * [_Infer_](http://fbinfer.com/) Facebook Infer. With `--pmd-xml`.
   * [_PHPPMD_](https://phpmd.org/) with `phpmd api.php xml ruleset.xml`.
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_SbtScalac_](http://www.scala-sbt.org/)
 * [_Simian_](http://www.harukizaemon.com/simian/)
 * [_StyleCop_](https://stylecop.codeplex.com/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)
 * [_ZPTLint_](https://pypi.python.org/pypi/zptlint)

 
## Usage ##
There is a running example [here](https://github.com/tomasbjerre/violation-comments-to-gitlab-maven-plugin/tree/master/violation-comments-to-gitlab-maven-plugin-example).

Here is and example: 

```
	<plugin>
		<groupId>se.bjurr.violations</groupId>
		<artifactId>violation-comments-to-gitlab-maven-plugin</artifactId>
		<version>1.0</version>
		<executions>
			<execution>
				<id>ViolationCommentsToGitLab</id>
				<goals>
					<goal>violation-comments</goal>
				</goals>
				<configuration>
					<gitLabUrl>${GITLAB_URL}</gitLabUrl>
					<mergeRequestId>${GITLAB_MERGEREQUESTID}</mergeRequestId>
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
