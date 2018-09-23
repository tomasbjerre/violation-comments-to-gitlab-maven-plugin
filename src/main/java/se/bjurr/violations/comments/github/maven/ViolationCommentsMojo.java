package se.bjurr.violations.comments.github.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.NONE;
import static org.gitlab.api.AuthMethod.HEADER;
import static org.gitlab.api.AuthMethod.URL_PARAMETER;
import static org.gitlab.api.TokenType.ACCESS_TOKEN;
import static org.gitlab.api.TokenType.PRIVATE_TOKEN;
import static se.bjurr.violations.comments.gitlab.lib.ViolationCommentsToGitLabApi.violationCommentsToGitLabApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;

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
  private List<ViolationConfig> violations;

  @Parameter(property = "commentOnlyChangedContent", required = false, defaultValue = "true")
  private boolean commentOnlyChangedContent;

  @Parameter(
      property = "createCommentWithAllSingleFileComments",
      required = false,
      defaultValue = "true")
  private boolean createCommentWithAllSingleFileComments;

  @Parameter(property = "gitLabUrl", required = false)
  private String gitLabUrl;

  @Parameter(property = "apiToken", required = false)
  private String apiToken;

  @Parameter(property = "projectId", required = false)
  private String projectId;

  @Parameter(property = "mergeRequestIid", required = false)
  private String mergeRequestIid;

  @Parameter(property = "ignoreCertificateErrors", required = false, defaultValue = "true")
  private Boolean ignoreCertificateErrors;

  @Parameter(property = "apiTokenPrivate", required = false, defaultValue = "true")
  private Boolean apiTokenPrivate;

  @Parameter(property = "authMethodHeader", required = false, defaultValue = "true")
  private Boolean authMethodHeader;

  @Parameter(property = "minSeverity", required = false, defaultValue = "INFO")
  private SEVERITY minSeverity;

  @Parameter(property = "keepOldComments", required = false)
  private Boolean keepOldComments;

  @Parameter(property = "shouldSetWip", required = false)
  private Boolean shouldSetWip;

  @Parameter(property = "commentTemplate", required = false)
  private String commentTemplate;

  @Override
  public void execute() throws MojoExecutionException {
    if (mergeRequestIid == null || mergeRequestIid.isEmpty()) {
      getLog().info("No merge request id defined, will not send violation comments to GitLab.");
      return;
    }

    if (violations == null || violations.isEmpty()) {
      getLog().info("No violations configured.");
      return;
    }

    getLog()
        .info(
            "Will comment project "
                + projectId
                + " and MR "
                + mergeRequestIid
                + " on "
                + gitLabUrl);

    List<Violation> allParsedViolations = new ArrayList<>();
    for (final ViolationConfig configuredViolation : violations) {

      final List<Violation> parsedViolations =
          violationsApi() //
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
      final Integer mergeRequestIidInteger = Integer.parseInt(mergeRequestIid);
      violationCommentsToGitLabApi() //
          .setHostUrl(gitLabUrl) //
          .setProjectId(projectId) //
          .setMergeRequestIid(mergeRequestIidInteger) //
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
          .withCommentTemplate(commentTemplate) //
          .toPullRequest();
    } catch (final Exception e) {
      getLog().error(e.getMessage(), e);
    }
  }
}
