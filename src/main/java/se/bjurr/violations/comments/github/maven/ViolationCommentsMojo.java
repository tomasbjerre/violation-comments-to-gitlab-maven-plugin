package se.bjurr.violations.comments.github.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.NONE;
import static org.gitlab.api.AuthMethod.HEADER;
import static org.gitlab.api.AuthMethod.URL_PARAMETER;
import static org.gitlab.api.TokenType.ACCESS_TOKEN;
import static org.gitlab.api.TokenType.PRIVATE_TOKEN;
import static se.bjurr.violations.comments.gitlab.lib.ViolationCommentsToGitLabApi.violationCommentsToGitLabApi;
import static se.bjurr.violations.lib.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.gitlab.api.AuthMethod;
import org.gitlab.api.TokenType;

import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Filtering;

@Mojo(name = "violation-comments", defaultPhase = NONE)
public class ViolationCommentsMojo extends AbstractMojo {

  @Parameter(property = "violations", required = false)
  private final List<ViolationConfig> violations = new ArrayList<ViolationConfig>();

  @Parameter(property = "commentOnlyChangedContent", required = false)
  private final boolean commentOnlyChangedContent = true;

  @Parameter(property = "createCommentWithAllSingleFileComments", required = false)
  private final boolean createCommentWithAllSingleFileComments = true;

  @Parameter(property = "gitLabUrl", required = false)
  private String gitLabUrl;

  @Parameter(property = "apiToken", required = false)
  private String apiToken;

  @Parameter(property = "projectId", required = false)
  private String projectId;

  @Parameter(property = "mergeRequestId", required = false)
  private String mergeRequestId;

  @Parameter(property = "ignoreCertificateErrors", required = false)
  private final Boolean ignoreCertificateErrors = true;

  @Parameter(property = "apiTokenPrivate", required = false)
  private final Boolean apiTokenPrivate = true;

  @Parameter(property = "authMethodHeader", required = false)
  private final Boolean authMethodHeader = true;

  @Parameter(property = "minSeverity", required = false)
  private final SEVERITY minSeverity = INFO;

  @Parameter(property = "keepOldComments", required = false)
  private final Boolean keepOldComments = false;

  @Parameter(property = "shouldSetWip", required = false)
  private final Boolean shouldSetWip = false;

  @Override
  public void execute() throws MojoExecutionException {
    if (mergeRequestId == null || mergeRequestId.isEmpty()) {
      getLog().info("No merge request id defined, will not send violation comments to GitLab.");
      return;
    }

    getLog()
        .info(
            "Will comment project " + projectId + " and MR " + mergeRequestId + " on " + gitLabUrl);

    List<Violation> allParsedViolations = new ArrayList<>();
    for (final ViolationConfig configuredViolation : violations) {

      final List<Violation> parsedViolations =
          violationsReporterApi() //
              .findAll(configuredViolation.getParser()) //
              .inFolder(configuredViolation.getFolder()) //
              .withPattern(configuredViolation.getPattern()) //
              .withReporter(configuredViolation.getReporter()) //
              .violations();
      if (minSeverity != null) {
        allParsedViolations = Filtering.withAtLEastSeverity(allParsedViolations, minSeverity);
      }
      allParsedViolations.addAll(parsedViolations);
    }

    try {
      final TokenType tokenType = apiTokenPrivate ? PRIVATE_TOKEN : ACCESS_TOKEN;
      final AuthMethod authMethod = authMethodHeader ? HEADER : URL_PARAMETER;
      final Integer mergeRequestIdInteger = Integer.parseInt(mergeRequestId);
      violationCommentsToGitLabApi() //
          .setHostUrl(gitLabUrl) //
          .setProjectId(projectId) //
          .setMergeRequestId(mergeRequestIdInteger) //
          .setApiToken(apiToken) //
          .setTokenType(tokenType) //
          .setMethod(authMethod) //
          .setCommentOnlyChangedContent(commentOnlyChangedContent) //
          .setCreateCommentWithAllSingleFileComments(createCommentWithAllSingleFileComments) //
          /**
           * Cannot yet support single file comments because the API does not support it.
           * https://gitlab.com/gitlab-org/gitlab-ce/issues/14850
           */
          .setIgnoreCertificateErrors(ignoreCertificateErrors) //
          .setViolations(allParsedViolations) //
          .setShouldKeepOldComments(keepOldComments) //
          .setShouldSetWIP(shouldSetWip) //
          .toPullRequest();
    } catch (final Exception e) {
      getLog().error("", e);
    }
  }
}